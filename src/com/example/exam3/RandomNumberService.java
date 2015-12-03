package com.example.exam3;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by brian on 03/12/2015.
 */
public class RandomNumberService extends Service {

    ArrayList<Integer> arrayList = new ArrayList<Integer>();
    Calendar c = Calendar.getInstance();
    Random random = new Random(c.get(Calendar.SECOND));
    @Override
    public void onCreate(){
        super.onCreate();

        for(int i = 0; i < 5; i ++){
            arrayList.add(randomNumber());
        }
    }
    public int randomNumber(){
        int rand = random.nextInt(1000);
        return rand;
    }
    public int onStartCommand(Intent intent, int flags, int startID){
        new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    Thread.sleep(1000);
                }
                catch(InterruptedException IE){IE.printStackTrace();}
                Intent intent = new Intent();
                intent.setAction(Activity_main.broadcastIntegerAction);
                intent.putExtra("data",arrayList);
                sendBroadcast(intent);
            }
        }).start();
        return START_NOT_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onTaskRemoved(Intent root){
        super.onTaskRemoved(root);
    }

    public void onDestroy(){
        super.onDestroy();
    }
}
