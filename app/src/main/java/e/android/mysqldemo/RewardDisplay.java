package e.android.mysqldemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class RewardDisplay extends AppCompatActivity {
    String[] businesses;
    String[] selectedCards;
    private List<RewardListData> data;
    private RecyclerView rv;
    private RewardAdapter adapter;
    private int expectedNumResults;
    private int numReturnedResults;
    private boolean resultsFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_display);
        Bundle extras = getIntent().getExtras();
        businesses = extras.getStringArray("businesses");
        selectedCards = extras.getStringArray("cards");

        expectedNumResults = 0;
        numReturnedResults = 0;
        resultsFound = false;

        data = new ArrayList<>();

        //set adapter and recycler view
        rv = findViewById(R.id.RewardRView);
        adapter = new RewardAdapter(RewardDisplay.this, data);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(RewardDisplay.this));

        //RewardsAsyncFetch backgroundWorker = new RewardsAsyncFetch();
        //backgroundWorker.execute("rewards", "lol", "lol");

        for(String business: businesses){
            for(String cCard: selectedCards){
                RewardsAsyncFetch backgroundWorker = new RewardsAsyncFetch();
                backgroundWorker.execute("rewards", business, cCard);
                expectedNumResults++;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    public void searchAgainBtn(View v) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    private class RewardsAsyncFetch extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String login_url = "http://mathwithpack.com/XZaBBaGazi28281loginFetchRewards.php";
            if(type.equals("rewards")) {
                try {
                    String business = params[1];
                    String cCard = params[2];
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("business","UTF-8")+"="+URLEncoder.encode(business,"UTF-8")+"&"
                            +URLEncoder.encode("card","UTF-8")+"="+URLEncoder.encode(cCard,"UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    outputStream.close();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                    String result="";
                    String line;
                    while((line = bufferedReader.readLine())!= null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return result;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            numReturnedResults++;
            if(result.equals("No Results Found")) {
                Toast.makeText(RewardDisplay.this, "No Results found for entered query", Toast.LENGTH_LONG).show();
            } else if(!result.equals("")){
                try{
                    /*
                    JSONArray results = new JSONArray(result);
                    for(int i=0; i< results.length(); i++){
                        JSONObject obj = results.getJSONObject(i);
                        String reward = obj.getString("Reward");
                        String business = obj.getString("Business");
                        String card = obj.getString("CardCompany");
                        RewardListData rewardListData = new RewardListData(card, reward, business);
                        data.add(rewardListData);
                    }*/
                    resultsFound = true;
                    TextView resultsTV = findViewById(R.id.resultsTV);
                    resultsTV.setText("Results:");

                    JSONObject obj1 = new JSONObject(result);
                    JSONObject obj = obj1.getJSONObject("0");
                    String reward = obj.getString("Reward");
                    String business = obj.getString("Business");
                    String card = obj.getString("CardCompany");
                    RewardListData rewardListData = new RewardListData(card, reward, business);
                    data.add(rewardListData);

                    adapter.notifyDataSetChanged();
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }

            if(numReturnedResults==expectedNumResults){
                if(!resultsFound){
                    TextView resultsTV = findViewById(R.id.resultsTV);
                    resultsTV.setText("No results found :(");
                }
            }
        }
    }
}
