package e.android.mysqldemo;

import android.app.Activity;
import android.os.AsyncTask;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class CardSelectFragment extends Fragment {
    private String[] businesses;
    public static final String ALL_CARDS = "allCards.txt";
    private RecyclerView rv;
    private CCardAdapter adapter;
    private List<CardListData> data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        try{
            businesses = args.getStringArray("businesses");
        } catch(NullPointerException e){ }

        data = new ArrayList<>();

        CreditCardAsyncFetch backgroundWorker = new CreditCardAsyncFetch();
        backgroundWorker.execute("start");

        return inflater.inflate(R.layout.activity_card_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button cardNextButton = getView().findViewById(R.id.cardNextBtn);
        cardNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardNextBtn();
            }
        });
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
                Toast.makeText(getContext(), "No Results found for entered query", Toast.LENGTH_LONG).show();
            } else{
                try{
                    JSONArray results = new JSONArray(result);
                    int i = -1;
                    List<String> allCards = new ArrayList<>();
                    while(true){
                        try {
                            i++;
                            JSONObject cardJSON = results.getJSONObject(i);
                            String card = cardJSON.getString("CardCompany");
                            allCards.add(card);
                            CardListData cardListData = new CardListData(card, CardListData.CardColor.WHITE);
                            data.add(cardListData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            break;
                        }
                    }

                    writeSelectedCardsToInternalStorage(allCards.toArray(new String[allCards.size()]),
                            CardSelectFragment.ALL_CARDS, getActivity());

                    //set adapter and recycler view
                    rv = getView().findViewById(R.id.CCardRView);
                    adapter = new CCardAdapter(getContext(), data);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(getContext()));
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

    public void cardNextBtn(){
        List<String> selectedCards = new ArrayList<>();
        for(CardListData d: data){
            if(d.getColor() == CardListData.CardColor.BLUE){
                selectedCards.add(d.getcCard());
            }
        }

        String[] selectedCardsArray = selectedCards.toArray(new String[selectedCards.size()]);


        if(selectedCardsArray.length == 0){
            Toast.makeText(getContext(), "Please select at least one card", Toast.LENGTH_LONG).
                    show();
            return;
        }

        //write the selected cards to internal storage
        writeSelectedCardsToInternalStorage(selectedCardsArray, CCardSelectFragment.USER_SELECTED_CARDS,
                getActivity());

        Bundle bundle = new Bundle();
        bundle.putStringArray("businesses", businesses);
        bundle.putStringArray("cards", selectedCardsArray);
        RewardDisplayFragment rewardDisplay = new RewardDisplayFragment();
        rewardDisplay.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, rewardDisplay, "rewardDisplayFragment")
                .addToBackStack(null)
                .commit();
    }

    public static void writeSelectedCardsToInternalStorage(String[] userCards, String filename,
                                                     Activity activity){
        //clear file of any previous cards
        try {
            PrintWriter pw = new PrintWriter(filename);
            pw.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }



        String userCardsString = Arrays.toString(userCards);
        FileOutputStream fos = null;

        try {
            fos = activity.openFileOutput(filename, MODE_PRIVATE);
            fos.write(userCardsString.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
