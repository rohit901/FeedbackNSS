package com.example.rohit.feedback;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class TimeStampActivity extends AppCompatActivity {
    TextView timestampDetail;
    Intent intent;
    TextView displayFeed;
    String result;
    int pos;
    private ProgressDialog pDialog;
    JSONObject jsonPart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_stamp);
        timestampDetail = (TextView)findViewById(R.id.timestampdisplayTv);
        displayFeed = (TextView)findViewById(R.id.displayFeedTV);
        intent = getIntent();
        result = intent.getStringExtra("result");
        pos = intent.getIntExtra("pos",0);
        pDialog = new ProgressDialog(TimeStampActivity.this);

        try {
            JSONObject jsonObject = new JSONObject(result);
            String feedbacks = jsonObject.getString("Sheet1");
            JSONArray arr = new JSONArray(feedbacks);

            jsonPart = arr.getJSONObject(pos);
            displayFeed.setText(jsonPart.getString("Feedback"));
            timestampDetail.setText(jsonPart.getString("Timestamp"));


        } catch (JSONException e) {
            e.printStackTrace();
        }




    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.timestampmenu,menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.resolvefeed:
                new SendRequest().execute();

                return true;
            default:
                return false;
        }
    }
    private void displayProgressFeedDialog() {
        pDialog.setMessage("Submitting.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }
    private void hideProgressDialog() {
        pDialog.dismiss();
    }
    public class SendRequest extends AsyncTask<String, Void, String> {


        protected void onPreExecute(){
            displayProgressFeedDialog();
        }

        protected String doInBackground(String... arg0) {

            try{

                //Enter script URL Here
                URL url = new URL("https://script.google.com/macros/s/AKfycbx8P3oTRzAgTrII93LWhoUKe97wsP4tMfJgIgzpT54AJZ80nlY/exec?");

                JSONObject postDataParams = new JSONObject();

                //Passing scanned code as parameter

                postDataParams.put("pos",pos);



                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getApplicationContext(), result,
            // Toast.LENGTH_LONG).show();
            hideProgressDialog();
            Toast.makeText(TimeStampActivity.this, "Resolved.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),FeedBackActivity.class);
            startActivity(intent);

        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
