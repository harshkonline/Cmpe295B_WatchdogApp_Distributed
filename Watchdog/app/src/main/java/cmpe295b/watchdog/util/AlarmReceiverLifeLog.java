package cmpe295b.watchdog.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

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
import java.util.ArrayList;
import java.util.Date;

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

      /*  SaveAsyncTask tsk = new SaveAsyncTask();
        tsk.execute(info);*/
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
            /*pubnub.subscribe("mobilexyz", new Callback() {
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

                public void reconnectCallback(String channel,    Object message) {
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
            });*/
        } catch (Exception e) {
            e.printStackTrace();
        }


        JSONObject data = new JSONObject();

        ArrayList<Object> dataTemp1=new ArrayList<Object>();
        dataTemp1.add("x");
        dataTemp1.add(new Date().getTime());

        ArrayList<Object> dataTemp2=new ArrayList<Object>();
        dataTemp2.add("cpuUtil");
        dataTemp2.add(totalCPUUtil);

        ArrayList<Object> parent=new ArrayList<Object>();
        parent.add(dataTemp1);
        parent.add(dataTemp2);

        try {
            data.put("columns",parent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

      long  t= new Date().getTime();
       String data1= "{'columns': [['cpuUtil',"+ totalCPUUtil+"]]}";
       // String data1= "{'columns': [['x',"+ t+"],['cpuUtil',"+ 45+"]]}";


        try {
           // JSONArray jsonArray = new JSONArray(data1);


            JSONObject obj=new JSONObject(data1);
            Log.i("@@json", String.valueOf(obj));
                pubnub.publish("mobilexyz", obj, new Callback() {

                @Override
                public void successCallback(String channel, Object response) {
                    Log.d("PUBNUB PUBLISH", response.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("PUBNUB PUBLISH", error.toString());
                }

            });


        } catch (Exception e) {
            e.printStackTrace();
        }






       /* try
        {

            Log.i("sysinfo@@@",totalCPUUtil);
            QueryBuilder qb = new QueryBuilder();

            HttpClient httpClient = new DefaultHttpClient();
            //HttpPost request = new HttpPost(qb.buildContactsSaveURL());
            HttpPost request = new HttpPost("http://10.0.0.227:4000/devices");

            //Log.i("@@apikey",qb.buildContactsSaveURL());
            JSONObject object = new JSONObject();
            try {

                object.put("device_id","dev");
                object.put("device_type","mobile");
                object.put("channel","ch1");
                object.put("date","2016-04-12");
                object.put("value", totalCPUUtil);


            } catch (Exception ex) {

            }


            message = object.toString();
            Log.i("@Message",message);
            StringEntity params =new StringEntity(message);
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            Log.i("@@response",String.valueOf(response.getStatusLine().getStatusCode()));
            if(response.getStatusLine().getStatusCode()<205)
            {
                Log.i("@response","SUCESS");

            }
            else
            {
                Log.i("@response","Failed");
            }
        } catch (Exception e) {
            //e.getCause();
            String val = e.getMessage();
            String val2 = val;
            Log.i("exception@@",val2);

        }*/


}



       // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
      /*  Intent intent2 = new Intent(context, BaseActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent2);
        setAlarm(context);*/



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
            *  Save the data to database
            * */

            try
            {
                SystemInfo sysInfo = arg0[0];
                Log.i("sysinfo@@@",sysInfo.totalCPUUtil);
                QueryBuilder qb = new QueryBuilder();
                String message="";
                HttpClient httpClient = new DefaultHttpClient();
                //HttpPost request = new HttpPost(qb.buildContactsSaveURL());
                HttpPost request = new HttpPost("http://10.0.0.227:4000/devices");

                //Log.i("@@apikey",qb.buildContactsSaveURL());
                JSONObject object = new JSONObject();
                try {

                    object.put("device_id","dev");
                    object.put("device_type","mobile");
                    object.put("channel","ch1");
                    object.put("date","2016-04-12");
                    object.put("value", sysInfo.totalCPUUtil);


                } catch (Exception ex) {

                }


                message = object.toString();
                Log.i("@Message",message);
                StringEntity params =new StringEntity(message);
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



}
