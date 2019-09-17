package com.example.gcmnetworkmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button buttonSetScheduler;
    Button butonCancelScheduler;
    private SchedulerTask schedulerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonSetScheduler= findViewById(R.id.button_set_scheduler);
        butonCancelScheduler= findViewById(R.id.button_cancel_scheduler);
        buttonSetScheduler.setOnClickListener(this);
        butonCancelScheduler.setOnClickListener(this);
        schedulerTask= new SchedulerTask(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.button_set_scheduler){
            // create periodik task from scheduler task
            schedulerTask.createPeriodicTask();
            Toast.makeText(this, "Periodic Task Create", Toast.LENGTH_SHORT).show();
        }
        if(view.getId()== R.id.button_cancel_scheduler){
            // cancel periode task
            schedulerTask.cancelPeriodeTask();
            Toast.makeText(this, "Periodic Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
