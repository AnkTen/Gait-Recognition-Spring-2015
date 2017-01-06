
package com.example.asa.gaitrecog;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class UserInfoActivity extends ActionBarActivity {

    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        //getMenuInflater().inflate(R.menu.menu_user_info, menu);
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

    public void submitOnClick(View v)
    {
        EditText firstname = (EditText) findViewById(R.id.firstName);
        //EditText lastname = (EditText) findViewById(R.id.lastName);

        String username = firstname.getText().toString();// + " " + lastname.getText();
        String user = firstname.getText().toString();

        SharedPrefHandler sph = new SharedPrefHandler(context, "Walk_counts");
        int currentwalkcount = sph.getInt(username);
        sph.put(username, ++currentwalkcount);

        // Now concatenating username and walk count
        //Log.v("current",""+currentwalkcount);
        username += " " + currentwalkcount;

        //Log.v("sudeep",username);

        Button button = (Button) v;
        String directoryName = "TrainingCycles";
        String button_clicked_type = "Training";
        username += ";" + directoryName + ";" + button_clicked_type + ";" + user;
        Intent intent = new Intent(getApplicationContext() , RecordingActivity.class );
        intent.putExtra(RecordingActivity.user_name,username);
        startActivity(intent);

    }
}
