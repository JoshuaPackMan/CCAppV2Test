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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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


public class MainActivity extends AppCompatActivity {
    private RecyclerView businessRV;
    private RecyclerView cardRV;
    private BusinessAdapter businessAdapter;
    private CCardAdapterForHomeScreen cardAdapter;
    private List<BusinessListData> businessData;
    private List<CardListData> cardData;
    private SQLiteDatabase db;
    public static final String FILE_NAME = "userCards.txt";
    private boolean userCardsOnFile;
    private String[] userCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        businessData = new ArrayList<>();
        cardData = new ArrayList<>();
        userCardsOnFile = false;

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
                            BusinessListData businessListData = new BusinessListData(business, BusinessListData.BusinessColor.WHITE);
                            businessData.add(businessListData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            break;
                        }
                    }

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

                    userCards = getUserCardsFromInternalStorage();
                    if(userCardsOnFile){
                        //set recycler view with user cards
                        //first turn userCards into an array list of CardListData
                        for(String card: userCards){
                            CardListData cData = new CardListData(card, CardListData.CardColor.WHITE);
                            cardData.add(cData);
                        }

                        cardRV = findViewById(R.id.CCardHomeScreenRView);
                        cardAdapter = new CCardAdapterForHomeScreen(MainActivity.this, cardData);
                        cardRV.setAdapter(cardAdapter);
                        cardRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    }
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
                selectedBusinesses.add(d.getBusiness());
            }
        }

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
        }
    }

    public void cardChangeBtnPressed(View v){
        Intent cCardDisplayIntent = new Intent(this, CardSelect.class);
        cCardDisplayIntent.putExtra("goal", "change");
        startActivity(cCardDisplayIntent);
    }

    private String[] getUserCardsFromInternalStorage(){
        String resultFromFile = "";
        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);
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

        List<String> userCardsArrayList = new ArrayList<>();
        String temp = "";
        for(int i=1;i<resultFromFile.length()-1;i++){
            char currentChar = resultFromFile.charAt(i);
            if(currentChar == ','){
                userCardsArrayList.add(temp);
                temp = "";
            } else if(i == resultFromFile.length()-2){
                temp += currentChar;
                userCardsArrayList.add(temp);
            } else {
                temp += currentChar;
            }
        }

        String[] userCardsFromFile = userCardsArrayList.toArray(new String[userCardsArrayList.size()]);
        for(int x=1;x<userCardsFromFile.length;x++){
            userCardsFromFile[x] = userCardsFromFile[x].substring(1);
        }


        userCardsOnFile = true;
        return userCardsFromFile;
    }
}
