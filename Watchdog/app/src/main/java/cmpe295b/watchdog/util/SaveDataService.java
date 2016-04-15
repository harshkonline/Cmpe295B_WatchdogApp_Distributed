package cmpe295b.watchdog.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

public class SaveDataService extends Service {
    public static String PARAM_IN_MSG="inmsg";

    public SaveDataService() {
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("@msg",intent.getStringExtra(PARAM_IN_MSG));
        Log.i("@@","keep working");
        return Service.START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // TODO Auto-generated method stub
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);

        //Restart the service once it has been killed android


        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);

    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        //start a separate thread and start listening to your network object
    }

}
