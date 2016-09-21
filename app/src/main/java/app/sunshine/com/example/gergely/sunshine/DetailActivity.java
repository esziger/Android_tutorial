package app.sunshine.com.example.gergely.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import app.sunshine.com.example.gergely.sunshine.data.WeatherContract;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
            implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();

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
        };

        static final int COL_WEATHER_ID = 0;
        static final int COL_WEATHER_DATE = 1;
        static final int COL_WEATHER_DESC = 2;
        static final int COL_WEATHER_MAX_TEMP = 3;
        static final int COL_WEATHER_MIN_TEMP = 4;

        public PlaceholderFragment() {
            //only call onCreateOptionMenu If we have this set.
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

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

            String dataString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));

            String weatherDescription = data.getString(COL_WEATHER_DESC);

            boolean isMetric = Utility.isMetric(getActivity());

            String high = Utility.formatTemperature(data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String low = Utility.formatTemperature(data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

            mForeCastString = String.format("%s - %s - %s/%s", dataString, weatherDescription, high, low);

            TextView textView = (TextView)getView().findViewById(R.id.detail_text);

            textView.setText(mForeCastString);

            if(mShareActionProvider != null)
            {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}