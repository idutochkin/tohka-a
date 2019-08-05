package com.idutochkin.run18.carpoling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
        import android.os.Bundle;
        import android.provider.Settings;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ProgressBar;
        import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;

        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private List<FeedRoute> feedRoutes;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;

    static final String LOG = "LOG";
    private static String url_db = "http://tohka-a.ru/api.php";

    public static final String APP_PREFERENCES = "SETTINGS";
    public static final String APP_PREFERENCES_SET_SETTINGS = "SET_SETTINGS";
    public static final String APP_PREFERENCES_CITY_FROM = "CITY_FROM";
    public static final String APP_PREFERENCES_POINT_FROM = "POINT_FROM";
    public static final String APP_PREFERENCES_CITY_TO = "CITY_TO";
    public static final String APP_PREFERENCES_POINT_TO = "POINT_TO";
    private SharedPreferences FilterData;
    private String filter;

    private static final String TAG_ROUTE_ID = "id";
    private static final String TAG_ROUTE_POINT_FROM= "point_from";
    private static final String TAG_ROUTE_POINT_TO = "point_to";
    private static final String TAG_ROUTE_DATE_FROM = "date_from";
    private static final String TAG_ROUTE_TIME_FROM = "time_from";
    private static final String TAG_ROUTE_MAX_PASSENGERS = "max_passengers";
    private static final String TAG_ROUTE_LEFT_PASSENGERS = "left_passengers";
    private static final String TAG_ROUTE_PRICE = "price";
    private static final String TAG_ROUTE_CHECK_ID = "check_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.feed_routes);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        filter = "";
        FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (FilterData.contains(APP_PREFERENCES_CITY_FROM)) {
            filter = filter+"CITY_FROM:"+FilterData.getString(APP_PREFERENCES_CITY_FROM, "")+"||";
        }
        if (FilterData.contains(APP_PREFERENCES_POINT_FROM)) {
            filter = filter+"POINT_FROM:"+FilterData.getString(APP_PREFERENCES_POINT_FROM, "")+"||";
        }
        if (FilterData.contains(APP_PREFERENCES_CITY_TO)) {
            filter = filter+"CITY_TO:"+FilterData.getString(APP_PREFERENCES_CITY_TO, "")+"||";
        }
        if (FilterData.contains(APP_PREFERENCES_POINT_TO)) {
            filter = filter+"POINT_TO:"+FilterData.getString(APP_PREFERENCES_POINT_TO, "");
        }

        new GetListRoutes().execute(filter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        filter = "";
        FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (FilterData.contains(APP_PREFERENCES_CITY_FROM)) {
            filter = filter+"CITY_FROM:"+FilterData.getString(APP_PREFERENCES_CITY_FROM, "")+"||";
        }
        if (FilterData.contains(APP_PREFERENCES_POINT_FROM)) {
            filter = filter+"POINT_FROM:"+FilterData.getString(APP_PREFERENCES_POINT_FROM, "")+"||";
        }
        if (FilterData.contains(APP_PREFERENCES_CITY_TO)) {
            filter = filter+"CITY_TO:"+FilterData.getString(APP_PREFERENCES_CITY_TO, "")+"||";
        }
        if (FilterData.contains(APP_PREFERENCES_POINT_TO)) {
            filter = filter+"POINT_TO:"+FilterData.getString(APP_PREFERENCES_POINT_TO, "");
        }

        new GetListRoutes().execute(filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            Intent filterActivity = new Intent(getApplicationContext(), FilterActivity.class);
            startActivity(filterActivity);
        }

        return super.onOptionsItemSelected(item);
    }


    private class GetListRoutes extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        protected Void doInBackground(String... filter) {
            Log.d(LOG, "Connect: START");
            try {
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url_db + "?android_id="+androidId+"&action=get_routes&filter="+filter[0]).build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                JSONObject resObj = new JSONObject(result);

                if(resObj.getString("TYPE").equals("OK")) {
                    JSONArray arRoutes = resObj.getJSONArray("DATA");
                    feedRoutes = new ArrayList<>();
                    for(int i = 0; i < arRoutes.length(); i++) {
                        JSONObject objRoute = arRoutes.getJSONObject(i);
                        FeedRoute route = new FeedRoute();

                        route.setId(objRoute.optString(TAG_ROUTE_ID));
                        route.setPointFrom(objRoute.optString(TAG_ROUTE_POINT_FROM));
                        route.setPointTo(objRoute.optString(TAG_ROUTE_POINT_TO));
                        route.setDateFrom(objRoute.optString(TAG_ROUTE_DATE_FROM));
                        route.setTimeFrom(objRoute.optString(TAG_ROUTE_TIME_FROM));
                        route.setMaxPassengers(objRoute.optString(TAG_ROUTE_MAX_PASSENGERS));
                        route.setLeftPassengers(objRoute.optString(TAG_ROUTE_LEFT_PASSENGERS));
                        route.setPrice(objRoute.optString(TAG_ROUTE_PRICE)+" р.");
                        route.setCheckId(objRoute.optString(TAG_ROUTE_CHECK_ID));

                        feedRoutes.add(route);
                    }
                } else {
                    Log.d(LOG, "return: "+resObj.getString("TYPE"));
                }
            } catch (Exception e) {
                Log.d(LOG, "Connect: "+e.toString());
            }
            return null;
        }

        protected void onPostExecute(Void users) {
            progressBar.setVisibility(View.GONE);
            RouteRecyclerViewAdapter adapter = new RouteRecyclerViewAdapter(getApplicationContext(), feedRoutes);
            mRecyclerView.setAdapter(adapter);
            adapter.setOnRouteClickListener(new OnRouteClickListener() {
                @Override
                public void onItemClick(FeedRoute route) {
                    if(route.getCheckId().equals("0"))
                        new SetCheck().execute(route.getId());
                    else
                        new unCheck().execute(route.getCheckId());
                }
            });
        }
    }

    private class SetCheck extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String[] params) {
            Log.d(LOG, "Connect: START");
            try {
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url_db + "?android_id="+androidId+"&firebase_instance_id="+FirebaseInstanceId.getInstance().getToken()+"&action=set_check&route_id=" + params[0]).build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                JSONObject resObj = new JSONObject(result);
                if(resObj.getString("TYPE").equals("OK")) {
                    return "OK";
                } else {
                    return resObj.getString("DATA");
                }
            } catch (Exception e) {
                Log.d(LOG, "Connect: "+e.toString());
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if(result.equals("OK")) {
                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(in);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private class unCheck extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String[] params) {
            Log.d(LOG, "Connect: START");
            try {
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url_db + "?android_id="+androidId+"&action=uncheck&check_id=" + params[0]).build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                JSONObject resObj = new JSONObject(result);
                if(resObj.getString("TYPE").equals("OK")) {
                    return "OK";
                } else {
                    return params[0];
                }
            } catch (Exception e) {
                Log.d(LOG, "Connect: "+e.toString());
            }
            return null;
        }

        protected void onPostExecute(String result) {
            if(result.equals("OK")) {
                Intent in = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(in);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast toast = Toast.makeText(getApplicationContext(), "Ошибка отмены бронирования (заказ id:"+result+").", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

}
