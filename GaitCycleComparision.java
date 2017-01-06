package com.example.asa.gaitrecog;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class GaitCycleComparision extends ActionBarActivity {

    static double maximum = 0.0, current_value = 0.0, threshold = 0.5, deviation = 0.0;

    static String NameOfCycle,FilePathTesting,FilePathTraining,FilePathTrainingInner,CurrentComparision,MatchingCycle;

    static ArrayList Outer,Inner,Temp;

    static TextView Cycle,MinDist;

    static File TrainingCycles[],TrainingFolders[],StorageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gait_cycle_comparision);

        MinDist = (TextView)findViewById(R.id.MinimumDistance);
        Cycle = (TextView)findViewById(R.id.CycleName);

        try {

            DTWSimilarity d = new DTWSimilarity();

            createStorageFile();
            File root = android.os.Environment.getExternalStorageDirectory();

            // Opening the testing cycle file
            FileInputStream textFile_testing = null;
            FilePathTesting = root.getAbsolutePath() + "//TestingCycles//Cycle_Testing.txt";

            // Opening the training cycles file
            FilePathTraining = root.getAbsolutePath() + "/TrainingCycles";
            File Folder = new File(FilePathTraining);
            TrainingFolders = Folder.listFiles();


            Outer = new ArrayList();

            for( File temp : TrainingFolders) {
                if(temp.isDirectory()) {

                    deviation = 0.0;
                    Inner = new ArrayList();
                    NameOfCycle = temp.toString();

                    System.out.println(" The folder and file is => " + temp.toString());

                    FilePathTrainingInner = temp.toString();
                    System.out.println("Training path = " + FilePathTrainingInner);
                    File Training_Folder = new File(FilePathTrainingInner);
                    TrainingCycles = Training_Folder.listFiles();

                    // Find the maximum distance out of all other comparisions
                    for(int i = 0 ; i < TrainingCycles.length ; i++) {
                        current_value = d.measure(getArrayList(FilePathTesting),getArrayList(TrainingCycles[i].toString()));
                        deviation += (current_value - threshold);

                        System.out.println("Current Value = " + current_value + "\n Name of Cycle = " + TrainingCycles[i].toString() + "\n Deviation = " + deviation);
                        CurrentComparision = "Current Value = " + current_value + "\n Name of Cycle = " + TrainingCycles[i].toString() + "\n Deviation = " + deviation;
                        WriteToExtMedia.writeToSDFile(CurrentComparision,"",StorageFile);
                    }

                    Inner.add(deviation);
                    Inner.add(NameOfCycle);

                    Outer.add(Inner);
                }
            }

            checkMaximum();

            // Displaying the maximum distance and the name of the cycle
            System.out.println("The maximum value is =====>>>> " + maximum);
            MinDist.setText(maximum + "");
            Cycle.setText(MatchingCycle);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Takes text file as the input and returns an arraylist of the same file
    public ArrayList getArrayList(String FileName) {

        ArrayList Inner = new ArrayList();
        ArrayList Temp;
        String current_line;

        try {
            FileInputStream textFile = new FileInputStream(FileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(textFile));
            int i = 0;
            while ((current_line = br.readLine()) != null) {
                Inner.add(Double.parseDouble(current_line));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Inner;
    }

    public void createStorageFile() {
        if (WriteToExtMedia.checkExternalMedia()){
            // Find the root of the external storage.
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File (root.getAbsolutePath() + "/gait");
            dir.mkdirs();
            String filename;
            filename = "DTW_Comparisions.txt";

            StorageFile = new File(dir,filename);
        }else{
            // couldn't find external media to write data onto
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void checkMaximum() {
        //maximum = Double.MIN_VALUE;
        Temp = (ArrayList)Outer.get(0);
        maximum = (Double)Temp.get(0);
        MatchingCycle = (String)Temp.get(1);
        Log.v("Maximum Value Initial : ", maximum + "");
        Log.v("Maximum Cycle Initial : ", MatchingCycle + "");
        for (int i = 1 ; i < Outer.size() ; i++) {
            Temp = (ArrayList)Outer.get(i);
            if((Double)Temp.get(0) > maximum) {
                maximum = (Double)Temp.get(0);
                MatchingCycle = (String)Temp.get(1);
                Log.v("Maximum Value : ", maximum + "");
                Log.v("Maximum Cycle : ", MatchingCycle + "");
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gait_cycle_comparision, menu);
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
}
