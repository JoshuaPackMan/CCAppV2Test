package e.android.mysqldemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CCardSelectActivity extends AppCompatActivity {
    public static final String USER_SELECTED_CARDS = "userCards.txt";
    private boolean userCardsOnFile;
    String[] businesses;
    private CCardAdapterForHomeScreen cardAdapter;
    private RecyclerView cardRV;
    private String[] userCards;
    private List<CardListData> cardData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ccard_select);

        Bundle extras = getIntent().getExtras();
        businesses = extras.getStringArray("businesses");
        cardData = new ArrayList<>();
        userCardsOnFile = false;
        userCards = getUserCardsFromInternalStorage();

        if(userCardsOnFile){
            displayUserCards();
        } else {
            Intent cCardDisplayIntent = new Intent(this, CardSelect.class);
            cCardDisplayIntent.putExtra("businesses",businesses);
            startActivity(cCardDisplayIntent);
        }

    }

    private void displayUserCards(){
        if(userCardsOnFile){
            //set recycler view with user cards
            //first turn userCards into an array list of CardListData
            for(String card: userCards){
                CardListData cData = new CardListData(card, CardListData.CardColor.WHITE);
                cardData.add(cData);
            }

            cardRV = findViewById(R.id.CCardRView);
            cardAdapter = new CCardAdapterForHomeScreen(CCardSelectActivity.this, cardData);
            cardRV.setAdapter(cardAdapter);
            cardRV.setLayoutManager(new LinearLayoutManager(CCardSelectActivity.this));
        }
    }



    public void cardChangeBtnPressed(View v){
        Intent cCardDisplayIntent = new Intent(this, CardSelect.class);
        cCardDisplayIntent.putExtra("businesses",businesses);
        startActivity(cCardDisplayIntent);
    }

    public void cCardSelectNextBtn(View v){
        Intent rewardDisplayIntent = new Intent(this, RewardDisplay.class);
        rewardDisplayIntent.putExtra("businesses", businesses);
        rewardDisplayIntent.putExtra("cards", userCards);
        startActivity(rewardDisplayIntent);
    }

    private String[] getUserCardsFromInternalStorage(){
        String resultFromFile = "";
        FileInputStream fis = null;
        cardData.clear();

        try {
            fis = openFileInput(USER_SELECTED_CARDS);
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

        if(userCardsFromFile.length == 0){
            userCardsOnFile = false;
        } else {
            userCardsOnFile = true;
        }
        return userCardsFromFile;
    }
}
