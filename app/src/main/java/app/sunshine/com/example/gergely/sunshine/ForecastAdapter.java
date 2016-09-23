package app.sunshine.com.example.gergely.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import app.sunshine.com.example.gergely.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;


    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
            Remember that these views are reused as needed.
         */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());

        int layoutId = -1;

        if (viewType == VIEW_TYPE_TODAY)
            layoutId = R.layout.list_item_forecast_today;
        else if(viewType == VIEW_TYPE_FUTURE_DAY)
            layoutId = R.layout.list_item_forecast;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        view.setTag(viewHolder);

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        int viewType = getItemViewType(cursor.getPosition());

        int imageIconId = -1;

        if( viewType == VIEW_TYPE_TODAY)
            imageIconId = Utility.getArtResourceForWeatherCondition(weatherId);
        else if (viewType == VIEW_TYPE_FUTURE_DAY)
            imageIconId = Utility.getIconResourceForWeatherCondition(weatherId);

        // Use placeholder image for now
        //viewHolder.imageView.setImageResource(R.mipmap.ic_launcher);
        viewHolder.imageView.setImageResource(imageIconId);

        // TODO Read date from cursor
        long longDate = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        String friendlyDate = Utility.getFriendlyDayString(context, longDate);
        viewHolder.date.setText(friendlyDate);

        // TODO Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        viewHolder.description.setText(description);

        Log.v("Description: ",description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.high.setText(Utility.formatTemperature(context, high, isMetric));

        // TODO Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.low.setText(Utility.formatTemperature(context, low, isMetric));
    }


    public class ViewHolder {

        public ImageView imageView;
        public TextView date;
        public TextView description;
        public TextView high;
        public TextView low;

        ViewHolder(View view)
        {
            imageView = (ImageView) view.findViewById(R.id.list_item_icon);
            date = (TextView)view.findViewById(R.id.list_item_date_textview);
            description = (TextView)view.findViewById(R.id.list_item_forecast_textview);
            high = (TextView) view.findViewById(R.id.list_item_high_textview);
            low = (TextView) view.findViewById(R.id.list_item_low_textview);
        }

    }
}