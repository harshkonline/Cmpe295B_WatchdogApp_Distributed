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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by harshad on 4/14/2016.
 */
public class AlarmReceiverLifeLog extends BroadcastReceiver {

    private static final String TAG = "LL24";
    static Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = "Sending Data for Health Monitoring and Prediction";
        String totalCPUUtil = "";
        totalCPUUtil = getCpuUsageStatistic().toString();
        //Long percentageUtillizedMeomory=0L;
        SystemInfo info = new SystemInfo();

        //calculate battery temperature
       // float temperature = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0) / 10);
        //String temperature=getBatteryTemperature();
        //calculate memory utillization

      /*  ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        if (Build.VERSION.SDK_INT >= 16) {
            // percentage can be calculated for API 16+
            percentageUtillizedMeomory  = (long) ((float) mi.availMem / mi.totalMem * 100);
        }else
        {
            percentageUtillizedMeomory=73L;

        }*/
      //  String percentageUtillizedMeomory=getPercentageMemory();

        info.totalCPUUtil = totalCPUUtil;
        /* Subscribe to the demo_tutorial channel */
        Pubnub pubnub = new Pubnub(
                "pub-c-d3bae2e1-d58c-457c-bee1-4fd8bb7c8992",  // PUBLISH_KEY   (Optional, supply "" to disable)
                "sub-c-915d6d7c-615c-11e5-9a34-02ee2ddab7fe",  // SUBSCRIBE_KEY (Required)
                "",      // SECRET_KEY    (Optional, supply "" to disable)
                "",      // CIPHER_KEY    (Optional, supply "" to disable)
                false    // SSL_ON?
        );
        String cpuData = "{'columns': [['cpuUtil'," + totalCPUUtil + "]]}";
       // String memoryData = "{'columns': [['memoryUtil'," + percentageUtillizedMeomory + "]]}";
        //String batteryData = "{'columns': [['batteryTemp'," + temperature+ "]]}";
        try {

            JSONObject obj = new JSONObject(cpuData);
           // JSONObject obj1 = new JSONObject(memoryData);
          //  JSONObject obj2 = new JSONObject(batteryData);
            Log.i("@@vpu", String.valueOf(obj));
           // Log.i("@@jmemory", String.valueOf(obj1));
            //Log.i("@@battery", String.valueOf(obj2));

            pubnub.publish("cpu", obj, new Callback() {

                @Override
                public void successCallback(String channel, Object response) {
                    Log.d("PUBNUB PUBLISH", response.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("PUBNUB PUBLISH", error.toString());
                }

            });

         /*   pubnub.publish("battery", obj2, new Callback() {

                @Override
                public void successCallback(String channel, Object response) {
                    Log.d("PUBNUB PUBLISH", response.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("PUBNUB PUBLISH", error.toString());
                }

            });


            pubnub.publish("memory", obj1, new Callback() {

                @Override
                public void successCallback(String channel, Object response) {
                    Log.d("PUBNUB PUBLISH", response.toString());
                }

                @Override
                public void errorCallback(String channel, PubnubError error) {
                    Log.d("PUBNUB PUBLISH", error.toString());
                }

            });*/


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* private String getPercentageMemory() {

        Random ran = new Random();
        int x = ran.nextInt(73-56+1)+56;
        return Integer.toString(x);
    }

    private String getBatteryTemperature() {

        Random ran = new Random();
        int x = ran.nextInt(33-27+1)+27;
        return Integer.toString(x);

    }*/

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
