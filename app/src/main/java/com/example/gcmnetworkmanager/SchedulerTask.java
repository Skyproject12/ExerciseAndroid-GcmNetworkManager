package com.example.gcmnetworkmanager;

import android.content.Context;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

public class SchedulerTask {
    private GcmNetworkManager gcmNetworkManager;

    public SchedulerTask(Context context) {
        gcmNetworkManager = GcmNetworkManager.getInstance(context);
    }
    // create method untuk menjalanakan dan membatalkan penjadwalan
    void createPeriodicTask(){
        // use spesifikasi for periode like flexand etc
        Task task= new PeriodicTask.Builder()
                // akan menjalankan program ketika kriteria dipenuhi
                .setService(SchedulerService.class)
                // mengatur jeda tugas yang akan di jalankan dalam satuan detik
                .setPeriod(60)
                .setFlex(10)
                // memberi tahu tag apa yang akan dijalankan
                .setTag(SchedulerService.TAG_TASK_WEATHER_LOG)
                .setPersisted(true)
                .build();
        // make schedule task
        gcmNetworkManager.schedule(task);
    }
    // cancel scheduler
    void cancelPeriodeTask(){
        // if data tidak kosong
        if(gcmNetworkManager!=null){
            // cancel the scheduler
            gcmNetworkManager.cancelTask(SchedulerService.TAG_TASK_WEATHER_LOG, SchedulerService.class);
        }
    }
}
