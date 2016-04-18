package cmpe295b.watchdog.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * Created by harshad on 4/14/2016.
 */
public class AlarmReceiverLifeLog extends BroadcastReceiver {

    private static final String TAG = "LL24";
    static Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = "Sending Data for Health Monitoring and Prediction";
        String totalCPUUtil="";
        totalCPUUtil=getCpuUsageStatistic().toString();
        SystemInfo info=new SystemInfo();
        // info.totalCPUUtil=totalCPUUtil;


        info.totalCPUUtil=totalCPUUtil;
        info.deviceId="BCDA234";

        SaveAsyncTask tsk = new SaveAsyncTask();
        tsk.execute(info);
       // Toast.makeText(context, totalCPUUtil, Toast.LENGTH_SHORT).show();



       // Pubnub pubnub = new Pubnub("demo", "demo");


        /* Subscribe to the demo_tutorial channel */
        Pubnub pubnub = new Pubnub(
                "demo",  // PUBLISH_KEY   (Optional, supply "" to disable)
                "demo",  // SUBSCRIBE_KEY (Required)
                "",      // SECRET_KEY    (Optional, supply "" to disable)
                "",      // CIPHER_KEY    (Optional, supply "" to disable)
                false    // SSL_ON?
        );


        /* Subscribe to the demo_tutorial channel */
        try {
            pubnub.subscribe("mobilexyz", new Callback() {
                @Override
                public void connectCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : CONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void disconnectCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : DISCONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                public void reconnectCallback(String channel, Object message) {
                    Log.d("PUBNUB", "SUBSCRIBE : RECONNECT on channel:" + channel
                            + " : " + message.getClass() + " : "
                            + message.toString());
                }

                @Override
                public void successCallback(String channel, Object message) {
                    Log.d("PUBNUB","SUBSCRIBE : " + channel + " : "
                            + message.getClass() + " : " + message.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("PUBNUB","SUBSCRIBE : ERROR on channel " + channel
                            + " : " + error.toString());
                }
            });
        } catch (PubnubException e) {
            e.printStackTrace();
        }


        JSONObject data = new JSONObject();
        try {
            data.put("cpuUtil",info.totalCPUUtil);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pubnub.publish("mobilexyz", data, new Callback() {

            @Override
            public void successCallback(String channel, Object response) {
                Log.d("PUBNUB", response.toString());
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("PUBNUB", error.toString());
            }

        });


        Toast.makeText(context, "mobilexyz", Toast.LENGTH_SHORT).show();


        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
      /*  Intent intent2 = new Intent(context, BaseActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
        setAlarm(context);*/
    }


    private String getCpuUsageStatistic() {

        String tempString = executeTop();
        String userUtil="";
        tempString = tempString.replaceAll(",", "");
        tempString = tempString.replaceAll("User", "");
        tempString = tempString.replaceAll("System", "");
        tempString = tempString.replaceAll("IOW", "");
        tempString = tempString.replaceAll("IRQ", "");
        tempString = tempString.replaceAll("%", "");
        for (int i = 0; i < 10; i++) {
            tempString = tempString.replaceAll("  ", " ");
        }
        tempString = tempString.trim();
        String[] myString = tempString.split(" ");
        int[] cpuUsageAsInt = new int[myString.length];
        for (int i = 0; i < myString.length; i++) {
            myString[i] = myString[i].trim();
            cpuUsageAsInt[i] = Integer.parseInt(myString[i]);
        }
        userUtil= (String.valueOf(cpuUsageAsInt[0]));
        return userUtil;
    }

    private String executeTop() {
        java.lang.Process p = null;
        BufferedReader in = null;
        String returnString = null;
        try {
            p = Runtime.getRuntime().exec("top -n 1");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (returnString == null || returnString.contentEquals("")) {
                returnString = in.readLine();
            }
        } catch (IOException e) {
            Log.e("executeTop", "error in getting first line of top");
            e.printStackTrace();
        } finally {
            try {
                in.close();
                p.destroy();
            } catch (IOException e) {
                Log.e("executeTop",
                        "error in closing and destroying top process");
                e.printStackTrace();
            }
        }
        return returnString;
    }



    public class SaveAsyncTask extends AsyncTask<SystemInfo, Void, Boolean> {

        @Override
        protected Boolean doInBackground(SystemInfo... arg0) {

           /***
            *  Publish Data to PubNub
            * */

            try
            {
                SystemInfo sysInfo = arg0[0];
                Log.i("sysinfo@@@",sysInfo.totalCPUUtil);
                QueryBuilder qb = new QueryBuilder();
                String message="";
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(qb.buildContactsSaveURL());
                Log.i("@@apikey",qb.buildContactsSaveURL());
                JSONObject object = new JSONObject();
                try {

                    object.put("cpuUtil", sysInfo.totalCPUUtil);


                } catch (Exception ex) {

                }


                message = object.toString();
                StringEntity params =new StringEntity(qb.createRecord(sysInfo));
                //StringEntity params =new StringEntity(message);
                request.addHeader("content-type", "application/json");
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
                Log.i("@@response",String.valueOf(response.getStatusLine().getStatusCode()));
                if(response.getStatusLine().getStatusCode()<205)
                {
                    Log.i("@@","SUCESS");
                    return true;

                }
                else
                {
                    return false;
                }
            } catch (Exception e) {
                //e.getCause();
                String val = e.getMessage();
                String val2 = val;
                Log.i("exception@@",val2);
                return false;
            }
        }

    }


    public static void setAlarm(Context context){
        Log.d("Carbon", "Alrm SET !!");

        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();
        // add 30 seconds to the calendar object
        cal.add(Calendar.SECOND, 30);
        Intent intent = new Intent(context, AlarmReceiverLifeLog.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get the AlarmManager service
        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
    }
}
