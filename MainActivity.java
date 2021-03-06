
package com.example.asa.gaitrecog;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity {
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void trainingButtonOnClick(View v){
        Button button = (Button) v;
        startActivity(new Intent(getApplicationContext(),UserInfoActivity.class));
    }

    public void testingButtonOnClick(View v){
        Button button = (Button) v;
        String directoryName = "TestingCycles";
        String UserName = "Testing";
        String button_clicked_type = "Testing";
        UserName += ";" + directoryName + ";" + button_clicked_type + ";" + "null";
        Intent intent = new Intent(getApplicationContext() , RecordingActivity.class );
        intent.putExtra(RecordingActivity.user_name,UserName);
        startActivity(intent);
    }
}
