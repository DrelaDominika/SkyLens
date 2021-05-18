package com.example.skylens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

//import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public String lat;
    public String lon;

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            String json = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1) {
                    char letter = (char) data;
                    json += letter;
                    data = reader.read();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return json;
        }

        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            Log.i("weatherJson", json);

            try {
                // parsing response into JSON object
                JSONObject jsonObject = new JSONObject(json);

                //getting weather description
                String weatherInfo = jsonObject.getString("weather");

                JSONArray array = new JSONArray(weatherInfo);

                JSONObject weather = array.getJSONObject(0);

                TextView weatherTextView = findViewById(R.id.description);
                weatherTextView.setText(weather.getString("description"));

                //getting longitude and latitude
                JSONObject coordInfo = jsonObject.getJSONObject("coord");

                TextView lonTV = findViewById(R.id.longitude);
                lonTV.setText(coordInfo.getString("lon"));

                TextView latTV = findViewById(R.id.latitude);
                latTV.setText(coordInfo.getString("lat"));

                //creating variables to pass values to maps activity
                lat = coordInfo.getString("lat");
                lon = coordInfo.getString("lon");




                //getting humidity and temp and pressure
                JSONObject mainInfo = jsonObject.getJSONObject("main");

                TextView humidityTV = findViewById(R.id.humidity);
                humidityTV.setText(mainInfo.getString("humidity") + '%');

                TextView pressureTV = findViewById(R.id.pressure);
                pressureTV.setText(mainInfo.getString("pressure") + "hPA");

                TextView tempTV = findViewById(R.id.temperature);
                tempTV.setText(mainInfo.getString("temp") + "°C\n");


                //getting wind speed
                JSONObject windInfo = jsonObject.getJSONObject("wind");

                TextView windTV = findViewById(R.id.windSpeed);
                windTV.setText(windInfo.getString("speed") + " m/s");

                //getting sunrise and sunset
                JSONObject sysInfo = jsonObject.getJSONObject("sys");

                //parsing unix time to human readable
                java.util.Date sunsetTime = new java.util.Date((long) sysInfo.getInt("sunset") * 1000);
                java.util.Date sunriseTime = new java.util.Date((long) sysInfo.getInt("sunrise") * 1000);

                TextView sunsetTV = findViewById(R.id.sunset);
                sunsetTV.setText((sunsetTime.toString().substring(11, 23)));

                TextView sunriseTV = findViewById(R.id.sunrise);
                sunriseTV.setText(sunriseTime.toString().substring(11, 23));

                //getting date
                String date =sunriseTime.toString().substring(0,10);
                TextView dateTV = findViewById(R.id.dateTime);
                dateTV.setText(date);

                //getting country code
                TextView countryTV =findViewById(R.id.country);
                countryTV.setText(sysInfo.getString("country"));

                //getting name
                TextView cityTV = findViewById(R.id.city);
                cityTV.setText(jsonObject.getString("name"));

                //find icon
                ImageView imageView = findViewById(R.id.imageView);
                JSONArray jsonArray = jsonObject.getJSONArray("weather");
                JSONObject obj = jsonArray.getJSONObject(0);
                String icon = obj.getString("icon");
//                Picasso.get().load("http://openweathermap.org/img/wn/"+icon+"@2x.png").into(imageView);



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void openMap(View view){
        Intent mapIntent =new Intent(this, MapsActivity.class);
        mapIntent.putExtra("lat",lat);
        mapIntent.putExtra("lon",lon);
        startActivity(mapIntent);

    }

    public void show(View view) {
        EditText cityEditText = findViewById(R.id.cityEditText);
        DownloadTask task = new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + cityEditText.getText().toString() + "&units=metric&appid=0c62dcd359d6281b470f01ab101e97e9&lang=en");
    }
}
