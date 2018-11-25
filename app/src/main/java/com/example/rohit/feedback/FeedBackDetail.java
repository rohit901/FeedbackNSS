package com.example.rohit.feedback;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FeedBackDetail extends AppCompatActivity {
    Intent intent;
    int position;
    String result;
    TextView feedbackData;
    ListView detailedLv;
    ArrayList<String> timeStamps;
    ArrayList<Integer> specificpos;
    ArrayList<Integer> timestamppos;
    ArrayAdapter arrayAdapter;
    String multipleTagText;
    int relativePos;
    JSONObject jsonPart;
    String titleFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_detail);
        //feedbackData = (TextView)findViewById(R.id.feedbackData);

        timeStamps = new ArrayList<String>();
        detailedLv = (ListView)findViewById(R.id.detailedFeedList);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,timeStamps);

        timestamppos = new ArrayList<Integer>();

        detailedLv.setAdapter(arrayAdapter);
        //Toast.makeText(this, "hi bro!", Toast.LENGTH_SHORT).show();






        intent = getIntent();
        //position = intent.getIntExtra("pos",0);
        specificpos = new ArrayList<Integer>();

        specificpos = intent.getIntegerArrayListExtra("specificpos"); ///specifc pos +2 is the row number in sheets
        titleFeed = intent.getStringExtra("title");
        result = intent.getStringExtra("result");
        detailedLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),TimeStampActivity.class);
                intent.putExtra("pos",timestamppos.get(position));
                intent.putExtra("result",result);
                startActivity(intent);
            }
        });
        //Toast.makeText(this, "pos is "+position, Toast.LENGTH_SHORT).show();
        //feedbackData.setText();
        try {
            int flag=0;
            JSONObject jsonObject = new JSONObject(result);
            String feedbacks = jsonObject.getString("Sheet1");
            JSONArray arr = new JSONArray(feedbacks);

                for(int i=0;i<specificpos.size();i++) {

                        jsonPart = arr.getJSONObject(specificpos.get(i));
                        String[] splitTags = jsonPart.getString("Tags").split(",");
                        if (splitTags.length == 1) {
                            if (jsonPart.getString("Tags").equals(titleFeed)) {
                                timestamppos.add(specificpos.get(i));
                                timeStamps.add(jsonPart.getString("Timestamp"));
                            }
                        } else if (splitTags.length > 1) {
                            for (int k = 0; k < splitTags.length; k++) {
                                multipleTagText = splitTags[k];
                                if (jsonPart.getString("Tags").contains(titleFeed)) {
                                    timestamppos.add(specificpos.get(i));
                                    timeStamps.add(jsonPart.getString("Timestamp"));
                                    //flag=1;
                                    break;
                                }

                            }
                        }

                }
                arrayAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }







    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "hakai", Toast.LENGTH_SHORT).show();
        timeStamps.clear();
        timestamppos.clear();
        specificpos.clear();
    }
}
