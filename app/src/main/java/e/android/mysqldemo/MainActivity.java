package e.android.mysqldemo;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView businessRV;
    private BusinessAdapter businessAdapter;
    private List<BusinessListData> businessData;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        businessData = new ArrayList<>();

        db = this.openOrCreateDatabase("CardDB", Context.MODE_PRIVATE, null);
        db.execSQL("create table if not exists CardsTable (Card text);");

        BusinessAsyncFetch backgroundWorker = new BusinessAsyncFetch();
        backgroundWorker.execute("start");
    }

    private class BusinessAsyncFetch extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String login_url = "http://mathwithpack.com/YYXXxZzPp829382adJloginFetchBusinesses.php";
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
                Toast.makeText(MainActivity.this, "No Results found for entered query", Toast.LENGTH_LONG).show();
            } else{
                try{
                    JSONArray results = new JSONArray(result);
                    int i = -1;
                    while(true){
                        try {
                            i++;
                            JSONObject businessJSON = results.getJSONObject(i);
                            String business = businessJSON.getString("Business");
                            /*
                            if(business.equals("Joes Crab Shack")){
                                business = "Joe's Crab Shack";
                            } else if(business.equals("Kiehls")) {
                                business = "Kiehl's";
                            } else if(business.equals("Macys")){
                                business = "Macy's";
                            }*/
                            BusinessListData businessListData = new BusinessListData(business, BusinessListData.BusinessColor.WHITE);
                            businessData.add(businessListData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            break;
                        }
                    }

                    //alphabetically sort the business list
                    Collections.sort(businessData, new Comparator<BusinessListData>() {
                        @Override
                        public int compare(BusinessListData a, BusinessListData b) {
                            String b1 = a.getBusiness();
                            String b2 = b.getBusiness();
                            return b1.compareToIgnoreCase(b2);
                        }
                    });

                    //set adapter and recycler view for businesses
                    businessRV = findViewById(R.id.BusinessRView);
                    businessAdapter = new BusinessAdapter(MainActivity.this, businessData);
                    businessRV.setAdapter(businessAdapter);
                    businessRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    businessAdapter.setOnItemClickListener(new BusinessAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            businessData.get(position).cardClicked();
                            businessAdapter.notifyItemChanged(position);
                        }
                    });

                    /*
                        userCards = getUserCardsFromInternalStorage();
                    //Log.v("mytag", Arrays.toString(userCards));
                    if(userCardsOnFile){
                        //set recycler view with user cards
                        //first turn userCards into an array list of CardListData
                        for(String card: userCards){
                            CardListData cData = new CardListData(card, CardListData.CardColor.WHITE);
                            cardData.add(cData);
                        }

                        cardRV = findViewById(R.id.CCardHomeScreenRView);
                        //Log.v("mytag",printCardData());
                        cardAdapter = new CCardAdapterForHomeScreen(MainActivity.this, cardData);
                        cardRV.setAdapter(cardAdapter);
                        cardRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    }*/
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void businessNextBtn(View v){
        List<String> selectedBusinesses = new ArrayList<>();
        for(BusinessListData d: businessData){
            if(d.getColor() == BusinessListData.BusinessColor.BLUE){
                String businessToAdd = d.getBusiness();
                selectedBusinesses.add(encodeSingleQuoteIfPresent(businessToAdd));
                /*
                if(businessToAdd.equals("Joe's Crab Shack")){
                    selectedBusinesses.add("Joes Crab Shack");
                } else if(businessToAdd.equals("Kiehl's")) {
                    selectedBusinesses.add("Kiehls");
                } else if(businessToAdd.equals("Macy's")){
                    selectedBusinesses.add("Macys");
                } else {
                    selectedBusinesses.add(businessToAdd);
                }*/
            }
        }

        Intent cCardSelectIntent = new Intent(this, CCardSelectActivity.class);
        cCardSelectIntent.putExtra("businesses", selectedBusinesses.toArray(
                new String[selectedBusinesses.size()]));
        startActivity(cCardSelectIntent);
        /*
        if(userCardsOnFile){
            Intent rewardDisplayIntent = new Intent(this, RewardDisplay.class);
            rewardDisplayIntent.putExtra("businesses", selectedBusinesses.toArray(new String[selectedBusinesses.size()]));
            rewardDisplayIntent.putExtra("cards", userCards);
            startActivity(rewardDisplayIntent);
        } else {
            Intent cCardDisplayIntent = new Intent(this, CardSelect.class);
            cCardDisplayIntent.putExtra("businesses", selectedBusinesses.toArray(new String[selectedBusinesses.size()]));
            cCardDisplayIntent.putExtra("goal", "rewards");
            startActivity(cCardDisplayIntent);
        }*/
    }

    private String encodeSingleQuoteIfPresent(String s){
        int indexOfSingleQuote = s.indexOf("'");
        if(indexOfSingleQuote == -1){
            return s;
        } else {
            return encodeSingleQuoteIfPresent(encodeSingleQuote(s, indexOfSingleQuote));
        }
    }

    private String encodeSingleQuote(String s, int indexOfSingleQuote){
        String sub1 = s.substring(0, indexOfSingleQuote);
        String sub2 = s.substring(indexOfSingleQuote+1);
        return sub1+"&#39"+sub2;
    }

    /*
    private String printCardData(){
        String[] cards = new String[cardData.size()];
        int i = 0;
        for(CardListData c: cardData){
            cards[i] = c.getcCard();
            i++;
        }

        return Arrays.toString(cards);
    }*/
}
