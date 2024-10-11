package com.example.aplikasicuaca;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class SecondActivity extends AppCompatActivity {
    TextView tvResult;
    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "1af50723434a3bd3cbd76a3f2fa5f9a2";
    DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tvResult = findViewById(R.id.tvResult);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String city = extras.getString("city");
            String country = extras.getString("country");


            if (!country.equals("")) {
                String tempUrl = url + "?q=" + city + "," + country + "&appid=" + appid;
                performApiRequest(tempUrl);
            } else {
                String tempUrl = url + "?q=" + city + "&appid=" + appid;
                performApiRequest(tempUrl);
            }
        }
    }

    private void performApiRequest(String tempUrl) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonResponse.getString("name");

                    String weatherCondition;
                    if (Integer.parseInt(clouds) > 75) {
                        weatherCondition = "Hujan";
                        tvResult.setBackgroundResource(R.drawable.img_1);
                        tvResult.setTextColor(Color.WHITE);
                    } else if (Integer.parseInt(clouds) > 50) {
                        weatherCondition = "Mendung";
                        tvResult.setBackgroundResource(R.drawable.img_2);
                        tvResult.setTextColor(Color.BLACK);
                    } else {
                        weatherCondition = "Cerah";
                        tvResult.setBackgroundResource(R.drawable.img);
                        tvResult.setTextColor(Color.BLACK);
                    }

                    
                    output += "Current weather of " + cityName + " (" + countryName + ")"
                            + "\n Temperatur                               : " + df.format(temp) + " °C"
                            + "\n Feels Like                                : " + df.format(feelsLike) + " °C"
                            + "\n Kelembaban                      : " + humidity + "%"
                            + "\n Deskripsi                                        : " + description
                            + "\n Kecepatan Angin                     : " + wind + "m/s"
                            + "\n Keadaan Mendung Awan  : " + clouds + "%"
                            + "\n Tekanan Udara                             : " + pressure + " hPa";
                    tvResult.setText(output);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void BackToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
