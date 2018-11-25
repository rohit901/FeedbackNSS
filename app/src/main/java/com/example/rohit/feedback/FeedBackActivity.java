package com.example.rohit.feedback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedBackActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    ProgressDialog pd;
    private ProgressDialog pDialog;
    Button syncJSON;
    String result;
    JSONObject jsonPart;
    ListView feedbackLv;
    ArrayList<String> feedbackLists;
    String currUserEmail;
    ArrayAdapter arrayAdapter;
    ArrayList<Integer> absolutepos;
    Boolean isUnique=false;
    String multipleTags;
    EditText searchTag;
    String currUserDepartment;
    FirebaseFirestore db;
    private static final String NAME_KEY = "Name";

    private static final String EMAIL_KEY = "Email";

    private static final String PHONE_KEY = "Phone";
    int flag;
    private boolean isCurrUserCoord;
    private boolean isSpecial=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);


        db = FirebaseFirestore.getInstance();
        //addNewContact();

        feedbackLv = (ListView) findViewById(R.id.feedbackLIstVIew);
        feedbackLists = new ArrayList<String>();
        absolutepos = new ArrayList<Integer>();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,feedbackLists);

        searchTag = (EditText)findViewById(R.id.searchTagEditText);
        searchTag.setVisibility(View.GONE);
        feedbackLv.setAdapter(arrayAdapter);

        pd = new ProgressDialog(FeedBackActivity.this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pDialog = new ProgressDialog(FeedBackActivity.this);


        syncJSON = (Button) findViewById(R.id.syncJSON);

        mAuth = FirebaseAuth.getInstance();



        feedbackLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(),FeedBackDetail.class);
               // intent.putExtra("pos",absolutepos.get(position));
                intent.putExtra("specificpos",absolutepos);
                intent.putExtra("title",feedbackLists.get(feedbackLists.indexOf(parent.getItemAtPosition(position))));
                intent.putExtra("result",result);
                startActivity(intent);

            }
        });




        searchTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                FeedBackActivity.this.arrayAdapter.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });





    }

    private void addNewContact() {

        Map<String, Object> newContact = new HashMap<>();

        newContact.put(NAME_KEY, "John");

        newContact.put(EMAIL_KEY, "john@gmail.com");

        newContact.put(PHONE_KEY, "080-0808-009");

        db.collection("Feedbacks").document("Persons").set(newContact)

                .addOnSuccessListener(new OnSuccessListener<Void>() {

                    @Override

                    public void onSuccess(Void aVoid) {

                        Toast.makeText(FeedBackActivity.this, "User Registered",

                                Toast.LENGTH_SHORT).show();

                    }

                })

                .addOnFailureListener(new OnFailureListener() {

                    @Override

                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(FeedBackActivity.this, "ERROR" + e.toString(),

                                Toast.LENGTH_SHORT).show();

                        Log.d("TAG", e.toString());

                    }

                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    public void syncData(View view){




        feedbackLists.clear();
        absolutepos.clear();

        try{

            //result =  task.execute("https://script.google.com/macros/s/AKfycbxOLElujQcy1-ZUer1KgEvK16gkTLUqYftApjNCM_IRTL3HSuDk/exec?id=1G3N87LBRtAO7BiAUwdVmox_bREKHkKvOwOceQLlQ4So&sheet=Sheet1").get();
              new JsonTask().execute("https://script.google.com/macros/s/AKfycbxOLElujQcy1-ZUer1KgEvK16gkTLUqYftApjNCM_IRTL3HSuDk/exec?id=1G3N87LBRtAO7BiAUwdVmox_bREKHkKvOwOceQLlQ4So&sheet=Sheet1");

            //Log.d("rohit901",result);


        }
        catch (Exception e){
            e.printStackTrace();
        }


    }
    private void hideProgressDialog() {
        pDialog.dismiss();
    }
    private void displayProgressDialog() {
        pDialog.setMessage("Downloading.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }




    public class JsonTask extends AsyncTask<String,Void,String> {







        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS

            //pd.show();
            displayProgressDialog();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            searchTag.setVisibility(View.VISIBLE);


            //Toast.makeText(FeedBackActivity.this, "DOne", Toast.LENGTH_SHORT).show();
            Log.d("rohit901",s);
            String currUserEmail2;
            currUserEmail2 = mAuth.getCurrentUser().getEmail();
            currUserEmail2 = currUserEmail2.substring(0,5);

           // Toast.makeText(FeedBackActivity.this, currUserEmail, Toast.LENGTH_SHORT).show();

            if(currUserEmail2.equals("f2016")){               // Before 2017


                currUserEmail = mAuth.getCurrentUser().getEmail();
                currUserEmail = currUserEmail.substring(0,8);
                Toast.makeText(FeedBackActivity.this, "EmailID: "+currUserEmail, Toast.LENGTH_SHORT).show();


            }

            else{                        //2017 onwards


                currUserEmail = mAuth.getCurrentUser().getEmail();
                currUserEmail = currUserEmail.substring(0,9);
                //Toast.makeText(FeedBackActivity.this, "EmailID: "+currUserEmail, Toast.LENGTH_SHORT).show();



            }


            result = s;
            try {
                JSONObject jsonObject = new JSONObject(result);
                String feedbacks = jsonObject.getString("Sheet1");
                JSONArray arr = new JSONArray(feedbacks);


                for(int k=0;k<arr.length();k++){

                    jsonPart = arr.getJSONObject(k);

                    if(jsonPart.getString("Volunteer_ID").equals(currUserEmail)){
                        currUserDepartment = jsonPart.getString("Department");
                        isCurrUserCoord = Boolean.parseBoolean(jsonPart.getString("IsCoord"));
                    }

                }



                for(int i =0;i<arr.length();i++) {





                    jsonPart = arr.getJSONObject(i);

                    //jsonPart.getString("To")

                    if(!jsonPart.getString("To").isEmpty()) {
                        String toString = jsonPart.getString("To");
                        String[] departmentString = toString.split(",");

                        boolean boldownloaded=false;

                        boolean boldownloaded2=false;

                        String[] multiDepartment = departmentString[0].split("/");

                        String[] coordStatus;

                        for (int l = 0; l < multiDepartment.length; l++) {


                            isSpecial = false;

                            coordStatus = multiDepartment[l].split("-");

                            if (coordStatus.length == 1){


                                if (multiDepartment[l].equals("President") || multiDepartment[l].equals("Vice President") || multiDepartment[l].equals("Secretary")) {

                                    isSpecial = true;
                                    boldownloaded = true;
                                    boldownloaded2 = false;

                                    if ((currUserDepartment.equals(multiDepartment[l])) && (jsonPart.getString("Resolved").isEmpty())) {
                                        absolutepos.add(i);
                                        String[] splitTags = jsonPart.getString("Tags").split(", ");
                                        //Log.d("SplitTag901",Integer.toString(splitTags.length));
                                        if (splitTags.length == 1) {
                                            if (feedbackLists.isEmpty()) {
                                                feedbackLists.add(jsonPart.getString("Tags"));
                                            } else {
                                                flag = 0;
                                                for (int k = 0; k < feedbackLists.size(); k++) {
                                                    if (feedbackLists.get(k).equals(jsonPart.getString("Tags"))) {
                                                        flag = 1;
                                                        break;
                                                    }
                                                }
                                                if (flag != 1) {
                                                    feedbackLists.add(jsonPart.getString("Tags"));
                                                }
                                            }
                                            arrayAdapter.notifyDataSetChanged();
                                        } else if (splitTags.length > 1) {

                                            //splitTags[0];
                                            for (int j = 0; j < splitTags.length; j++) {

                                                multipleTags = splitTags[j];


                                                if (feedbackLists.isEmpty()) {
                                                    feedbackLists.add(multipleTags);
                                                } else {
                                                    flag = 0;
                                                    for (int k = 0; k < feedbackLists.size(); k++) {
                                                        if (feedbackLists.get(k).equals(multipleTags)) {
                                                            flag = 1;
                                                            break;
                                                        }
                                                    }
                                                    if (flag != 1) {
                                                        feedbackLists.add(multipleTags);
                                                    }
                                                }
                                                arrayAdapter.notifyDataSetChanged();


                                            }

                                        }


                                        break;

                                    }


                                }

                        }

                        else if(coordStatus[1].equals("Coord")){
                                multiDepartment[l] = coordStatus[0];
                                boldownloaded = true;
                                boldownloaded2 = false;
                            }
                        else if (coordStatus[1].equals("ExComm")){
                                multiDepartment[l] = coordStatus[0];
                                boldownloaded2 = true;
                                boldownloaded = false;
                            }





                        if ((currUserDepartment.equals(multiDepartment[l])) && (!isSpecial)&& (jsonPart.getString("Resolved").isEmpty()) && ((isCurrUserCoord == boldownloaded) || (!isCurrUserCoord == boldownloaded2))) {
                            absolutepos.add(i);
                            String[] splitTags = jsonPart.getString("Tags").split(", ");
                            //Log.d("SplitTag901",Integer.toString(splitTags.length));
                            if (splitTags.length == 1) {
                                if (feedbackLists.isEmpty()) {
                                    feedbackLists.add(jsonPart.getString("Tags"));
                                } else {
                                    flag = 0;
                                    for (int k = 0; k < feedbackLists.size(); k++) {
                                        if (feedbackLists.get(k).equals(jsonPart.getString("Tags"))) {
                                            flag = 1;
                                            break;
                                        }
                                    }
                                    if (flag != 1) {
                                        feedbackLists.add(jsonPart.getString("Tags"));
                                    }
                                }
                                arrayAdapter.notifyDataSetChanged();
                            } else if (splitTags.length > 1) {

                                //splitTags[0];
                                for (int j = 0; j < splitTags.length; j++) {

                                    multipleTags = splitTags[j];


                                    if (feedbackLists.isEmpty()) {
                                        feedbackLists.add(multipleTags);
                                    } else {
                                        flag = 0;
                                        for (int k = 0; k < feedbackLists.size(); k++) {
                                            if (feedbackLists.get(k).equals(multipleTags)) {
                                                flag = 1;
                                                break;
                                            }
                                        }
                                        if (flag != 1) {
                                            feedbackLists.add(multipleTags);
                                        }
                                    }
                                    arrayAdapter.notifyDataSetChanged();


                                }

                            }


                            break;

                        }
                    }


                    }
                }





            } catch (JSONException e) {
                e.printStackTrace();
            }

            hideProgressDialog();
            if(feedbackLists.isEmpty()){
                Toast.makeText(FeedBackActivity.this, "No Feedbacks as of Now!", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(String... urls) {



            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;


        }
    }
    /* pd.show();



            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;*/
}


