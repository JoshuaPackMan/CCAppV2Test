package e.android.mysqldemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;


public class CardSelect extends AppCompatActivity {
    String[] businesses;
    private RecyclerView rv;
    private CCardAdapter adapter;
    private List<CardListData> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_select);
        Bundle extras = getIntent().getExtras();
        businesses = extras.getStringArray("businesses");

        data = new ArrayList<>();

        CreditCardAsyncFetch backgroundWorker = new CreditCardAsyncFetch();
        backgroundWorker.execute("start");
    }

    private class CreditCardAsyncFetch extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String login_url = "http://mathwithpack.com/ZTTaPPweSgHaT8x22loginFetchCards.php";
            if(type.equals("start")) {
                try {
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
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
                Toast.makeText(CardSelect.this, "No Results found for entered query", Toast.LENGTH_LONG).show();
            } else{
                try{
                    JSONArray results = new JSONArray(result);
                    int i = -1;
                    while(true){
                        try {
                            i++;
                            JSONObject cardJSON = results.getJSONObject(i);
                            String card = cardJSON.getString("CardCompany");
                            CardListData cardListData = new CardListData(card, CardListData.CardColor.WHITE);
                            data.add(cardListData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            break;
                        }
                    }

                    //set adapter and recycler view
                    rv = findViewById(R.id.CCardRView);
                    adapter = new CCardAdapter(CardSelect.this, data);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(CardSelect.this));
                    adapter.setOnItemClickListener(new CCardAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            data.get(position).cardClicked();
                            adapter.notifyItemChanged(position);
                        }
                    });
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void cardNextBtn(View v){
        List<String> selectedCards = new ArrayList<>();
        for(CardListData d: data){
            if(d.getColor() == CardListData.CardColor.BLUE){
                selectedCards.add(d.getcCard());
            }
        }

        Intent rewardDisplayIntent = new Intent(this, RewardDisplay.class);
        rewardDisplayIntent.putExtra("businesses", businesses);
        rewardDisplayIntent.putExtra("cards", selectedCards.toArray(new String[selectedCards.size()]));
        startActivity(rewardDisplayIntent);
    }
}
