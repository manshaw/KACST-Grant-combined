package cz.covid19cz.nebojsa;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.covid19cz.erouska.R;
import cz.covid19cz.nebojsa.utility.AppLanguageManager;

public class GraphActivity extends AppCompatActivity implements OnMapReadyCallback
        , NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnCameraIdleListener
        , LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String PACAKGE_NAME = "pk.com.pakzarzameen.c19places";
    public TextView header;
    JSONArray jsonArray;
    Boolean LeftEnabled = true;
    Boolean rightEnabled = false;
    Double val;
    DecimalFormat formater = new DecimalFormat("00");
    TextView tv_day, place, address, titlehead;
    CardView cardView;
    LinearLayout legendColor, legendText;
    Integer PlaceID;
    SharedPreferences sharedPreferences;
    String placeLoc;
    LatLng latLng;
    JSONObject locObj;
    String bt;
    TextView bt_tv, remove_tv;
    private BarChart barChart;
    private String graphVal, placeName, placeAddress, placeCategory;
    private SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private ArrayList<Double> RiskGraphData = new ArrayList<>();
    private ArrayList<String> x_values_today = new ArrayList<>();
    private ArrayList<String> x_values_tomorrow = new ArrayList<>();
    private ArrayList<String> x_values_aftertomorrow = new ArrayList<>();
    private ArrayList<Double> y_values_today = new ArrayList<>();
    private ArrayList<Double> y_values_tomorrow = new ArrayList<>();
    private ArrayList<Double> y_values_aftertomorrow = new ArrayList<>();
    private ArrayList<Integer> colors_today = new ArrayList<>();
    private ArrayList<Integer> colors_tomorrow = new ArrayList<>();
    private ArrayList<Long> seconds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new AppLanguageManager(this).getAppLanguage().equals("cz")) {
            setContentView(R.layout.activity_graph_cz);
        } else if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            setContentView(R.layout.activity_graph_ar);
        } else {
            setContentView(R.layout.activity_graph);
        }
//        getSupportActionBar().show();
//        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp);
////        upArrow.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
//        if (Build.VERSION.SDK_INT >= 24) {
//            getSupportActionBar().setTitle(Html.fromHtml("<font color='#F59662' weight='100'><b>Plan your day </b></font>", 0));
//        } else {
//            getSupportActionBar().setTitle(Html.fromHtml("<font color='#F59662'>Plan your day </font>"));
//        }
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        sharedPreferences = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);

        PlaceID = getIntent().getIntExtra("placeID", 0);
        graphVal = getIntent().getStringExtra("graphVal");
        placeName = sharedPreferences.getString("FavPlaceNam" + PlaceID, null);
        placeAddress = sharedPreferences.getString("FavPlaceAddress" + PlaceID, null);
        placeCategory = sharedPreferences.getString("FavCat" + PlaceID, null);
        bt = getIntent().getStringExtra("bestTime");
        placeLoc = sharedPreferences.getString("FavPlaceLoc" + PlaceID, null);
        try {
            locObj = new JSONObject(placeLoc);
            latLng = new LatLng(locObj.getDouble("lat"), locObj.getDouble("lon"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        remove_tv = findViewById(R.id.deletePlace);
        place = findViewById(R.id.place);
        titlehead = findViewById(R.id.titlehead);
        address = findViewById(R.id.address);    // address will be here
        place.setSelected(true);
        address.setSelected(true);
        place.setText(placeName);
        address.setText(placeAddress);
        bt_tv = findViewById(R.id.tv_bestTime);
        bt_tv.setText(bt);
        if (bt.equals("No data") || bt.equals("Not today") || bt.equals(getString(R.string.favourite_places_status_1_cz)) || bt.equals(getString(R.string.favourite_places_status_2_cz)) || bt.equals(getString(R.string.favourite_places_status_1_ar)) || bt.equals(getString(R.string.favourite_places_status_2_ar))) {
            bt_tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_no_data, 0, 0, 0);
            if (bt.equals("No data") || bt.equals(getString(R.string.favourite_places_status_1_cz)) || bt.equals(getString(R.string.favourite_places_status_1_ar)))
                bt_tv.setTextColor(Color.GRAY);
            else
                bt_tv.setTextColor(Color.parseColor("#FF4133"));
        }
        cardView = findViewById(R.id.cardView);
        tv_day = findViewById(R.id.tvdayGraph);
        legendColor = findViewById(R.id.legendColor);
        titlehead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        legendText = (LinearLayout) findViewById(R.id.legendsText);
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if (graphVal.length() == 0) {
            LinearLayout applink = findViewById(R.id.applink);
            Button download = findViewById(R.id.download);
            applink.setVisibility(View.VISIBLE);
            tv_day.setVisibility(View.INVISIBLE);
            cardView.setVisibility(View.INVISIBLE);
//            legendText.setVisibility(View.INVISIBLE);
            legendColor.setVisibility(View.INVISIBLE);
            remove_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (new AppLanguageManager(GraphActivity.this).getAppLanguage().equals("cz")) {
                        setDialog(view, "cz");
                    } else if (new AppLanguageManager(GraphActivity.this).getAppLanguage().equals("ar")) {
                        setDialog(view, "ar");
                    } else {
                        setDialog(view, "en");
                    }
                }
            });
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://chronorobotics.tk"));
                    startActivity(browserIntent);
                }
            });
        } else {
            Log.d("Graph", "graphVal:" + graphVal);
            try {
                jsonArray = new JSONArray(graphVal);
                for (int i = 0; i < jsonArray.length(); i++)
                    if (jsonArray.getJSONObject(i).getDouble("value") == 0) {
                        RiskGraphData.add(.03);
                    } else {
                        RiskGraphData.add((jsonArray.getJSONObject(i).getDouble("value")));
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            barChart = findViewById(R.id.risk_bar_chart);
            barChart.setViewPortOffsets(40, 0, 40, 60);
            Log.d("Graph", "data:" + RiskGraphData.toString());
            for (int i = 0; i <= 192; i++) {
                seconds.add(i * 900L);
            }
            populate_data();
            remove_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (new AppLanguageManager(GraphActivity.this).getAppLanguage().equals("cz")) {
                        setDialog(view, "cz");
                    } else if (new AppLanguageManager(GraphActivity.this).getAppLanguage().equals("ar")) {
                        setDialog(view, "ar");
                    } else {
                        setDialog(view, "en");
                    }
                }
            });
        }
    }

    public void setDialog(final View view, String language) {
        if (language.equals("cz")) {
            final AlertDialog alertDialog1 = new AlertDialog.Builder(view.getContext(), R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_delete_black_24dp)
                    .setTitle("odstranit místo")
                    .setMessage("odstranit toto místo?")
                    .setPositiveButton("Ano", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delete(PlaceID);
                            Intent intent = new Intent(view.getContext(), RiskActivity.class);
                            view.getContext().startActivity(intent);
                            ((Activity) view.getContext()).finish();
                        }
                    })
                    .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
//            alertDialog1.getWindow().setBackgroundDrawableResource(android.R.color.white);

        } else if (language.equals("ar")) {
            final AlertDialog alertDialog1 = new AlertDialog.Builder(view.getContext(), R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_delete_black_24dp)
                    .setTitle("إزالة المكان")
                    .setMessage("إزالة هذا المكان؟")
                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delete(PlaceID);
                            Intent intent = new Intent(view.getContext(), RiskActivity.class);
                            view.getContext().startActivity(intent);
                            ((Activity) view.getContext()).finish();
                        }
                    })
                    .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
//            alertDialog1.getWindow().setBackgroundDrawableResource(android.R.color.white);

        } else {
            final AlertDialog alertDialog1 = new AlertDialog.Builder(view.getContext(), R.style.CustomDialogTheme)
                    .setIcon(R.drawable.ic_delete_black_24dp)
                    .setTitle("Remove Place")
                    .setMessage("Remove this place?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delete(PlaceID);
                            Intent intent = new Intent(view.getContext(), RiskActivity.class);
                            view.getContext().startActivity(intent);
                            ((Activity) view.getContext()).finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
//            alertDialog1.getWindow().setBackgroundDrawableResource(android.R.color.white);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void delete(int pos) {
        int temp = sharedPreferences.getInt("FavPlacesNum", 0);
        int temp_curr = 0;
        for (int i = pos; i < temp - 1; i++) {
            temp_curr = i + 1;
            sharedPreferences.edit().putString("FavPlaceLoc" + i, sharedPreferences.getString("FavPlaceLoc" + temp_curr, null)).apply();
            sharedPreferences.edit().putString("FavPlaceNam" + i, sharedPreferences.getString("FavPlaceNam" + temp_curr, null)).apply();
            sharedPreferences.edit().putString("FavCat" + i, sharedPreferences.getString("FavCat" + temp_curr, null)).apply();
            sharedPreferences.edit().putString("asBest" + i, sharedPreferences.getString("asBest" + temp_curr, null)).apply();
            sharedPreferences.edit().putString("FavPlaceAddress" + i, sharedPreferences.getString("FavPlaceAddress" + temp_curr, null)).apply();
            sharedPreferences.edit().putString("FavPlaceDis" + i, sharedPreferences.getString("FavPlaceDis" + temp_curr, null)).apply();
            sharedPreferences.edit().putString("asGraph" + i, sharedPreferences.getString("asGraph" + temp_curr, null)).apply();
        }
        sharedPreferences.edit().putInt("FavPlacesNum", temp - 1).apply();
        sharedPreferences.edit().putBoolean("fromGraph", true);
    }

    public void populate_data() {
        final Drawable mDrawable = ContextCompat.getDrawable(GraphActivity.this, R.drawable.ic_navigate_before_black_24dp);
        final Drawable mDrawable1 = ContextCompat.getDrawable(GraphActivity.this, R.drawable.ic_navigate_next_black_24dp);
        tv_day.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, mDrawable1, null);

        Calendar calendar = Calendar.getInstance();
        Calendar ourTime = Calendar.getInstance();
        Long time, timefixed;
        int hour, minutes;
        minutes = calendar.get(Calendar.MINUTE);
        minutes = (minutes / 5) * 5;
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), minutes);
        timefixed = calendar.getTimeInMillis() / 1000;
        for (int i = 0; i < 192; i++) {
            time = timefixed + seconds.get(i);
            ourTime.setTimeInMillis(time * 1000);
            hour = ourTime.get(Calendar.HOUR_OF_DAY);
            minutes = ourTime.get(Calendar.MINUTE);
            if (whatDay(time) == 0) {
                if (hour > 6) {
                    colors_today.add(whatColor(RiskGraphData.get(i)));
                    x_values_today.add(hour + ":" + formater.format(minutes));
                    y_values_today.add(RiskGraphData.get(i));
                }
            } else if (whatDay(time) == 1) {
                if (hour > 6) {
                    colors_tomorrow.add(whatColor(RiskGraphData.get(i)));
                    x_values_tomorrow.add(hour + ":" + formater.format(minutes));
                    y_values_tomorrow.add(RiskGraphData.get(i));
                }
            } else if (whatDay(time) == 2) {
                if (hour > 8) {
                    x_values_aftertomorrow.add(hour + ":" + formater.format(minutes));
                    y_values_aftertomorrow.add(RiskGraphData.get(i));
                }
            }
        }
        drawLineGraph(y_values_today, x_values_today, colors_today);

        Drawable unwrappedDrawablebefore = AppCompatResources.getDrawable(this, R.drawable.ic_navigate_before_black_24dp);
        final Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawablebefore);
        Drawable unwrappedDrawablebefore1 = AppCompatResources.getDrawable(this, R.drawable.ic_navigate_next_black_24dp);
        final Drawable wrappedDrawable1 = DrawableCompat.wrap(unwrappedDrawablebefore1);
        tv_day.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int[] textLocation = new int[2];
                    tv_day.getLocationOnScreen(textLocation);

                    if (event.getRawX() <= textLocation[0] + tv_day.getTotalPaddingLeft()) {

                        if (LeftEnabled) {
                            rightEnabled = false;
                        } else {
                            if (new AppLanguageManager(getApplicationContext()).getAppLanguage().equals("cz")) {
                                tv_day.setText(R.string.graph_day1_cz);
                            } else if (new AppLanguageManager(getApplicationContext()).getAppLanguage().equals("ar")) {
                                tv_day.setText(R.string.graph_day1_ar);
                            } else {
                                tv_day.setText("Today");
                            }
                            LeftEnabled = true;
                            mDrawable.setColorFilter(new PorterDuffColorFilter(0xffAFAAA4, PorterDuff.Mode.SRC_IN));//                            DrawableCompat.setTint(wrappedDrawable, Color.rgb(238,160,86));
                            rightEnabled = false;
                            mDrawable1.setColorFilter(new PorterDuffColorFilter(0xffEEA056, PorterDuff.Mode.SRC_IN));//
                            tv_day.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, mDrawable1, null);

                            drawLineGraph(y_values_today, x_values_today, colors_today);
                        }

                        return true;
                    }


                    if (event.getRawX() >= textLocation[0] + tv_day.getWidth() - tv_day.getTotalPaddingRight()) {

                        // Right drawable was tapped
                        if (rightEnabled) {
                            LeftEnabled = false;
                        } else {
                            mDrawable1.setColorFilter(new PorterDuffColorFilter(0xffAFAAA4, PorterDuff.Mode.SRC_IN));//                            DrawableCompat.setTint(wrappedDrawable, Color.rgb(238,160,86));
                            mDrawable.setColorFilter(new PorterDuffColorFilter(0xffEEA056, PorterDuff.Mode.SRC_IN));//


                            tv_day.setCompoundDrawablesWithIntrinsicBounds(mDrawable, null, mDrawable1, null);
                            if (new AppLanguageManager(getApplicationContext()).getAppLanguage().equals("cz")) {
                                tv_day.setText(R.string.graph_day2_cz);
                            } else if (new AppLanguageManager(getApplicationContext()).getAppLanguage().equals("ar")) {
                                tv_day.setText(R.string.graph_day2_ar);
                            } else {
                                tv_day.setText("Tomorrow");
                            }
                            rightEnabled = true;
//                            DrawableCompat.setTint(wrappedDrawable1, Color.rgb(238,160,86));
                            LeftEnabled = false;
//                            DrawableCompat.setTint(wrappedDrawable,Color.GRAY);
                            drawLineGraph(y_values_tomorrow, x_values_tomorrow, colors_tomorrow);
                        }

                        return true;
                    }
                }
                return true;
            }
        });
    }

    public int whatColor(Double data) {
        if (data > .66)
            return Color.rgb(255, 77, 77);
        else if (data > .33)
            return Color.rgb(255, 153, 51);
        else
            return Color.rgb(0, 153, 0);

    }

    public int whatDay(Long time) {
        int dif;
        Calendar ourTime = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        ourTime.setTimeInMillis(time * 1000);
        if (calendar.get(Calendar.DAY_OF_WEEK) == 7) {
            if (ourTime.get(Calendar.DAY_OF_WEEK) == 7)
                return 0;
            else {
                return (ourTime.get(Calendar.DAY_OF_WEEK) + 7) - calendar.get(Calendar.DAY_OF_WEEK);
            }
        } else {
            dif = ourTime.get(Calendar.DAY_OF_WEEK) - calendar.get(Calendar.DAY_OF_WEEK);
            return dif;
        }
    }

    public void drawLineGraph(ArrayList yval, ArrayList<String> xval, ArrayList<Integer> colors) {
        if (!barChart.isEmpty()) {
            barChart.clear();
        }
        // no description text
        barChart.getDescription().setEnabled(false);

        // enable touch gestures
        barChart.setTouchEnabled(true);

        // enable scaling and dragging
        //  Linechart.setDragEnabled(true);
        //  Linechart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        //  Linechart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        barChart.setMaxHighlightDistance(300);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(Typeface.DEFAULT);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(4, false);
        xAxis.setTextSize(12);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setEnabled(true); //no xAxis will be Drawn

        YAxis y = barChart.getAxisLeft();
        y.setTypeface(Typeface.DEFAULT);
        y.setLabelCount(6, false);
        y.setTextColor(Color.TRANSPARENT);
//        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisMinimum(0);
        y.setAxisMaximum(1);
//        Bitmap bitmap = Bitmap.createBitmap(Resources.getSystem().getDisplayMetrics().widthPixels, 150, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        Paint paint1 = new Paint();
//        paint1.setColor(Color.rgb(100,100,100));
//        Paint paint = new Paint();
//        paint.setColor(Color.rgb(250,210,210));
//        paint1.setTextSize(35);
//        paint1.setTypeface(Typeface.create("Segoe UI",Typeface.ITALIC));
//        canvas.drawRect(30F, 0F, Resources.getSystem().getDisplayMetrics().widthPixels-90, 40, paint);
//        canvas.drawText("HIGH RISK", Resources.getSystem().getDisplayMetrics().widthPixels/2,35f, paint1);
//        paint.setColor(Color.rgb(253,235,175));
//        canvas.drawRect(30F, 40F, Resources.getSystem().getDisplayMetrics().widthPixels-90, 80, paint);
//        canvas.drawText("MODERATE RISK", Resources.getSystem().getDisplayMetrics().widthPixels/2,75f, paint1);
//
//        paint.setColor(Color.rgb(190,226,233));
//        canvas.drawRect(30F, 80F, Resources.getSystem().getDisplayMetrics().widthPixels-90, 120, paint);
//        canvas.drawText("LOW RISK", Resources.getSystem().getDisplayMetrics().widthPixels/2,115f, paint1);
//
//
////        y.setAxisLineColor(Color.WHITE);
////        LimitLine upperLimitLine = new LimitLine(.66f);
////        upperLimitLine.setLineColor(this.getResources().getColor(R.color.red_active));
////        y.addLimitLine(upperLimitLine);
////        LimitLine lowerLimitLine = new LimitLine(.33f);
////        lowerLimitLine.setLineColor(this.getResources().getColor(R.color.green_active));
////        y.addLimitLine(lowerLimitLine);
//        Drawable d = new BitmapDrawable(getResources(), bitmap);
//
//        Linechart.setBackground(d);

        barChart.getAxisRight().setEnabled(false);

        // add data
        // lower max, as cubic runs significantly slower than linear


        barChart.getLegend().setEnabled(false);

        //Linechart.animateXY(1200, 1200);
        barChart.animateY(1200);

        // don't forget to refresh the drawing
        barChart.invalidate();
        setData(0, 0, yval, xval, colors);
    }

    private void setData(int count, float range, ArrayList<Double> lineChartArray, ArrayList<String> xval, ArrayList<Integer> colors) {

        ArrayList<BarEntry> values = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xval));
        for (int i = 0; i < lineChartArray.size(); i++) {
            values.add(new BarEntry(i, lineChartArray.get(i).floatValue()));
        }

        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            // set1.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new BarDataSet(values, "DataSet 1");


            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColors(colors);

//            set1.setFillFormatter(new IFillFormatter() {
//                @Override
//                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
//                    return Linechart.getAxisLeft().getAxisMinimum();
//                }
//            });
            // create a data object with the data sets
            BarData data = new BarData(set1);
            data.setValueTypeface(Typeface.DEFAULT);
            data.setValueTextSize(9f);
            data.setDrawValues(false);
            data.setValueFormatter(new DefaultAxisValueFormatter(3));
            barChart.setVisibility(View.VISIBLE);
//            barChart.getAxisLeft().setLabelCount(6, true);
            // set data

            barChart.setData(data);
            barChart.setScaleXEnabled(true); // enables horizontal zooming and scrolling
            barChart.setScaleYEnabled(false);
//            barChart.setVisibleXRangeMaximum(10);
//            barChart.moveViewToX(10);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        Log.d("LOCATION: ", getLocationFromAddress(placeAddress).toString());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(placeName);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getIcon()));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.addMarker(markerOptions);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }


    private Bitmap getIcon() {
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw;
        if (placeCategory.equals("pharmacy"))
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_icon_pharmacy);
        else if (placeCategory.equals("park"))
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_icon_park);
        else
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_icon_shop);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

}
