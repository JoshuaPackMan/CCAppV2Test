package e.android.mysqldemo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_display);
        Bundle extras = getIntent().getExtras();
        businesses = extras.getStringArray("businesses");
        selectedCards = extras.getStringArray("cards");

        data = new ArrayList<>();

        //set adapter and recycler view
        rv = findViewById(R.id.RewardRView);
        adapter = new RewardAdapter(RewardDisplay.this, data);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(RewardDisplay.this));

        for(String business: businesses){
            for(String cCard: selectedCards){
                RewardsAsyncFetch backgroundWorker = new RewardsAsyncFetch();
                backgroundWorker.execute("rewards", business, cCard);
            }
        }
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
            if(result.equals("No Results Found")) {
                Toast.makeText(RewardDisplay.this, "No Results found for entered query", Toast.LENGTH_LONG).show();
            } else{
                try{
                    JSONObject results = new JSONObject(result);
                    JSONObject rewardJSON = results.getJSONObject("0");
                    String reward = rewardJSON.getString("Reward");
                    String business = rewardJSON.getString("Business");
                    String card = rewardJSON.getString("CardCompany");
                    RewardListData rewardListData = new RewardListData(card, reward, business);
                    data.add(rewardListData);

                    adapter.notifyDataSetChanged();
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
