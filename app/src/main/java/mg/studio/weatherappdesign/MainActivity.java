package mg.studio.weatherappdesign;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
    }


    private class DownloadUpdate extends AsyncTask<String, Void, DownloadUpdate.ReturnResult> {

        class ReturnResult{
            public String temperature;
            public int weather_code;

            ReturnResult(String tem,int code){
                temperature = tem;
                weather_code = code;
            }
        }

        @Override
        protected ReturnResult doInBackground(String... strings) {

            // Get the LocalIp and then use it to get the weather information.
            String LocalIp = "223.104.25.255";          // default IP whose location is ChongQing
            try {
                String LocalIp1 = InetAddress.getLocalHost().getHostAddress();
                System.out.print(LocalIp1);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            String stringUrl = "https://api.seniverse.com/v3/weather/now.json?key=gtzfvskl8srbywpn&location="+ LocalIp +"&language=zh-Hans&unit=c";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.w("TAG","it is running here");
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                Log.w("MainAcitivity",buffer.toString());

                //get The temperature
                int l = buffer.indexOf("temperature") + "temperature\":\"".length();
                int r = buffer.indexOf("\"",l);
                String temperature = buffer.substring(l,r);

                //get the weather code
                l = buffer.indexOf("code") + "code\":\"".length();
                r = buffer.indexOf("\"",l);
                int weather_code = Integer.parseInt(buffer.substring(l,r));
                Log.w("MainAcitivity\"code = ",weather_code + "");

                return new ReturnResult(temperature,weather_code);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ReturnResult result) {

            String temperature = result.temperature;
            int weather_code   = result.weather_code;
            String weather_icon_path = "weather_icon_";

            //update the temprature and weather_icon according the result
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temperature);

            //update the day of week
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
            String Str_cnt = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

            int cnt = Integer.parseInt(Str_cnt);
            String[] WeekList = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};

            // update the top
            ((TextView) findViewById(R.id.top_day_of_week)).setText(WeekList[(cnt - 1) % 7]);

            // update the bottom temperature
            ((TextView) findViewById(R.id.bottom_day_of_week1)).setText(WeekList[cnt % 7].substring(0,3));
            ((TextView) findViewById(R.id.bottom_day_of_week2)).setText(WeekList[(cnt + 1) % 7].substring(0,3));
            ((TextView) findViewById(R.id.bottom_day_of_week3)).setText(WeekList[(cnt + 2) % 7].substring(0,3));
            ((TextView) findViewById(R.id.bottom_day_of_week4)).setText(WeekList[(cnt + 3) % 7].substring(0,3));
            ((TextView) findViewById(R.id.bottom_day_of_week5)).setText(WeekList[(cnt + 4) % 7].substring(0,3));

            // update the weather icon
            Log.d(getClass().toString(),weather_icon_path + weather_code);
            Log.d(getClass().toString(),getPackageName());

            int resID = getResources().getIdentifier(weather_icon_path + weather_code, "drawable", getPackageName());
            Drawable image = getResources().getDrawable(resID);

//            String path = "com/drawable/resource/image.png";
//            InputStream is = getClassLoader().getResourceAsStream(path);
//            Drawable.createFromStream(is, "src");
            ((ImageView)findViewById(R.id.img_weather_condition)).setImageDrawable(image);
        }
    }
}
