package e.android.mysqldemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RewardDisplay extends AppCompatActivity {
    String[] businesses;
    String[] selectedCards;
    private List<RewardListData> data;
    private RecyclerView rv;
    private RewardAdapter adapter;
    /*
    private int expectedNumResults;
    private int numReturnedResults;
    private boolean resultsFound;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_display);
        Bundle extras = getIntent().getExtras();
        businesses = extras.getStringArray("businesses");
        selectedCards = extras.getStringArray("cards");

        /*
        expectedNumResults = 0;
        numReturnedResults = 0;
        resultsFound = false;
        */
        data = new ArrayList<>();

        //set adapter and recycler view
        rv = findViewById(R.id.RewardRView);
        adapter = new RewardAdapter(RewardDisplay.this, data);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(RewardDisplay.this));

        int length = 0;
        int businessLength = businesses.length;
        int cardsLength = selectedCards.length;
        if(businessLength > cardsLength){
            length = businessLength;
        } else {
            length = cardsLength;
        }

        JSONArray jsonArray = new JSONArray();
        for(int x=0; x<length; x++){
            JSONObject jsonObj = new JSONObject();
            String business = "";
            String card = "";

            if(x < businessLength){
                business = businesses[x];
            }

            if(x < cardsLength){
                card = selectedCards[x];
            }

            try{
                jsonObj.put("business",business);
                jsonObj.put("card",card);
                jsonArray.put(jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RewardsAsyncFetch backgroundWorker = new RewardsAsyncFetch();
        backgroundWorker.execute("rewards", jsonArray.toString());
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
            String login_url = "http://mathwithpack.com/ZXyZZkMnNaTr238123FetchRewardsJSON.php";
            if(type.equals("rewards")) {
                try {
                    String jsonInputString = params[1];
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json; utf-8");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    byte[] input = jsonInputString.getBytes("utf-8");
                    outputStream.write(input, 0, input.length);
                    outputStream.flush();
                    outputStream.close();
                    outputStream.close();
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
                    String result="";
                    String line;
                    while((line = bufferedReader.readLine())!= null) {
                        result += line;
                    }
                    bufferedReader.close();
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
            if(result.equals("")){
                Button searchAgainWithAllCardsBtn = findViewById(R.id.searchAgainWithAllCardsBtn);
                searchAgainWithAllCardsBtn.setVisibility(View.VISIBLE);
                TextView resultsTV = findViewById(R.id.resultsTV);
                resultsTV.setText("No Results Found:");

                TextView searchAgainWithAllCardsTV = findViewById(R.id.searchAgainWithAllCardsTV);
                searchAgainWithAllCardsTV.setText("If you want to see the rewards you could get at your selected " +
                        "business/businesses with different cards, hit the button below:");

            } else{
                Button searchAgainWithAllCardsBtn = findViewById(R.id.searchAgainWithAllCardsBtn);
                searchAgainWithAllCardsBtn.setVisibility(View.INVISIBLE);
                try{
                    JSONArray jsonArray = new JSONArray(result);
                    int length = jsonArray.length();
                    for(int i=0; i<length; i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String reward = obj.getString("Reward");
                        String business = decodeSingleQuoteIfPresent(obj.getString("Business"));
                        //business += ":";
                        data.add(new RewardListData(obj.getString("CardCompany"), reward, business));
                    }

                    adapter.notifyDataSetChanged();
                    TextView resultsTV = findViewById(R.id.resultsTV);
                    resultsTV.setText("Results:");
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private String decodeSingleQuoteIfPresent(String s){
        int indexOfSingleQuote = s.indexOf("&#39");
        if(indexOfSingleQuote == -1){
            return s;
        } else {
            return decodeSingleQuoteIfPresent(decodeSingleQuote(s, indexOfSingleQuote));
        }
    }

    private String decodeSingleQuote(String s, int indexOfSingleQuote){
        String sub1 = s.substring(0, indexOfSingleQuote);
        String sub2 = s.substring(indexOfSingleQuote+4);
        return sub1+"'"+sub2;
    }

    private String[] getUserCardsFromInternalStorage(){
        String resultFromFile = "";
        FileInputStream fis = null;
        //cardData.clear();

        try {
            fis = openFileInput(CardSelect.ALL_CARDS);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text);
            }

            resultFromFile = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        List<String> cardsArrayList = new ArrayList<>();
        String temp = "";
        for(int i=1;i<resultFromFile.length()-1;i++){
            char currentChar = resultFromFile.charAt(i);
            if(currentChar == ','){
                cardsArrayList.add(temp);
                temp = "";
            } else if(i == resultFromFile.length()-2){
                temp += currentChar;
                cardsArrayList.add(temp);
            } else {
                temp += currentChar;
            }
        }

        String[] cardsFromFile = cardsArrayList.toArray(new String[cardsArrayList.size()]);
        for(int x=1;x<cardsFromFile.length;x++){
            cardsFromFile[x] = cardsFromFile[x].substring(1);
        }

        /*
        if(userCardsFromFile.length == 0){
            userCardsOnFile = false;
        } else {
            userCardsOnFile = true;
        }*/
        return cardsFromFile;
    }

    public void searchAgainWithAllCards(View v){
        String[] allCards = getUserCardsFromInternalStorage();
        Intent rewardDisplayIntent = new Intent(this, RewardDisplay.class);
        rewardDisplayIntent.putExtra("businesses", businesses);
        rewardDisplayIntent.putExtra("cards", allCards);
        startActivity(rewardDisplayIntent);
    }
}
