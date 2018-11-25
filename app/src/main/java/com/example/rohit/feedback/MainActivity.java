package com.example.rohit.feedback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private ProgressDialog pDialog;
    private GoogleSignInClient mGoogleSignInClient;
    Button submitButton;
    EditText toEdit;
//    CheckBox coordCheck;
//    CheckBox excommCheck;
    EditText tagsEdit;
    EditText toEditTextMulti;
    EditText feedEdit;
    private RadioGroup radioGroup;

    String toSelected;

    Spinner ToSpinner;
    final Context c = this;
    private boolean canExecute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        Button signOutButton = findViewById(R.id.sign_out_button);
        pDialog = new ProgressDialog(MainActivity.this);

        toEditTextMulti = (EditText)findViewById(R.id.ToEditTextMulti);
//        coordCheck = (CheckBox)findViewById(R.id.checkCoord);
//        excommCheck = (CheckBox)findViewById(R.id.checkExComm);


       // radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        //radioGroup.clearCheck();

//        //radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton rb = (RadioButton) group.findViewById(checkedId);
//                //if (null != rb && checkedId > -1) {
//                   // Toast.makeText(MainActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
//                //}
//
//            }
//        });




       // toEdit = (EditText)findViewById(R.id.toEditText);
        ToSpinner = (Spinner) findViewById(R.id.toSpinner);
        tagsEdit = (EditText)findViewById(R.id.TagsEditText);
        feedEdit = (EditText)findViewById(R.id.FeedbackEditText);



        // Spinner click listener
        ToSpinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("President");
        categories.add("Vice President");
        categories.add("Secretary");
        categories.add("D3-Coord");
        categories.add("HPA-Coord");
        categories.add("CLP-Coord");
        categories.add("EPD-Coord");
        categories.add("UMANG-Coord");
        categories.add("SCHOOL-Coord");
        categories.add("PARISHODH-Coord");
        categories.add("EVENTS-Coord");
        categories.add("SPONS-Coord");

        categories.add("D3-ExComm");
        categories.add("HPA-ExComm");
        categories.add("CLP-ExComm");
        categories.add("EPD-ExComm");
        categories.add("UMANG-ExComm");
        categories.add("SCHOOL-ExComm");
        categories.add("PARISHODH-ExComm");
        categories.add("EVENTS-ExComm");
        categories.add("SPONS-ExComm");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ToSpinner.setAdapter(dataAdapter);




        submitButton = (Button)findViewById(R.id.submitButton);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                canExecute = true;

                String multiTo[] = toEditTextMulti.getText().toString().split("/");

//                for (int i=0;i<multiTo.length;i++){
//
////                    if( multiTo[i].equals("President") || multiTo[i].equals("Vice President") || multiTo[i].equals("Secretary")){
////                        if(!coordCheck.isChecked()){
////                            Toast.makeText(c, "Please Make the Coord Check box Checked for Pres, Vice Pres and Sec", Toast.LENGTH_SHORT).show();
////                            canExecute = false;
////                            break;
////                        }
////                    }
//                }



                if(canExecute ) {
                    new SendRequest().execute();
                }

            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.admin:
                Log.i("menu button","admin clicked!");
                        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(c);
                        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(c);
                        alertDialogBuilderUserInput.setView(mView);
                        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
                        alertDialogBuilderUserInput
                                .setCancelable(false)
                                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {

                                        //Toast.makeText(c, "Entered: "+userInputDialogEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                                        // ToDo get user input here
                                        if(userInputDialogEditText.getText().toString().equals("rohitrohit")){

                                            //Toast.makeText(c, "You have Access!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(),FeedBackActivity.class);
                                            startActivity(intent);


                                        }
                                        else{
                                            Toast.makeText(MainActivity.this, "You Do not have the access to this feature!", Toast.LENGTH_SHORT).show();
                                        }



                                    }
                                });





                        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                        alertDialogAndroid.show();









                return true;
            default:
                return false;
        }
    }

    /**
     * Display Progress bar while Logging in
     */

    private void displayProgressDialog() {
        pDialog.setMessage("Logging in.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }
    private void displayProgressFeedDialog() {
        pDialog.setMessage("Submitting.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        displayProgressDialog();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Login Failed: ", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }

                });
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();

        TextView displayName = findViewById(R.id.displayName);
        ImageView profileImage = findViewById(R.id.profilePic);
        if (user != null) {
            displayName.setText(user.getDisplayName());
            displayName.setVisibility(View.VISIBLE);
            // Loading profile image
            Uri profilePicUrl = user.getPhotoUrl();
            if (profilePicUrl != null) {
                Glide.with(this).load(profilePicUrl)
                        .into(profileImage);
            }
            profileImage.setVisibility(View.GONE);
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
          //  findViewById(R.id.toTextVIew).setVisibility(View.VISIBLE);
            //findViewById(R.id.toEditText).setVisibility(View.VISIBLE);
            findViewById(R.id.toSpinner).setVisibility(View.VISIBLE);
          //  findViewById(R.id.radioGroup).setVisibility(View.VISIBLE);
            findViewById(R.id.TagsTextVIew).setVisibility(View.VISIBLE);
            findViewById(R.id.TagsEditText).setVisibility(View.VISIBLE);
            findViewById(R.id.ToEditTextMulti).setVisibility(View.VISIBLE);
//            findViewById(R.id.checkCoord).setVisibility(View.VISIBLE);
//            findViewById(R.id.checkExComm).setVisibility(View.VISIBLE);
            findViewById(R.id.FeedBackTextVIew).setVisibility(View.VISIBLE);
            findViewById(R.id.FeedbackEditText).setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);

        } else {
            displayName.setVisibility(View.GONE);
         //   findViewById(R.id.radioGroup).setVisibility(View.GONE);
            profileImage.setVisibility(View.GONE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.toTextVIew).setVisibility(View.GONE);
            //findViewById(R.id.toEditText).setVisibility(View.GONE);
            findViewById(R.id.ToEditTextMulti).setVisibility(View.GONE);
//            findViewById(R.id.checkCoord).setVisibility(View.GONE);
//            findViewById(R.id.checkExComm).setVisibility(View.GONE);
            findViewById(R.id.toSpinner).setVisibility(View.GONE);
            findViewById(R.id.TagsTextVIew).setVisibility(View.GONE);
            findViewById(R.id.TagsEditText).setVisibility(View.GONE);
            findViewById(R.id.FeedBackTextVIew).setVisibility(View.GONE);
            findViewById(R.id.FeedbackEditText).setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);

        }
    }

    private void hideProgressDialog() {
        pDialog.dismiss();
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
        toSelected = item;
        if( !(toEditTextMulti.getText().toString().isEmpty()) ) {
            String prevString = toEditTextMulti.getText().toString();
            prevString = prevString+"/"+item;
            toEditTextMulti.setText(prevString);
            toEditTextMulti.setSelection(toEditTextMulti.getText().length());
        }else{
            toEditTextMulti.setText(item);
            toEditTextMulti.setSelection(toEditTextMulti.getText().length());
        }




    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class SendRequest extends AsyncTask<String, Void, String> {


        protected void onPreExecute(){
            displayProgressFeedDialog();
        }

        protected String doInBackground(String... arg0) {

            try{

                //Enter script URL Here
                URL url = new URL("https://script.google.com/macros/s/AKfycbyn6lYv0KhSfJfcFKGfo1ED8U_QUdYlvFaKuTSCGY6L8XpTVfs/exec?");

                JSONObject postDataParams = new JSONObject();

                //Passing scanned code as parameter
//                RadioButton rb = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
//                boolean isCoord=false;
//                if(rb.getText().equals("Coord")){
//                    isCoord = true;
//                }

                postDataParams.put("user",mAuth.getCurrentUser().getDisplayName());
                postDataParams.put("to",toEditTextMulti.getText().toString().trim());
                postDataParams.put("tags",tagsEdit.getText().toString().trim());
                postDataParams.put("feedback",feedEdit.getText().toString());


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
            Toast.makeText(c, "Thank you for the Feedback, We will try to work on it ASAP.", Toast.LENGTH_SHORT).show();

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