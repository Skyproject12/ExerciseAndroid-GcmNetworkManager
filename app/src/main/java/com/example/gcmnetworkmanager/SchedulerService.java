package com.example.gcmnetworkmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

// memiliki suatu thread yang di jalanakan secara terpisah saat asynchronous
public class SchedulerService extends GcmTaskService {

    final String TAG= SchedulerService.class.getSimpleName();
    final String APP_ID= "ff728c2cf44b15078267a46442290055";
    final String CITY= "BANDUNG";

    static String TAG_TASK_WEATHER_LOG= "Weather Task";

    // run scheduler
    @Override
    public int onRunTask(TaskParams taskParams) {
        int result=0;
        // ketika suatu param sama dengan TAG_TASK_WEATHER_LOG
        if(taskParams.getTag().equals(TAG_TASK_WEATHER_LOG)){
            // akan memanggil method getCurrentWeather
            getCurrentWeather();
            // memberi result succes pada GcmNetwork
            result= GcmNetworkManager.RESULT_SUCCESS;
        }
        return result;
    }

    // initial suatu scheduler
    @Override
    public void onInitializeTasks() {
        super.onInitializeTasks();
        // take SchedulerTask
        SchedulerTask schedulerTask= new SchedulerTask(this);
        // createPeriodic with call method in schedulerTask
        schedulerTask.createPeriodicTask();
    }
    private void getCurrentWeather(){
        Log.d("GetWeather","Running");
        SyncHttpClient client= new SyncHttpClient();
        String url= "http://api.openweathermap.org/data/2.5/weather?q="+ CITY + "&appid"+ APP_ID;
        // definition url for client
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // take result from api format json
                String result= new String(responseBody);
                Log.d(TAG, result);
                try {
                    JSONObject responseObject= new JSONObject(result);
                    // get data from api
                    String currentWeather= responseObject.getJSONArray("weather").getJSONObject(0).getString("main");
                    String description= responseObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    double tempInKelvin= responseObject.getJSONObject("main").getDouble("temp");

                    double tempInCelcius= tempInKelvin - 273;
                    String temperatur= new DecimalFormat("##.##").format(tempInCelcius);

                    // title
                    String title= "Current Weather";
                    // message with value of data from api
                    String message= currentWeather+ ","+description+"with"+temperatur+"celcius";
                    int notifId=100;
                    // inggrediesn for notification
                    showNotification(getApplicationContext(), title, message, notifId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("GetWeather", "Failed");
            }
        });
    }
    private void showNotification(Context context, String title, String message, int notifId){
        String CHANNEL_ID= "Channel_1";
        String CHANNEL_NAME= "Job service channel";
        NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_arrow)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.black))
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setSound(alarmSound);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel= new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{1000,1000,1000,1000,1000});
            builder.setChannelId(CHANNEL_ID);
            if(notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
        Notification notification= builder.build();
        if(notificationManager!=null){
            notificationManager.notify(notifId, notification);
        }
    }
}
