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
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private RecyclerView rv;
    private BusinessAdapter adapter;
    private List<BusinessListData> data;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new ArrayList<>();

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
                            data.add(businessListData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            break;
                        }
                    }

                    //set adapter and recycler view
                    rv = findViewById(R.id.BusinessRView);
                    adapter = new BusinessAdapter(MainActivity.this, data);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    adapter.setOnItemClickListener(new BusinessAdapter.OnItemClickListener() {
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

    public void businessNextBtn(View v){
        List<String> selectedBusinesses = new ArrayList<>();
        for(BusinessListData d: data){
            if(d.getColor() == BusinessListData.BusinessColor.BLUE){
                selectedBusinesses.add(d.getBusiness());
            }
        }

        Intent cCardDisplayIntent = new Intent(this, CardSelect.class);
        cCardDisplayIntent.putExtra("businesses", selectedBusinesses.toArray(new String[selectedBusinesses.size()]));
        startActivity(cCardDisplayIntent);
    }
}
