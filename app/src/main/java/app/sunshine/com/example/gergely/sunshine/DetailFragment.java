package app.sunshine.com.example.gergely.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.sunshine.com.example.gergely.sunshine.data.WeatherContract;

/**
 * Created by esziger on 2016-09-22.
 */

public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    private ShareActionProvider mShareActionProvider;

    private String mForeCastString;

    private final static int MY_LOADER_ID = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID

    };

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDTTY = 5;
    static final int COL_WEATHER_PRESSURE = 6;
    static final int COL_WEATHER_DEGREES = 7;
    static final int COL_WEATHER_WIND_SPEED = 8;
    static final int COL_WEATHER_WEATHER_ID = 9;


    TextView mDetailHigh;
    TextView mDetailLow;
    TextView mDetailDesc;
    TextView mDetailDate;
    TextView mDetailFriendlyDate;
    ImageView mDetailImage;
    TextView mDetailPressure;
    TextView mDetailWind;
    TextView mDetailHumidity;

    public DetailFragment() {
        //only call onCreateOptionMenu If we have this set.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mDetailHigh = (TextView)rootView.findViewById(R.id.detail_high);
        mDetailLow = (TextView)rootView.findViewById(R.id.detail_low);
        mDetailDesc = (TextView)rootView.findViewById(R.id.detail_weather_desc);
        mDetailDate = (TextView)rootView.findViewById(R.id.detail_date_text);
        mDetailFriendlyDate = (TextView)rootView.findViewById(R.id.detail_date);
        mDetailImage = (ImageView)rootView.findViewById(R.id.detail_weather_image);
        mDetailPressure = (TextView)rootView.findViewById(R.id.detail_pressure);
        mDetailWind = (TextView)rootView.findViewById(R.id.detail_wind);
        mDetailHumidity = (TextView)rootView.findViewById(R.id.detail_humidity);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        getLoaderManager().initLoader(MY_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private Intent createShareForecastIntent()
    {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForeCastString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailfragment, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);


        if(mForeCastString != null)
        {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "In onCreateLoader");

        Intent intent = getActivity().getIntent();
        if(intent == null)
        {
            return null;
        }

        return new CursorLoader(getActivity(), intent.getData(), FORECAST_COLUMNS, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG,"In onLoadFinished");

        if(!data.moveToFirst())
        {
            return;
        }

        int weatherId = data.getInt(COL_WEATHER_WEATHER_ID);

        String dataString = Utility.getFriendlyDayString(getActivity(), data.getLong(COL_WEATHER_DATE));


        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);

        mDetailFriendlyDate.setText(friendlyDateText);

        mDetailDate.setText(dateText);

        String weatherDescription = data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        mForeCastString = String.format("%s - %s - %s/%s", dataString, weatherDescription, high, low);

        //TextView textView = (TextView)getView().findViewById(R.id.detail_text);
        //textView.setText(mForeCastString);

        Float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        Float degree = data.getFloat(COL_WEATHER_DEGREES);
        Float wind_speed = data.getFloat(COL_WEATHER_WIND_SPEED);
        Float humidity = data.getFloat(COL_WEATHER_HUMIDTTY);

        mDetailHigh.setText(high);
        mDetailLow.setText(low);
        mDetailDesc.setText(weatherDescription);
        //mDetailDate.setText(dataString);
        mDetailPressure.setText(getActivity().getString(R.string.format_pressure, pressure));
        mDetailWind.setText(Utility.getFormattedWind(getActivity(), wind_speed, degree));
        mDetailHumidity.setText(getActivity().getString(R.string.format_humidity, humidity));

        if(mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
