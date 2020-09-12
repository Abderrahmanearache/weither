package com.example.Weither;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RequestQueue rq;
    private TextView descriptionTextView, temperatureTextView, cityTextView, dayTextView, heurTextView;
    private TextView amPmIndicatorTextView, TMiTextView, TMTextView, ptextview, HMTextView;
    private LinearLayout backGroundlinearLayout;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolabr = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolabr);
        rq = Volley.newRequestQueue(this);
        networkRequest("casa");
        descriptionTextView = (TextView) findViewById(R.id.description);

        temperatureTextView = (TextView) findViewById(R.id.degree);

        cityTextView = (TextView) findViewById(R.id.city);

        dayTextView = (TextView) findViewById(R.id.day);

        amPmIndicatorTextView = (TextView) findViewById(R.id.dORn);

        heurTextView = (TextView) findViewById(R.id.hour);

        TMiTextView = (TextView) findViewById(R.id.tmin);

        TMTextView = (TextView) findViewById(R.id.tmax);

        ptextview = (TextView) findViewById(R.id.pression);

        HMTextView = (TextView) findViewById(R.id.humidite);

        backGroundlinearLayout = (LinearLayout) findViewById(R.id.main_list);
        imageView = (ImageView) findViewById(R.id.icon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                networkRequest(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        return true;
    }

    public void getImage(String icon) {
        String apiURI = String.format("https://openweathermap.org/img/wn/%s@2x.png", icon);
        ImageRequest imageRequest = new ImageRequest(apiURI, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        rq.add(imageRequest);
    }

    public void networkRequest(String query) {
        String apiURI = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s", query, getString(R.string.api_key));
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiURI, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                showResults(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        rq.add(jsonObjectRequest);
    }

    public void showResults(JSONObject weatherData) {
        try {

            final double toC = -273.15;
            JSONObject weather = weatherData.getJSONArray("weather").getJSONObject(0);
            JSONObject main = weatherData.getJSONObject("main");
            String cityNameAndMain = weatherData.getString("name") + " | " + weather.getString("main");
            String temperature = (int) (main.getDouble("temp") + toC) + "°C";
            String tmin = (int) (main.getDouble("temp_min") + toC) + " °C";
            String tmax = (int) (main.getDouble("temp_max") + toC) + " °C";
            String pressure = main.getInt("pressure") + " hPa";
            String humidity = main.getInt("humidity") + "";
            String fullDay = batshTimeToString(weatherData.getLong("dt"));
            String[] dayData = fullDay.split(" ");


            descriptionTextView.setText(weather.getString("description"));
            temperatureTextView.setText(temperature);
            cityTextView.setText(cityNameAndMain);
            dayTextView.setText(dayData[0].toUpperCase());
            amPmIndicatorTextView.setText(dayData[2]);
            heurTextView.setText(dayData[1]);
            TMiTextView.setText(tmin);
            TMTextView.setText(tmax);
            ptextview.setText(pressure);
            HMTextView.setText(humidity);

            String mainStr = weather.getString("main");
            if (mainStr.equals("Clouds")) {
                backGroundlinearLayout.setBackground(getDrawable(R.drawable.clouds));
            } else if (mainStr.equals("Thunderstorm")) {
                backGroundlinearLayout.setBackground(getDrawable(R.drawable.thenderstorm));
            } else if (mainStr.equals("Drizzle")) {
                backGroundlinearLayout.setBackground(getDrawable(R.drawable.drizzle));
            } else if (mainStr.equals("Rain")) {
                backGroundlinearLayout.setBackground(getDrawable(R.drawable.rain));
            } else if (mainStr.equals("Snow")) {
                backGroundlinearLayout.setBackground(getDrawable(R.drawable.snow));
            } else if (mainStr.equals("Clear")) {
                backGroundlinearLayout.setBackground(getDrawable(R.drawable.clear));
            } else {
                backGroundlinearLayout.setBackground(getDrawable(R.drawable.clear));
            }
            getImage(weather.getString("icon"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String batshTimeToString(Long batchTime) {
        Date dt = new Date(batchTime * 1000);
        SimpleDateFormat sfd = new SimpleDateFormat("EEEE hh:mm a", Locale.US);
        return sfd.format(dt);
    }
}
