package e.android.mysqldemo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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


public class MainActivity extends AppCompatActivity {
    EditText fieldAEt;
    private RecyclerView rv;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fieldAEt = findViewById(R.id.etFieldA);
    }

    public void onLogin(View v){
        String fieldA = fieldAEt.getText().toString();
        //String fieldB = fieldBEt.getText().toString();
        String type = "login";
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        AsyncFetch backgroundWorker = new AsyncFetch();
        backgroundWorker.execute(type, fieldA);
    }

    private class AsyncFetch extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String login_url = "http://mathwithpack.com/YYXXxZzPp829382adJlogin.php";
            if(type.equals("login")) {
                try {
                    String fieldA = params[1];
                    //String fieldB = params[2];
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                /*String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(fieldA,"UTF-8")+"&"
                        +URLEncoder.encode("surname","UTF-8")+"="+URLEncoder.encode(fieldB,"UTF-8");
                */
                    String post_data = URLEncoder.encode("business","UTF-8")+"="+URLEncoder.encode(fieldA,"UTF-8");
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
            List<ListData> data = new ArrayList<>();
            if(result.equals("No Results Found")) {
                Toast.makeText(MainActivity.this, "No Results found for entered query", Toast.LENGTH_LONG).show();
            } else{
            /*
            try{
                JSONObject obj = new JSONObject(result);
                JSONObject zero = obj.getJSONObject("0");
                String cardCompany0 = zero.getString("CardCompany");
                String reward0 = zero.getString("Reward");
                JSONObject one = obj.getJSONObject("1");
                String cardCompany1 = one.getString("CardCompany");
                String reward1 = one.getString("Reward");
                String[] results = new String[4];
                results[0] = cardCompany0;
                results[1] = reward0;
                results[2] = cardCompany1;
                results[3] = reward1;
                Log.v("mytag",Arrays.toString(results));
            } catch(JSONException e){
                e.printStackTrace();
            }
            */
                try{
                    JSONObject obj = new JSONObject(result);
                    Log.v("mytag","result: "+result);
                    int i = -1;
                    while(true){
                        try {
                            i++;
                            String index = Integer.toString(i);
                            Log.v("mytag","index: "+index);
                            JSONObject cardReward = obj.getJSONObject(index);
                            String cardCompany = cardReward.getString("CardCompany");
                            String reward = cardReward.getString("Reward");
                            ListData listData = new ListData(cardCompany,reward);
                            data.add(listData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            break;
                        }
                    }

                    rv = findViewById(R.id.RView);
                    adapter = new MyAdapter(MainActivity.this, data);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
