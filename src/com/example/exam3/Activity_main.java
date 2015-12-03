package com.example.exam3;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by brian on 03/12/2015.
 */
public class Activity_main extends Activity {

    public static String broadcastIntegerAction = "com.example.exam3.integer";

    private IntentFilter intentFilter;
    private TextView TextView_1;
    private TextView TextView_2;
    private Button startButton;
    private Button stopButton;
    private Button connectButton;
    private JSONObject jsonResult;
    private String jsonString;

    public HashMap <String, String> inputMap;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        inputMap = new HashMap<String, String>();

        TextView_1 = (TextView)findViewById(R.id.TextView_1);
        TextView_2 = (TextView)findViewById(R.id.TextView_2);
        startButton = (Button)findViewById(R.id.buttonStart);
        stopButton = (Button)findViewById(R.id.buttonStop);
        connectButton = (Button)findViewById(R.id.buttonConnect);

        intentFilter = new IntentFilter();

        intentFilter.addAction(broadcastIntegerAction);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_main.this,RandomNumberService.class);
                startService(intent);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_main.this,RandomNumberService.class);
                stopService(intent);
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit this depending on ip address.
                String url = "http://10.12.11.185:8080/loan-calculator";

                JSONObject jsonInputs = new JSONObject(inputMap);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{

                            String jsonString = HttpUtils.urlContentPost(url,"numbers",jsonInputs.toString());
                            jsonResult = new JSONObject(jsonString);
                        }catch(ClientProtocolException CPE){TextView_2.setText("Something is wrong");}
                        catch(IOException e){TextView_2.setText("it might be the server");}
                        catch(JSONException JSE){TextView_2.setText("Your json is bad and you should feel bad");}

                    }
                }).start();
                try{
                    TextView_2.setText(url+"\n"+jsonInputs.toString() +"\n");
                    TextView_2.setText(TextView_2.getText() + jsonResult.getString("num0")
                            +"\n" + jsonResult.getString("num1")
                            +"\n" + jsonResult.getString("num2")
                            +"\n" + jsonResult.getString("num3")
                            +"\n" + jsonResult.getString("num4")
                            +"\n" + jsonResult.getString("num5"));
                }catch(Exception EE){EE.printStackTrace();}
            }
        });
    }
    protected void onPause(){
        unregisterReceiver(receiver);
        super.onPause();
    }

    public void onResume(){
        super.onResume();
        registerReceiver(receiver,intentFilter);//register the receiver when activity resumes
    }

    private BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){

            TextView_1.setText(TextView_1.getText()+ "numbers\n");
            String key = TextView_1.getText().toString();
            if(intent.getAction().equals(broadcastIntegerAction)){
                TextView_1.setText(TextView_1.getText()+ intent.getIntegerArrayListExtra("data").toString());
                List<Integer> temp = new ArrayList<>(intent.getIntegerArrayListExtra("data"));
                for(int i = 0; i < temp.size(); i ++) {
                    inputMap.put("data" + i, temp.get(i).toString());
                }
            }

        }

    };

}