package app.sunshine.com.example.gergely.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //weather APi key
    //955b2c18b6acca76a70af7a3add079b4
    //http://api.openweathermap.org/data/2.5/weather?q=London&APPID=955b2c18b6acca76a70af7a3add079b4&mode=html
    //for 7 days
    //api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=xml&units=metric&cnt=7

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ////////////////////Start of Networking ////////////////

       // String stringUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&APPID=955b2c18b6acca76a70af7a3add079b4&mode=json&units=metric&cnt=7";
        //new FetchWeatherTask().execute();

        ////////////////////End of Networking ////////////////

        String[] values = new String[]{"Today - Sunny - 21",
                                       "Tomorrow - Sunny - 22",
                                       "Sunday - Sunny - 22",
                                       "Monday - Sunny - 22"};

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(values));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                                                R.layout.list_item_forecast,
                                                                R.id.list_item_forecast_textview,
                                                                weekForecast);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listview = (ListView) view.findViewById(R.id.listview_forecast);
        listview.setAdapter(adapter);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... params)
        {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if(params.length == 0)
            {
                return null;
            }

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numdays = 7;
            String appid = "955b2c18b6acca76a70af7a3add079b4";

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&APPID=955b2c18b6acca76a70af7a3add079b4&mode=json&units=metric&cnt=7");

                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                                        .appendQueryParameter(QUERY_PARAM, params[0])
                                        .appendQueryParameter(FORMAT_PARAM, format)
                                        .appendQueryParameter(UNITS_PARAM, units)
                                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numdays))
                                        .appendQueryParameter(APPID_PARAM,appid)
                                        .build();
                URL url = new URL(buildUri.toString());

                Log.v("FetchWeatherTask", "Built URI" + buildUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v("FetchWeatherTask", "Forecast JSON String: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}
