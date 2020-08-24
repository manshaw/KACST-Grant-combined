package cz.covid19cz.nebojsa;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.kml.KmlLayer;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import cz.covid19cz.erouska.R;
import cz.covid19cz.nebojsa.autocompleteaddress.PlacesAutoCompleteAdapter;
import cz.covid19cz.nebojsa.utility.AppLanguageManager;
import cz.covid19cz.nebojsa.utility.GPSTracker;
import cz.covid19cz.nebojsa.utility.IMaps;
import cz.covid19cz.nebojsa.utility.MapsUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback
        , NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnCameraIdleListener
        , LocationListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String GEOMETRY = "position";
    public static final String LOCATION = "location";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";
    public static final String ICON = "icon";
    public static final String TYPE = "park";
    public static final String TYPE_ID = "id";
    public static final String NAME = "title";
    public static final String PLACE_ID = "place_id";
    public static final String REFERENCE = "reference";
    public static final String VICINITY = "vicinity";
    public static final String PLACE_NAME = "place_name";
    private final static String TAG = "MapActivity";
    private static final int M_MAX_ENTRIES = 5;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String PACAKGE_NAME = "pk.com.pakzarzameen.c19places";
    static String COOKIE_CSRF;
    static String CSRF_TOKEN;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public TextView hint, count;
    public ArrayList<Integer> pos = new ArrayList<>();
    int position;
    Toast mToast = null;
    ProgressBar mProgressbar;
    LatLng startLocation;
    PlacesAdapter placesAdapter;
    ArrayList<String> place_name, place_address_;
    SharedPreferences prefs = null;
    ArrayList<String> mLikelyPlaceNames = new ArrayList<>();
    ArrayList<String> PlaceLocs = new ArrayList<>();
    ArrayList<Double> Lat_places = new ArrayList<>();
    ArrayList<Double> Long_places = new ArrayList<>();
    ListView lstPlaces;
    Boolean selected = false;
    //    FloatingActionButton next;
    SharedPreferences sharedPreferences;
    ConstraintLayout cl_place;
    ScrollView scrollView;
    private SupportMapFragment mapFragment;
    private GoogleApiClient mGoogleApiClient;
    private ImageView ivMyLocation, ivClearSearchText;
    private GoogleMap mMap;
    private TextView iconSearchBar;
    private TextView landArea;
    private AutoCompleteTextView tvLocationSearch;
    private ConstraintLayout cl_places;
    private Handler handler;
    private HandlerThread mHandlerThread;
    private PlacesAutoCompleteAdapter placesAutoCompleteAdapter;
    private boolean selectFromMap = false, txtFromIsSelected = false;
    private double pickUpLat = 0.0, pickUpLng = 0.0, customerLat = 0.0, customerLng = 0.0;
    private String pickUpAddress = "";
    private LocationRequest mLocationRequest;
    private ArrayList<LatLng> latLngArrayList;
    private List<String> crops;
    private boolean isUserInteracting, refresh;
    private KmlLayer layer;
    private String category, bt, grapht;
    private int back_count = 0;
    private RequestQueue mQueue;
    private ArrayList<String> mLikelyPlaceAddresses = new ArrayList<>();
    private ArrayList<String> distances_places = new ArrayList<>();

    public static String distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }
        int distanceint;
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        double distance = Math.round(SphericalUtil.computeDistanceBetween(point1, point2));

        // Adjust to KM if M goes over 1000 (see javadoc of method for note
        // on only supporting metric)
        if (distance >= 1000) {
            numberFormat.setMaximumFractionDigits(1);

            String dis = String.valueOf(distance / 1000);
            if (dis.length() >= 6) {
                distanceint = (int) (distance / 1000);
                return numberFormat.format(distanceint) + " Km";
            } else


                return numberFormat.format(distance / 1000) + " Km";
        }
        return numberFormat.format(distance) + " m";
    }

    @Override
    protected void onCreate(Bundle savedInsanceState) {
        super.onCreate(savedInsanceState);
        if (new AppLanguageManager(this).getAppLanguage().equals("cz")) {
            setContentView(R.layout.activity_maps_cz);
        } else if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            setContentView(R.layout.activity_maps_ar);
        } else {
            setContentView(R.layout.activity_maps);
        }
        mToast = new Toast(this);
        category = getIntent().getStringExtra("category");
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();


        mProgressbar = findViewById(R.id.progressBar);
        mProgressbar.setVisibility(View.GONE);
        scrollView = findViewById(R.id.showscroll);

        sharedPreferences = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        latLngArrayList = new ArrayList<>();
        cl_places = findViewById(R.id.cl_places);
        lstPlaces = findViewById(R.id.listplaces);
        lstPlaces.setClickable(true);
        initUI();
        initFunstionality();
    }

    private void initUI() {
        tvLocationSearch = findViewById(R.id.tvLocationSearch);
//        iconSearchBar = (TextView) findViewById(R.id.icon_search_bar);
        ivClearSearchText = findViewById(R.id.ivClearSearchText);
        hint = findViewById(R.id.hint);
        count = findViewById(R.id.count);
//        next = (FloatingActionButton) findViewById(R.id.next1);

    }

    private void initFunstionality() {

        if (handler == null) {
            mHandlerThread = new HandlerThread(TAG,
                    android.os.Process.THREAD_PRIORITY_BACKGROUND);
            mHandlerThread.start();
            // Initialize the Handler
            handler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        ArrayList<String> results = placesAutoCompleteAdapter.resultList;

                        if (results != null && results.size() > 0) {
                            Log.d("SIZEING: ", String.valueOf(results.size()));
                            placesAutoCompleteAdapter.notifyDataSetChanged();
                        } else {
                            placesAutoCompleteAdapter.notifyDataSetInvalidated();
                        }
                    }
                }
            };
        }

        tvLocationSearch.setAdapter(new PlacesAutoCompleteAdapter(MapActivity.this,
                R.layout.item_auto_place));
        tvLocationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickUpAddress.length() != 0) {
                    tvLocationSearch.setText("");
                }
            }
        });
        tvLocationSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                txtFromIsSelected = hasFocus;
            }
        });
        tvLocationSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.v("Selected Location: ", parent.getItemAtPosition(position).toString());
                closeKeyboard();
// Get data associated with the specified position
// in the list (AdapterView)
                final String description = parent.getItemAtPosition(position).toString();
                LatLng latLng = getLocationFromAddress(getApplicationContext(), description);
                try {
                    if (latLng.latitude != 0.0 && latLng.longitude != 0.0) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                        // Add marker
                        startLocation = latLng;
                        pickUpLat = latLng.latitude;
                        pickUpLng = latLng.longitude;
                        pickUpAddress = tvLocationSearch.getText().toString().trim();
                        mQueue = Volley.newRequestQueue(MapActivity.this);
                        loadNearByPlaces_get(pickUpLat, pickUpLng);
                        if (pickUpAddress.length() > 20) {
                            tvLocationSearch.setText(pickUpAddress.substring(0, 20) + " ...");
                        }
                    } else {
                        Toast.makeText(MapActivity.this, "Can not find location", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    tvLocationSearch.setText("");
                    e.printStackTrace();
                }
            }
        });

        tvLocationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                final String value = s.toString();
                if (value.length() > 0) {
                    // Remove all callbacks and messages
                    handler.removeCallbacksAndMessages(null);
                    // Now add a new one
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (placesAutoCompleteAdapter == null) {
                                placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(MapActivity.this,
                                        R.layout.item_auto_place);
                            }
                            // Background thread
                            placesAutoCompleteAdapter.resultList = placesAutoCompleteAdapter.mPlaceAPI.autocomplete(value);
                            // Footer
                            if (placesAutoCompleteAdapter.resultList.size() > 0) {
                                placesAutoCompleteAdapter.resultList.add("footer");
                            }
                            // Post to Main Thread
                            handler.sendEmptyMessage(1);
                        }
                    }, 500);
                }
                ivClearSearchText.setVisibility(s.length() == 0 ? View.GONE : View.VISIBLE);
            }


            @Override
            public void afterTextChanged(Editable s) {
                Log.v(TAG, "afterTextChanged: " + s.toString());
                try {
                    String[] latlng = s.toString().split(" ");
                    Log.v(TAG, "qwead: " + latlng[0] + "/" + latlng[1]);
                    float lat = Float.parseFloat(latlng[0]);
                    Log.v(TAG, "afterTextChanged lat: " + lat);
                    float lng = Float.parseFloat(latlng[1]);
                    Log.v(TAG, "afterTextChanged lng: " + lng);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 18));
                } catch (Exception e) {
                    Log.v(TAG, "LaExer: " + e.getMessage());
                }
                if (selectFromMap) {
                    selectFromMap = false;
                } else {
                    tvLocationSearch.setDropDownHeight(DrawerLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        });
        ivClearSearchText.setOnClickListener(new
                                                     View.OnClickListener() {
                                                         @SuppressLint("RestrictedApi")
                                                         @Override
                                                         public void onClick(View v) {

                                                             if (startLocation == null) {
                                                                 tvLocationSearch.setText("");
                                                                 return;
                                                             }
                                                             //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 18));
                                                             tvLocationSearch.setText("");
                                                             tvLocationSearch.requestFocus();
                                                             clearStart();
                                                             pickUpAddress = "";

                                                             count.setVisibility(View.GONE);
//                                                             next.setVisibility(View.GONE);
                                                             hint.setVisibility(View.VISIBLE);
                                                             final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                                                             int pixels = (int) (240 * scale + 0.5f);
                                                             ViewGroup.LayoutParams params = scrollView.getLayoutParams();
                                                             params.height = pixels;
                                                             scrollView.setLayoutParams(params);
                                                             cl_places.setVisibility(View.GONE);
//                                                             hint.setText("Search your place and then nearby places will be shown here");
                                                             try {
                                                                 if (!placesAdapter.isEmpty())
                                                                     placesAdapter.clear();
                                                             } catch (Exception e) {
                                                                 e.printStackTrace();
                                                             }
                                                         }
                                                     }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "Please mark your land by clicking on map", Toast.LENGTH_LONG).show();
    }


    public void clearStart() {
        if (mMap != null)
            mMap.clear();
        tvLocationSearch.setText("");
        startLocation = null;
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
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

    private void closeKeyboard() {
        Log.d(TAG, "closeKeyboard()");
        try {
            InputMethodManager imm = (InputMethodManager) getApplicationContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (tvLocationSearch.hasFocus()) {
                imm.hideSoftInputFromWindow(tvLocationSearch.getWindowToken(), 0);
            } else {
                imm.hideSoftInputFromWindow(tvLocationSearch.getWindowToken(), 0);
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult");
//        switch (requestCode) {
//            case AndroidPermissionRequestCode.ACCESS_FINE_LOCATION:
        Log.v(TAG, "AndroidPermissionRequestCode.ACCESS_COARSE_LOCATION");

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED");
            try {
                mMap.setMyLocationEnabled(true);

                Log.v(TAG, "Location permission granted");

                final GPSTracker tracker = new GPSTracker(getApplicationContext());

                startLocation = new LatLng(tracker.getLatitude(), tracker.getLongitude());
//                                                                if (!MasterConfig.isLiveServer()) {
                Log.d(TAG, "LATLNG: " + startLocation.toString());
                Log.d(TAG, "LATLNG: " + startLocation.latitude);
                Log.d(TAG, "mMap: " + mMap.toString());
                if (startLocation.latitude == 0.0 && startLocation.longitude == 0.0) {
                    return;
                }
//                                                                }
                mMap.animateCamera(CameraUpdateFactory
                        .newLatLngZoom(startLocation, 18));
//                                                        setStartMarker();
                new MapsUtil.GetAddressByLatLng(new IMaps() {
                    @Override
                    public void processFinished(Object obj) {
                        String address = (String) obj;
                        if (!address.isEmpty()) {
                            // Set marker's title
                            selectFromMap = true;
                            tvLocationSearch.setText(address);
                            pickUpLat = tracker.getLatitude();
                            pickUpLng = tracker.getLongitude();
                            pickUpAddress = address;
                        }
                    }
                }).execute(startLocation);

            } catch (SecurityException ex) {
                Log.v(TAG, "MAP setMyLocationEnable: " + ex.getMessage());
            }
        } else {

            //Toast.makeText(this, "Kindly allow GPS for better accuracy", Toast.LENGTH_LONG).show();


        }
//        }

        //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Set listeners for click events.
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }

    public void bAddLandClicked(View view) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        isUserInteracting = true;
    }

    //    private void loadNearByPlaces(double latitude, double longitude) {
////YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works
//        StringBuilder googlePlacesUrl =
//                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        googlePlacesUrl.append("location=").append(latitude).append(",").append(longitude);
//        googlePlacesUrl.append("&radius=").append(5000);
//        googlePlacesUrl.append("&types=").append(category.toLowerCase());
////        googlePlacesUrl.append("&sensor=true");
//        googlePlacesUrl.append("&key=" + "AIzaSyAZsuRHDgBPsQsbNk5fIBjjxAeqp5NqZEo");
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, googlePlacesUrl.toString(), null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject result) {
//                        parseLocationResult(result);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "onErrorResponse: Error= " + error);
//                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
//                    }
//                });
//
//        mQueue.add(request);
//    }
//
//    @SuppressLint("RestrictedApi")
//    private void parseLocationResult(JSONObject result) {
//
//        String id, place_id, placeName = null, reference, icon, vicinity = null;
//        double latitude, longitude;
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        try {
//            JSONArray jsonArray = result.getJSONArray("results");
//
//            if (result.getString("status").equalsIgnoreCase("OK")) {
//
//                mMap.clear();
//
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject place = jsonArray.getJSONObject(i);
//
//                    id = place.getString(TYPE_ID);
//                    place_id = place.getString(PLACE_ID);
//                    if (!place.isNull(NAME)) {
//                        placeName = place.getString(NAME);
//                        mLikelyPlaceNames.add(placeName);
//                    }
//                    if (!place.isNull(VICINITY)) {
//                        vicinity = place.getString(VICINITY);
//                    }
//                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
//                            .getDouble(LATITUDE);
//                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
//                            .getDouble(LONGITUDE);
//                    reference = place.getString(REFERENCE);
//                    icon = place.getString(ICON);
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    LatLng latLng = new LatLng(latitude, longitude);
//                    markerOptions.position(latLng);
//                    markerOptions.title(placeName + " : " + vicinity);
//
//                    mMap.addMarker(markerOptions);
//                    builder.include(markerOptions.getPosition());
//                }
//                fillPlacesList();
//                LatLngBounds bounds = builder.build();
//                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
//                hint.setVisibility(View.GONE);
//                count.setVisibility(View.VISIBLE);
//                next.setVisibility(View.VISIBLE);
//                String category_fixed = category;
//                if (category.equals("grocery_or_supermarket")) {
//                    category_fixed = "Groceries";
//                } else if (category.equals("Pharmacy")) {
//                    category_fixed = "Pharmacies";
//                }
//                count.setText(jsonArray.length() + " " + category_fixed + " nearby!");
//                // Toast.makeText(getBaseContext(), jsonArray.length() + " " + category + "s found!", Toast.LENGTH_LONG).show();
//            } else if (result.getString("status").equalsIgnoreCase("ZERO_RESULTS")) {
//                Toast.makeText(getBaseContext(), "Not in 2km radius",
//                        Toast.LENGTH_LONG).show();
//            }
//
//        } catch (JSONException e) {
//
//            e.printStackTrace();
//            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
//        }
//    }
//    private void loadNearByPlaces(double latitude, double longitude) {
////YOU Can change this type at your own will, e.g hospital, cafe, restaurant.... and see how it all works
//        StringBuilder googlePlacesUrl =
//                new StringBuilder("https://places.ls.hereapi.com/places/v1/autosuggest?");
//        googlePlacesUrl.append("in=").append(latitude).append(",").append(longitude);
//        googlePlacesUrl.append(";r=").append(20000);
//        googlePlacesUrl.append("&q=").append(category.toLowerCase());
////        googlePlacesUrl.append("&sensor=true");
//        hint.setText("Searching");
//        googlePlacesUrl.append("&apiKey=" + "eG7hZyXLCgNripO0ikK-Cbc4zGgVwjxhkWja5XWNytg");
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, googlePlacesUrl.toString(), null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject result) {
//                        parseLocationResult(result);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "onErrorResponse: Error= " + error);
//                        Log.e(TAG, "onErrorResponse: Error= " + error.getMessage());
//                    }
//                });
//
//        mQueue.add(request);
//    }
//
//    @SuppressLint("RestrictedApi")
//    private void parseLocationResult(JSONObject result) {
//        String placeName = null;
//        JSONArray latlng1;
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        try {
//            JSONArray jsonArray = result.getJSONArray("results");
//            mMap.clear();
//            if (jsonArray.length() > 0) {
//                for (int i = 1; i < jsonArray.length(); i++) {
//                    JSONObject place = jsonArray.getJSONObject(i);
//                    if (!place.isNull(NAME)) {
//                        placeName = place.getString(NAME);
//                        mLikelyPlaceNames.add(placeName);
//                    }
//                    latlng1 = place.getJSONArray(GEOMETRY);
//                    PlaceLocs.add(latlng1.toString());
//                    MarkerOptions markerOptions = new MarkerOptions();
//                    LatLng latLng = new LatLng(latlng1.getDouble(0), latlng1.getDouble(1));
//                    markerOptions.position(latLng);
//                    markerOptions.title(placeName);
//                    mMap.addMarker(markerOptions);
//                    builder.include(markerOptions.getPosition());
//                }
//                fillPlacesList();
//                LatLngBounds bounds = builder.build();
//                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 350));
//                hint.setVisibility(View.GONE);
//                count.setVisibility(View.VISIBLE);
//                next.setVisibility(View.VISIBLE);
//                String category_fixed = category;
//                if (category.equals("grocery_or_supermarket")) {
//                    category_fixed = "Groceries";
//                } else if (category.equals("Pharmacy")) {
//                    category_fixed = "Pharmacies";
//                }
//                count.setText(jsonArray.length() - 1 + " " + category_fixed + " nearby!");
//            } else
//                hint.setText("Nothing found in 20km Radius");
//
//        } catch (JSONException e) {
//
//            e.printStackTrace();
//            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
//        }
//    }
    public void loadNearByPlaces_get(final Double Lat, final Double Long) {

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();
        if (new AppLanguageManager(this).getAppLanguage().equals("cz")) {
            hint.setText("vyhledávání");
        } else {
            hint.setText("Searching");
        }
        cl_places.setVisibility(View.VISIBLE);
//        scrollView.setVisibility(View.VISIBLE);
        MediaType mediaType = MediaType.parse("application/graphql");
        final Request request = new Request.Builder()
                .url("https://hana.chronorobotics.tk/graphql")

                .method("GET", null)
//                .method("POST", body)
//                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-CSRFToken", "fetch")
                .build();


        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                e.printStackTrace();
                                            }

                                            @Override
                                            public void onResponse(Call call, final Response response) throws IOException {
                                                COOKIE_CSRF = response.headers("Set-Cookie").get(0);
                                                CSRF_TOKEN = COOKIE_CSRF.substring(10, 74);
                                                queuePost(Lat, Long);
                                            }
                                        }
        );
    }

    public void queuePost(Double Lat, Double Lon) {
        OkHttpClient client1 = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"query\":\"query placesq(\\r\\n  $searchPosition: Position!,\\r\\n  $placeCategory: Category!,\\r\\n  $radius: Int!\\r\\n){\\r\\n  placesNearby(\\r\\n    searchPosition: $searchPosition\\r\\n    placeCategory: $placeCategory\\r\\n    radius: $radius\\r\\n  ){\\r\\n    places{\\r\\n      name\\r\\n      address\\r\\n      coords{\\r\\n        lat,\\r\\n        lon\\r\\n      }\\r\\n    }\\r\\n  }\\r\\n}\\r\\n\",\"variables\":{\"searchPosition\":{\"lat\":" + Lat.toString() + ",\"lon\":" + Lon.toString() + "},\"placeCategory\":\"" + category + "\",\"radius\":10000}}");
        final Request request = new Request.Builder()
                .url("https://hana.chronorobotics.tk/graphql")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", COOKIE_CSRF)
                .addHeader("X-CSRFToken", CSRF_TOKEN)
                .addHeader("Referer", "https://hana.chronorobotics.tk/graphql")
                .build();
        client1.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                try {
                    String jsonData = response.body().string();
                    JSONObject obj = new JSONObject(jsonData);
                    obj = obj.getJSONObject("data");
                    obj = obj.getJSONObject("placesNearby");
                    parseLocationResult(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void parseLocationResult(JSONObject result) {
        final String category_fixed;
        String placeName = null;
        JSONObject latlng1;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        try {
            final JSONArray jsonArray = result.getJSONArray("places");
//            scrollView.setVisibility(View.VISIBLE);
//            cl_places.setVisibility(View.VISIBLE);
            Log.d("SIZE_PLACE: ", String.valueOf(jsonArray.length()));
            mLikelyPlaceNames = new ArrayList<>();
            PlaceLocs = new ArrayList<>();
            Lat_places = new ArrayList<>();
            Long_places = new ArrayList<>();
            mLikelyPlaceAddresses = new ArrayList<>();
            distances_places = new ArrayList<>();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.clear();
                }
            });
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);
                    placeName = place.getString("name");
                    mLikelyPlaceAddresses.add(place.getString("address"));
                    mLikelyPlaceNames.add(placeName);
                    latlng1 = place.getJSONObject("coords");
                    PlaceLocs.add(latlng1.toString());
                    Lat_places.add(latlng1.getDouble("lat"));
                    Long_places.add(latlng1.getDouble("lon"));
                    final MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(latlng1.getDouble("lat"), latlng1.getDouble("lon"));
                    distances_places.add(distanceBetween(startLocation, latLng));
                    markerOptions.position(latLng);
                    markerOptions.title(placeName);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getIcon()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap.addMarker(markerOptions);
                        }
                    });
                    builder.include(markerOptions.getPosition());
                }
                fillPlacesList();
                final LatLngBounds bounds = builder.build();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
//                        scrollView.setVisibility(View.VISIBLE);
                        cl_places.setVisibility(View.VISIBLE);
                        hint.setVisibility(View.GONE);
                        count.setVisibility(View.VISIBLE);
//                        next.setVisibility(View.GONE);
                    }
                });


                if (new AppLanguageManager(this).getAppLanguage().equals("cz")) {
                    if (category.equals("shop")) {
                        if (jsonArray.length() > 4) {
                            category_fixed = "potravin";
                        } else {
                            category_fixed = "potraviny";
                        }
                    } else if (category.equals("pharmacy")) {
                        if (jsonArray.length() > 4) {
                            category_fixed = "lékáren";
                        } else if (jsonArray.length() > 1) {
                            category_fixed = "lékárny";
                        } else {
                            category_fixed = "lékárna";
                        }
                    } else {
                        if (jsonArray.length() > 4) {
                            category_fixed = "parků";
                        } else if (jsonArray.length() > 1) {
                            category_fixed = "parky";
                        } else {
                            category_fixed = "park";
                        }
                    }
                } else if (new AppLanguageManager(MapActivity.this).getAppLanguage().equals("ar")) {
                    if (category.equals("shop")) {
                        category_fixed = "محتويات البقالة";
                    } else if (category.equals("pharmacy")) {
                        category_fixed = "صيدليات";
                    } else {
                        category_fixed = "الحدائق";
                    }
                } else {
                    if (category.equals("shop")) {
                        category_fixed = "groceries";
                    } else if (category.equals("pharmacy")) {
                        category_fixed = "pharmacies";
                    } else
                        category_fixed = "parks";
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (new AppLanguageManager(MapActivity.this).getAppLanguage().equals("cz")) {
                            count.setText(jsonArray.length() + " " + category_fixed + " " + getString(R.string.search_results_cz) + "!");
                        } else if (new AppLanguageManager(MapActivity.this).getAppLanguage().equals("ar")) {
                            count.setText(jsonArray.length() + " " + category_fixed + " " + getString(R.string.search_results_ar));
                        } else {
                            count.setText(jsonArray.length() + " " + category_fixed + " " + getString(R.string.search_results) + "!");

                        }
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hint.setText("Nothing found in 10km Radius");
                    }
                });
            }
        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }


    private Bitmap getIcon() {
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw;
        if (category.equals("pharmacy"))
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_icon_pharmacy);
        else if (category.equals("park"))
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_icon_park);
        else
            bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_icon_shop);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return smallMarker;
    }

    private void fillPlacesList() {
        placesAdapter = new PlacesAdapter(this, mLikelyPlaceNames, mLikelyPlaceAddresses, distances_places, category, false, "No data");
        final Parcelable state = lstPlaces.onSaveInstanceState();
        if (placesAdapter.isEmpty()) {
//            hint.setText("Nothing found in 10km Radius");
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lstPlaces.setAdapter(placesAdapter);
                }
            });
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lstPlaces.onRestoreInstanceState(state);
            }
        });
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!placesAdapter.getSelected().isEmpty()) {
//                    storePref(position);
//                    Intent intent = new Intent(MapActivity.this, RiskActivity.class);
//                    intent.putStringArrayListExtra("places_names", mLikelyPlaceNames);
//                    intent.putIntegerArrayListExtra("places_index", placesAdapter.getSelected());
//                    intent.putStringArrayListExtra("places_location", PlaceLocs);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
//                } else
//                    Toast.makeText(MapActivity.this, "You have not selected any place", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void storePref(int position) {
        int favpl = sharedPreferences.getInt("FavPlacesNum", 0);
        int tempnum;
        String cat_temp;
        if (category.equals("grocery_or_supermarket"))
            cat_temp = "shop";
        else
            cat_temp = category;
        for (int i = 0; i < placesAdapter.getSelected().size(); i++) {
            tempnum = i + favpl;
            sharedPreferences.edit().putString("FavCat" + tempnum, cat_temp).apply();
            sharedPreferences.edit().putString("FavPlaceNam" + tempnum, place_name.get(placesAdapter.getSelected().get(i))).apply();      // e.g FavPlaceNam1,2,3
            sharedPreferences.edit().putString("FavPlaceLoc" + tempnum, PlaceLocs.get(position)).apply();
            sharedPreferences.edit().putBoolean("newPlace", true).apply();
            sharedPreferences.edit().putString("asGraph" + tempnum, grapht).apply();
            sharedPreferences.edit().putBoolean("fromMap", true).apply();
            sharedPreferences.edit().putString("asBest" + tempnum, bt).apply();
            if (tempnum == 0)
                sharedPreferences.edit().putBoolean("saved", true).apply();
            sharedPreferences.edit().putString("FavPlaceAddress" + tempnum, place_address_.get(placesAdapter.getSelected().get(i))).apply();
            sharedPreferences.edit().putString("FavPlaceDis" + tempnum, distances_places.get(placesAdapter.getSelected().get(i))).apply();
        }
        favpl = favpl + placesAdapter.getSelected().size();
        sharedPreferences.edit().putInt("FavPlacesNum", favpl).apply();
    }

    SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }


    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startHelpActivity() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void startRiskActivity() {
        Intent intent = new Intent(this, RiskActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        back_count++;
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (240 * scale + 0.5f);
        ViewGroup.LayoutParams params = scrollView.getLayoutParams();
        params.height = pixels;
        scrollView.setLayoutParams(params);
        if (selected) {

//            scrollView.setVisibility(View.VISIBLE);
            cl_places.setVisibility(View.VISIBLE);
            count.setVisibility(View.VISIBLE);
            selected = false;
            placesAdapter = new PlacesAdapter(this, mLikelyPlaceNames, mLikelyPlaceAddresses, distances_places, category, selected, "18:30");
            lstPlaces.setAdapter(placesAdapter);
//            ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) cl_places.getLayoutParams();
//            lp.matchConstraintPercentHeight = (float) 0.35;
//            cl_places.setLayoutParams(lp);
//            cl_places.setMaxHeight(600);
            back_count--;
            mProgressbar.setVisibility(View.GONE);
        }
        if (back_count == 1) {
            back_count = 0;
            startMainActivity();
            finish();
        }
        return;
    }

    public void select_Place(int position) {
        this.position = position;
        selected = true;
        place_name = new ArrayList<>();
        place_address_ = new ArrayList<>();
        place_address_.add(mLikelyPlaceAddresses.get(position));
        place_name.add(mLikelyPlaceNames.get(position));
        if (ifUnique(mLikelyPlaceNames.get(position), mLikelyPlaceAddresses.get(position))) {
            mProgressbar.setVisibility(View.VISIBLE);

//        cl_places.setMaxHeight(300);
            queryGet(position);
        } else {
            if (new AppLanguageManager(MapActivity.this).getAppLanguage().equals("cz")) {
                final AlertDialog alertDialog1 = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                        .setMessage(getString(R.string.dialog_box_text_cz))
                        .setPositiveButton(getString(R.string.dialog_button_1_cz), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MapActivity.this, RiskActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_2_cz), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();

            } else if (new AppLanguageManager(MapActivity.this).getAppLanguage().equals("ar")) {
                final AlertDialog alertDialog1 = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                        .setMessage(getString(R.string.dialog_box_text_ar))
                        .setPositiveButton(getString(R.string.dialog_button_1_ar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MapActivity.this, RiskActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.dialog_button_2_ar), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            } else {
                final AlertDialog alertDialog1 = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                        .setMessage(getString(R.string.dialog_box_text))
                        .setPositiveButton(getString(R.string.dialog_button_1), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(MapActivity.this, RiskActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.dialog_button_2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        }
//        placesAdapter = new PlacesAdapter(this, place_name, place_address_, distances_places, category, selected, "No data");
//        lstPlaces.setAdapter(placesAdapter);
//        if (!placesAdapter.getSelected().isEmpty()) {
//            storePref();
//            Intent intent = new Intent(MapActivity.this, RiskActivity.class);
//            intent.putStringArrayListExtra("places_names", mLikelyPlaceNames);
//            intent.putIntegerArrayListExtra("places_index", placesAdapter.getSelected());
//            intent.putStringArrayListExtra("places_location", PlaceLocs);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//            finish();
//        } else
//            Toast.makeText(MapActivity.this, "You have not selected any place", Toast.LENGTH_LONG).show();
    }

    public void showAToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void queryGet(final int position) {

        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                        cookieStore.put(url.host(), cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .build();

        MediaType mediaType = MediaType.parse("application/graphql");
        final Request request = new Request.Builder()
                .url("https://recom.chronorobotics.tk/graphql")

                .method("GET", null)
//                .method("POST", body)
//                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-CSRFToken", "fetch")
                .build();


        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {
                                                e.printStackTrace();


                                            }

                                            @Override
                                            public void onResponse(Call call, final Response response) throws IOException {
                                                COOKIE_CSRF = response.headers("Set-Cookie").get(0);
                                                CSRF_TOKEN = COOKIE_CSRF.substring(10, 74);
                                                queuePost(position);
                                            }
                                        }
        );
    }

    public void queuePost(final int position) throws IOException {
        OkHttpClient client1 = new OkHttpClient();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String date = df.format(Calendar.getInstance().getTime());
        MediaType mediaType = MediaType.parse("application/json");
        final RequestBody body = RequestBody.create(mediaType, "{\"query\":\"query risk(\\r\\n  $version: String!,\\r\\n  $time: DateTime!,\\r\\n  $form: FormType!,\\r\\n  $position: Position!,\\r\\n  $placeCategory: Category!\\r\\n){\\r\\n  riskData(\\r\\n    version: $version,\\r\\n    time: $time,\\r\\n    form: $form\\r\\n\\t\\tposition: $position\\r\\n    placeCategory: $placeCategory\\r\\n  ){\\r\\n    riskGraph{\\r\\n      time\\r\\n\\t\\t\\tvalue\\r\\n    }\\r\\n    bestTime\\r\\n  }\\r\\n}\",\"variables\":{\"version\":\"v1\",\"time\":\"" + date + "\",\"form\":{\"text\":\"data is null\"},\"position\":{\"lat\":" + Lat_places.get(position) + ",\"lon\":" + Long_places.get(position) + "},\"placeCategory\":\"" + category + "\"}}");
        final Request request = new Request.Builder()
                .url("https://recom.chronorobotics.tk/graphql")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", COOKIE_CSRF)
                .addHeader("X-CSRFToken", CSRF_TOKEN)
                .addHeader("Referer", "https://recom.chronorobotics.tk/graphql")
                .build();

        client1.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                try {
                    String jsonData = response.body().string();
                    JSONObject obj = new JSONObject(jsonData);
                    JSONObject data = obj.getJSONObject("data");
                    JSONObject riskdata = data.getJSONObject("riskData");

                    bt = riskdata.getString("bestTime");
                    grapht = riskdata.getString("riskGraph");
                    if (bt.equals("null")) {
                        if (!riskdata.getString("riskGraph").equals("[]")) {
                            placesAdapter = new PlacesAdapter(MapActivity.this, place_name, place_address_, distances_places, category, selected, "Not today");
                        } else {
                            placesAdapter = new PlacesAdapter(MapActivity.this, place_name, place_address_, distances_places, category, selected, "No data");

                        }
                    } else {
                        placesAdapter = new PlacesAdapter(MapActivity.this, place_name, place_address_, distances_places, category, selected, bt);
                    }

//                    Log.d("TEST1: ", latLng.toString());
//                    Log.d("TEST: ", "hello papa" + place_address_.get(0));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Lat_places.get(position), Long_places.get(position)), 18));
                            } catch (Exception e) {
                            }
                            mProgressbar.setVisibility(View.GONE);
                            lstPlaces.setAdapter(placesAdapter);
                            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
                            int pixels = (int) (100 * scale + 0.5f);
                            ViewGroup.LayoutParams params = scrollView.getLayoutParams();
                            params.height = pixels;
                            scrollView.setLayoutParams(params);
//                            scrollView.setVisibility(View.VISIBLE);
                            cl_places.setVisibility(View.VISIBLE);
                            count.setVisibility(View.GONE);


                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void add_place() {
        if (!placesAdapter.getSelected().isEmpty()) {
            storePref(position);
            Intent intent = new Intent(MapActivity.this, RiskActivity.class);
            intent.putStringArrayListExtra("places_names", mLikelyPlaceNames);
            intent.putIntegerArrayListExtra("places_index", placesAdapter.getSelected());
            intent.putStringArrayListExtra("places_location", PlaceLocs);
            startActivity(intent);
            finish();
        } else
            Toast.makeText(MapActivity.this, "You have not selected any place", Toast.LENGTH_LONG).show();
    }

    public void change_height(float val) {
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) cl_places.getLayoutParams();
        lp.matchConstraintPercentHeight = val;
        cl_places.setLayoutParams(lp);
    }

    public Boolean ifUnique(String name, String address) {
        int ss = sharedPreferences.getInt("FavPlacesNum", 0);
        int temp = 0;
        for (int i = 0; i < ss; i++) {
            if (name.equals(sharedPreferences.getString("FavPlaceNam" + i, null)) && address.equals(sharedPreferences.getString("FavPlaceAddress" + i, null))) {
                return false;
            }
        }
        return true;
    }

    private class CustomLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.v(TAG, "Location Retreived was " + location.getAccuracy() + " meters accurate_2");
            mMap.animateCamera(CameraUpdateFactory.
                    newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}

