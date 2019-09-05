package e.android.mysqldemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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

public class BusinessDisplayFragment extends Fragment {
    private RecyclerView businessRV;
    private BusinessAdapter businessAdapter;
    private List<BusinessListData> businessData;
    private SQLiteDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        businessData = new ArrayList<>();

        db = getActivity().openOrCreateDatabase("CardDB", Context.MODE_PRIVATE, null);
        db.execSQL("create table if not exists CardsTable (Card text);");

        BusinessAsyncFetch backgroundWorker = new BusinessAsyncFetch();
        backgroundWorker.execute("start");

        return inflater.inflate(R.layout.fragment_business_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getView().findViewById(R.id.businessNextBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> selectedBusinesses = new ArrayList<>();
                for(BusinessListData d: businessData){
                    if(d.getColor() == BusinessListData.BusinessColor.BLUE){
                        String businessToAdd = d.getBusiness();
                        selectedBusinesses.add(businessToAdd);
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putStringArray("businesses", selectedBusinesses.toArray(
                        new String[selectedBusinesses.size()]));
                CCardSelectFragment cCardSelectFragment = new CCardSelectFragment();
                cCardSelectFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, cCardSelectFragment, "cCardSelectFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        super.onViewCreated(view, savedInstanceState);
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
                Toast.makeText(getContext(), "No Results found for entered query", Toast.LENGTH_LONG).show();
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
                    businessRV = getView().findViewById(R.id.BusinessRView);
                    businessAdapter = new BusinessAdapter(getContext(), businessData);
                    businessRV.setAdapter(businessAdapter);
                    businessRV.setLayoutManager(new LinearLayoutManager(getContext()));
                    businessAdapter.setOnItemClickListener(new BusinessAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            businessData.get(position).cardClicked();
                            businessAdapter.notifyItemChanged(position);
                        }
                    });
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
