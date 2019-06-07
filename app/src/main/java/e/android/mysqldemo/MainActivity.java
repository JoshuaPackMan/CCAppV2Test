package e.android.mysqldemo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private MyAdapter adapter;
    private List<BusinessListData> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new ArrayList<>();

        AsyncFetch backgroundWorker = new AsyncFetch();
        backgroundWorker.execute("start");
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
            if(type.equals("start")) {
                try {
                    //String fieldA = params[1];
                    //String fieldB = params[2];
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                /*String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(fieldA,"UTF-8")+"&"
                        +URLEncoder.encode("surname","UTF-8")+"="+URLEncoder.encode(fieldB,"UTF-8");
                */
                    //String post_data = URLEncoder.encode("business","UTF-8")+"="+URLEncoder.encode(fieldA,"UTF-8");
                    //bufferedWriter.write(post_data);
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
            //List<BusinessListData> data = new ArrayList<>();
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
                    rv = findViewById(R.id.RView);
                    adapter = new MyAdapter(MainActivity.this, data);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
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
}
