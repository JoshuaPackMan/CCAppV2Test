package e.android.mysqldemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CCardSelectFragment extends Fragment {
    public static final String USER_SELECTED_CARDS = "userCards.txt";
    private boolean userCardsOnFile;
    String[] businesses;
    private CCardAdapterForHomeScreen cardAdapter;
    private RecyclerView cardRV;
    private String[] userCards;
    private List<CardListData> cardData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        businesses = args.getStringArray("businesses");
        cardData = new ArrayList<>();

        return inflater.inflate(R.layout.activity_ccard_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button cardChangeBtn = getView().findViewById(R.id.cardChangeBtn);
        cardChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardChangeBtnPressed();
            }
        });

        Button cardSelectNextBtn = getView().findViewById(R.id.cardNextBtn);
        cardSelectNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cCardSelectNextBtn();
            }
        });

        userCardsOnFile = false;
        userCards = getUserCardsFromInternalStorage();

        if(userCardsOnFile){
            displayUserCards();
        } else {
            //write a card to internal storage to prevent a back button endless loop
            //(because if you hit back from cardSelectFragment then you'll come back here
            //which will send you back to cardSelectFragment because you don't have any cards
            String[] fillerCard = {"Bank of America"};
            CardSelectFragment.writeSelectedCardsToInternalStorage(fillerCard,
                    CCardSelectFragment.USER_SELECTED_CARDS,
                    getActivity());


            Bundle bundle = new Bundle();
            bundle.putStringArray("businesses", businesses);
            CardSelectFragment cardSelectFragment = new CardSelectFragment();
            cardSelectFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, cardSelectFragment, "cardSelectFragment")
                    .addToBackStack(null)
                    .commit();
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

            cardRV = getView().findViewById(R.id.CCardRView);
            cardAdapter = new CCardAdapterForHomeScreen(getContext(), cardData);
            cardRV.setAdapter(cardAdapter);
            cardRV.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }



    private void cardChangeBtnPressed(){
        Bundle bundle = new Bundle();
        bundle.putStringArray("businesses", businesses);
        CardSelectFragment cardSelectFragment = new CardSelectFragment();
        cardSelectFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, cardSelectFragment, "cCardSelectFragment")
                .addToBackStack(null)
                .commit();
    }

    public void cCardSelectNextBtn(){
        Bundle bundle = new Bundle();
        bundle.putStringArray("businesses", businesses);
        bundle.putStringArray("cards", userCards);
        RewardDisplayFragment rewardDisplayFragment = new RewardDisplayFragment();
        rewardDisplayFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, rewardDisplayFragment, "cCardSelectFragment")
                .addToBackStack(null)
                .commit();
    }

    private String[] getUserCardsFromInternalStorage(){
        String resultFromFile = "";
        FileInputStream fis = null;
        cardData.clear();

        try {
            fis = getActivity().openFileInput(USER_SELECTED_CARDS);
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
