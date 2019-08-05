package com.idutochkin.run18.carpoling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FilterActivity extends AppCompatActivity {

    static final String LOG = "LOG";
    private static String url_db = "http://tohka-a.ru/api.php";

    public static final String APP_PREFERENCES = "SETTINGS";
    public static final String APP_PREFERENCES_CITY_FROM = "CITY_FROM";
    public static final String APP_PREFERENCES_POINT_FROM = "POINT_FROM";
    public static final String APP_PREFERENCES_CITY_TO = "CITY_TO";
    public static final String APP_PREFERENCES_POINT_TO = "POINT_TO";
    private SharedPreferences FilterData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_acitvity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        new GetListCities().execute();
    }
    private Void setSpinner(MaterialSpinner spinnerCityFrom, String myString){
        for (int i = 0; i < spinnerCityFrom.getListView().getCount(); i++){
            if (spinnerCityFrom.getListView().getItemAtPosition(i).equals(myString)){
                i = i + 1;
                spinnerCityFrom.setSelectedIndex(i);
            }
        }
        return null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_apply) {
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
        }

        return super.onOptionsItemSelected(item);
    }


    private class GetListCities extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(String... filter) {
            Log.d(LOG, "Connect: START");
            try {
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url_db + "?android_id="+androidId+"&action=get_list_points").build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                JSONObject resObj = new JSONObject(result);

                if(resObj.getString("TYPE").equals("OK")) {
                    JSONObject arData = resObj.getJSONObject("DATA");
                    return arData;
                } else {
                    Log.d(LOG, "return: "+resObj.getString("TYPE"));
                }
            } catch (Exception e) {
                Log.d(LOG, "Connect: "+e.toString());
            }
            return null;
        }

        protected void onPostExecute(JSONObject data) {
            try {
                JSONObject From = data.getJSONObject("FROM");
                Iterator<String> fromKeys = From.keys();

                MaterialSpinner spinnerCityFrom = (MaterialSpinner) findViewById(R.id.spinnerCityFrom);
                ArrayList<String> spinnerCityFromArray = new ArrayList<String>();
                spinnerCityFromArray.add("Все");
                while (fromKeys.hasNext()) {
                    String cityFrom = fromKeys.next();
                    spinnerCityFromArray.add(cityFrom);
                }
                ArrayAdapter<String> spinnerCityFromArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerCityFromArray);
                spinnerCityFrom.setAdapter(spinnerCityFromArrayAdapter);
                spinnerCityFrom.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = FilterData.edit();
                        editor.putString(APP_PREFERENCES_CITY_FROM, item);
                        editor.apply();

                        FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                        if (FilterData.contains(APP_PREFERENCES_CITY_FROM)) {
                            String cityFrom = FilterData.getString(APP_PREFERENCES_CITY_FROM, "");
                            if(!cityFrom.equals("Все"))
                                new GetListPointsFrom().execute(cityFrom);
                        }
                    }
                });

                JSONObject To = data.getJSONObject("TO");
                Iterator<String> toKeys = To.keys();

                MaterialSpinner spinnerCityTo = (MaterialSpinner) findViewById(R.id.spinnerCityTo);
                ArrayList<String> spinnerCityToArray = new ArrayList<String>();
                spinnerCityToArray.add("Все");
                while (toKeys.hasNext()) {
                    String cityTo = toKeys.next();
                    spinnerCityToArray.add(cityTo);
                }
                ArrayAdapter<String> spinnerCityToArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerCityToArray);
                spinnerCityTo.setAdapter(spinnerCityToArrayAdapter);
                spinnerCityTo.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = FilterData.edit();
                        editor.putString(APP_PREFERENCES_CITY_TO, item);
                        editor.apply();

                        FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                        if (FilterData.contains(APP_PREFERENCES_CITY_TO)) {
                            String cityTo = FilterData.getString(APP_PREFERENCES_CITY_TO, "");
                            if(!cityTo.equals("Все"))
                                new GetListPointsTo().execute(cityTo);
                        }
                    }
                });

                FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                if (FilterData.contains(APP_PREFERENCES_CITY_FROM)) {
                    String cityFrom = FilterData.getString(APP_PREFERENCES_CITY_FROM, "");
                    setSpinner(spinnerCityFrom, cityFrom);
                    if(!cityFrom.equals("Все"))
                        new GetListPointsFrom().execute(cityFrom);
                }
                if (FilterData.contains(APP_PREFERENCES_CITY_TO)) {
                    String cityTo = FilterData.getString(APP_PREFERENCES_CITY_TO, "");
                    setSpinner(spinnerCityTo, cityTo);
                    if(!cityTo.equals("Все"))
                        new GetListPointsTo().execute(cityTo);
                }
            } catch (Exception e) {
                Log.d(LOG, "Exception: "+data.toString());
            }
        }
    }


    private class GetListPointsFrom extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(String... filter) {
            Log.d(LOG, "Connect: START");
            try {
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url_db + "?android_id="+androidId+"&action=get_list_points").build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                JSONObject resObj = new JSONObject(result);

                if(resObj.getString("TYPE").equals("OK")) {
                    JSONObject arData = resObj.getJSONObject("DATA").getJSONObject("FROM").getJSONObject(filter[0]).getJSONObject("POINTS");
                    return arData;
                } else {
                    Log.d(LOG, "return: "+resObj.getString("TYPE"));
                }
            } catch (Exception e) {
                Log.d(LOG, "Connect: "+e.toString());
            }
            return null;
        }

        protected void onPostExecute(JSONObject data) {
            try {
                MaterialSpinner spinnerPointFrom = (MaterialSpinner) findViewById(R.id.spinnerPointFrom);
                ArrayList<String> spinnerPointFromArray = new ArrayList<String>();
                Iterator<String> pointKeys = data.keys();
                spinnerPointFromArray.add("Все");
                while (pointKeys.hasNext()) {
                    String pointFrom = pointKeys.next();
                    spinnerPointFromArray.add(pointFrom);
                }
                ArrayAdapter<String> spinnerPointFromArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerPointFromArray);
                spinnerPointFrom.setAdapter(spinnerPointFromArrayAdapter);
                spinnerPointFrom.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = FilterData.edit();
                        editor.putString(APP_PREFERENCES_POINT_FROM, item);
                        editor.apply();
                    }
                });
                FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                if (FilterData.contains(APP_PREFERENCES_POINT_FROM)) {
                    String pointFrom = FilterData.getString(APP_PREFERENCES_POINT_FROM, "");
                    setSpinner(spinnerPointFrom, pointFrom);
                }
            } catch (Exception e) {
                Log.d(LOG, "Exception: "+data.toString());
            }
        }
    }


    private class GetListPointsTo extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected JSONObject doInBackground(String... filter) {
            Log.d(LOG, "Connect: START");
            try {
                String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url_db + "?android_id="+androidId+"&action=get_list_points").build();
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                JSONObject resObj = new JSONObject(result);

                if(resObj.getString("TYPE").equals("OK")) {
                    JSONObject arData = resObj.getJSONObject("DATA").getJSONObject("TO").getJSONObject(filter[0]).getJSONObject("POINTS");
                    return arData;
                } else {
                    Log.d(LOG, "return: "+resObj.getString("TYPE"));
                }
            } catch (Exception e) {
                Log.d(LOG, "Connect: "+e.toString());
            }
            return null;
        }

        protected void onPostExecute(JSONObject data) {
            try {
                MaterialSpinner spinnerPointTo = (MaterialSpinner) findViewById(R.id.spinnerPointTo);
                ArrayList<String> spinnerPointToArray = new ArrayList<String>();
                Iterator<String> pointKeys = data.keys();
                spinnerPointToArray.add("Все");
                while (pointKeys.hasNext()) {
                    String pointTo = pointKeys.next();
                    spinnerPointToArray.add(pointTo);
                }
                ArrayAdapter<String> spinnerPointToArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnerPointToArray);
                spinnerPointTo.setAdapter(spinnerPointToArrayAdapter);
                spinnerPointTo.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                    @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                        FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = FilterData.edit();
                        editor.putString(APP_PREFERENCES_POINT_TO, item);
                        editor.apply();
                    }
                });
                FilterData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
                if (FilterData.contains(APP_PREFERENCES_POINT_TO)) {
                    String pointTo = FilterData.getString(APP_PREFERENCES_POINT_TO, "");
                    setSpinner(spinnerPointTo, pointTo);
                }
            } catch (Exception e) {
                Log.d(LOG, "Exception: "+data.toString());
            }
        }
    }

}
