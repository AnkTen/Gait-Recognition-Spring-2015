
package com.example.asa.gaitrecog;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;



public class RecordingActivity extends ActionBarActivity implements SensorEventListener{

    public static String Temp[];
    public static String user_name,user;
    public static String directoryName;
    public static String user_plus_directory_plus_button_type_plus_user;
    public static String button_clicked_type;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    TextView username_tv;
    TextView x_tv;
    TextView y_tv;
    TextView z_tv;

    String currentreading;

    long timestamp;

    Button starttrainingbutton, stoptrainingbutton;

    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        // Initialize the layout objects
        x_tv = (TextView)findViewById(R.id.xtextView);
        y_tv = (TextView)findViewById(R.id.ytextView);
        z_tv = (TextView)findViewById(R.id.ztextView);
        username_tv = (TextView)findViewById(R.id.usernametextView);

        Intent intent = getIntent();
        user_plus_directory_plus_button_type_plus_user = intent.getStringExtra(user_name);
        Temp = user_plus_directory_plus_button_type_plus_user.split(";");

        user_name = Temp[0];
        directoryName = Temp[1];
        button_clicked_type = Temp[2];
        user = Temp[3];

        username_tv.setText(user_name);

        starttrainingbutton = (Button) findViewById(R.id.starttrainingbutton);
        stoptrainingbutton = (Button) findViewById(R.id.stoptrainingbutton);

        // Initialize the sensors
        senSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Initialize the file from username obtained
        if (WriteToExtMedia.checkExternalMedia()){
            // Find the root of the external storage.
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/gait");
            dir.mkdirs();
            String filename;
            filename = "GaitData" + user_name + ".txt";

            file = new File(dir,filename);
        }else{
          // couldn't find external media to write data onto
            Toast toast = Toast.makeText(getApplicationContext(), "Cannot write to storage, exiting.", Toast.LENGTH_SHORT);
            toast.show();
            android.os.Process.killProcess(android.os.Process.myPid());

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onStartClick(View view) {
        senSensorManager.registerListener(this,senAccelerometer, 10000);
        Toast toast = Toast.makeText(getApplicationContext(), "Recording Started...", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onStopClick(View view) {
        senSensorManager.unregisterListener(this);
        Toast toast = Toast.makeText(getApplicationContext(), "Recording Stopped...", Toast.LENGTH_SHORT);
        toast.show();

        // Call Peak detection to carry out further processing
        PeakDetection p = new PeakDetection();
        p.initiate(user_name,directoryName,button_clicked_type,user);

    }

    public void onDeleteClick(View view) {

        file.delete();
        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File textFile_testing = null;
            String FilePathTesting = root.getAbsolutePath() + "//TestingCycles//Cycle_Testing.txt";
            textFile_testing = new File(FilePathTesting);
            textFile_testing.delete();
            FilePathTesting = root.getAbsolutePath() + "//gait//GaitDataTesting.txt";
            textFile_testing = new File(FilePathTesting);
            textFile_testing.delete();
            FilePathTesting = root.getAbsolutePath() + "//gait//DTW_Comparisions.txt";
            textFile_testing = new File(FilePathTesting);
            textFile_testing.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void check(View view) {
        Button button = (Button) view;
        startActivity(new Intent(getApplicationContext(),GaitCycleComparision.class));
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor accSensor = sensorEvent.sensor;
        if (accSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            timestamp = sensorEvent.timestamp;

            currentreading = timestamp + " " + x + " " + y + " " + z;

            x_tv.setText(Float.toString(x));
            y_tv.setText(Float.toString(y));
            z_tv.setText(Float.toString(z));

            WriteToExtMedia.writeToSDFile(currentreading, user_name, file);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
