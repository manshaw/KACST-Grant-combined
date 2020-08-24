package cz.covid19cz.nebojsa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cz.covid19cz.erouska.R;
import cz.covid19cz.nebojsa.utility.AppLanguageManager;
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

public class RiskActivity extends AppCompatActivity {
    private static final String PACAKGE_NAME = "pk.com.pakzarzameen.c19places";
    static String COOKIE_CSRF;
    static String CSRF_TOKEN;
    String date;
    Integer size_array;
    ArrayList<String> place_names = new ArrayList<>();
    ArrayList<Integer> selected_place = new ArrayList<>();
    ArrayList<String> place_locs = new ArrayList<>();
    JSONObject place_locations;
    ArrayList<Double> lat_locs = new ArrayList<>();
    ArrayList<Double> long_locs = new ArrayList<>();
    ArrayList<String> category = new ArrayList<>();
    ArrayList<String> TimeDay = new ArrayList<>();
    ArrayList<String> Address = new ArrayList<>();
    String CSRFTOKEN;
    ProgressBar mProgressbar;
    SharedPreferences sharedPreferences;
    TextView addPlace;
    int size_places;
    private ArrayList<String> Status = new ArrayList<>();
    private ArrayList<String> GraphVal = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (new AppLanguageManager(this).getAppLanguage().equals("cz")) {
            setContentView(R.layout.activity_risk_cz);
        } else if (new AppLanguageManager(this).getAppLanguage().equals("ar")) {
            setContentView(R.layout.activity_risk_ar);
        } else {
            setContentView(R.layout.activity_risk);
        }
        mProgressbar = findViewById(R.id.progressBar);
//        place_names = getIntent().getStringArrayListExtra("places_names");
//        selected_place = getIntent().getIntegerArrayListExtra("places_index");
//        place_locs = getIntent().getStringArrayListExtra("places_location");
        sharedPreferences = getSharedPreferences(PACAKGE_NAME, MODE_PRIVATE);
        getStoredData();
        if (place_locs != null) {
            for (int i = 0; i < place_locs.size(); i++) {
                try {
                    place_locations = new JSONObject(place_locs.get(i));
                    lat_locs.add(place_locations.getDouble("lat"));
                    long_locs.add(place_locations.getDouble("lon"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            size_array = selected_place.size();
            if (sharedPreferences.getBoolean("newPlace", false)) {
                size_places = size_array - 1;
            } else {
                size_places = size_array;
            }
            CookieManager.getInstance().setAcceptCookie(true);
            recyclerView = findViewById(R.id.my_recycler_view);

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            // specify an adapter (see also next example)
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            date = df.format(Calendar.getInstance().getTime());
            if (sharedPreferences.getBoolean("saved", false)) {
                for (int i = 0; i < sharedPreferences.getInt("FavPlacesNum", 0); i++) {
                    Status.add(sharedPreferences.getString("asBest" + i, null));
                    GraphVal.add(sharedPreferences.getString("asGraph" + i, null));
                }
                sharedPreferences.edit().putBoolean("fromMap", false).apply();
                sharedPreferences.edit().putBoolean("fromGraph", false).apply();
                checkTime();
                display(selectedPlaces(place_names, selected_place));
                display(selectedPlaces(place_names, selected_place));
            } else {
                if (sharedPreferences.getInt("FavPlacesNum", 0) > 0)
                    queryGet();
                else {
                    mProgressbar.setVisibility(View.GONE);
                }
            }
        }

        addPlace = findViewById(R.id.btnAddPlace);
        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    public ArrayList<String> selectedPlaces(ArrayList<String> place_names, ArrayList<Integer> selected_place) {
        sharedPreferences.edit().putBoolean("saved", true).apply();
        mProgressbar.setVisibility(View.GONE);
        ArrayList<String> places = new ArrayList<>();
        for (int i = 0; i < selected_place.size(); i++) {
            places.add(i, place_names.get(selected_place.get(i)));
        }
        return places;
    }

    public void display(ArrayList<String> places) {
        mAdapter = new MyAdapter(places, Status, GraphVal, category, TimeDay, Address);
        recyclerView.setAdapter(mAdapter);
    }

    public void queryGet() {

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
        final RequestBody body = RequestBody.create(mediaType, "{\"query\":\"query risk(\\n  $version: String!,\\n  $time: DateTime!,\\n  $form: FormType!,\\n  $position: Position!,\\n  $placeCategory: String!\\n){\\n  riskData(\\n    version: $version,\\n    time: $time,\\n    form: $form\\n\\t\\tposition: $position\\n    placeCategory: $placeCategory\\n  ){\\n    riskGraph{\\n      time\\n\\t\\t\\tvalue\\n    }\\n    bestTime\\n  }\\n}\",\"variables\":{\"version\":\"v1\",\"time\":\"2020-03-25T06:47:31+00:00\",\"form\":{\"text\":\"this is the form's text\"},\"position\":{\"lat\":54,\"lon\":34.343},\"placeCategory\":\"public\"},\"operationName\":\"risk\"}");
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
                                                queuePost(lat_locs, long_locs, 0);
                                            }
                                        }
        );
    }

    public void queuePost(ArrayList<Double> Lat, ArrayList<Double> Long, final Integer i) throws IOException {
        if (i == (size_places)) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    getSavedValue();
                    checkTime();
                    display(selectedPlaces(place_names, selected_place));
                }
            });
        } else {
            OkHttpClient client1 = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            final RequestBody body = RequestBody.create(mediaType, "{\"query\":\"query risk(\\r\\n  $version: String!,\\r\\n  $time: DateTime!,\\r\\n  $form: FormType!,\\r\\n  $position: Position!,\\r\\n  $placeCategory: Category!\\r\\n){\\r\\n  riskData(\\r\\n    version: $version,\\r\\n    time: $time,\\r\\n    form: $form\\r\\n\\t\\tposition: $position\\r\\n    placeCategory: $placeCategory\\r\\n  ){\\r\\n    riskGraph{\\r\\n      time\\r\\n\\t\\t\\tvalue\\r\\n    }\\r\\n    bestTime\\r\\n  }\\r\\n}\",\"variables\":{\"version\":\"v1\",\"time\":\"" + date + "\",\"form\":{\"text\":\"data is null\"},\"position\":{\"lat\":" + Lat.get(i) + ",\"lon\":" + Long.get(i) + "},\"placeCategory\":\"" + category.get(i) + "\"}}");
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
                        GraphVal.add(riskdata.getString("riskGraph"));
                        Status.add(riskdata.getString("bestTime"));
                        sharedPreferences.edit().putString("asGraph" + i, riskdata.getString("riskGraph")).apply();
                        sharedPreferences.edit().putString("asBest" + i, riskdata.getString("bestTime")).apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int j = i + 1;
                    queuePost(lat_locs, long_locs, j);
                }
            });
        }
    }

    private void getSavedValue() {
        if (sharedPreferences.getBoolean("newPlace", false)) {
            Status.add(sharedPreferences.getString("asBest" + size_places, null));
            GraphVal.add(sharedPreferences.getString("asGraph" + size_places, null));
            sharedPreferences.edit().putBoolean("newPlace", false).apply();
        }
    }

    private void checkTime() {
        for (int i = 0; i < selected_place.size(); i++) {
            if (!Status.get(selected_place.get(i)).equals("null")) {
                Date date;
                String Time = Status.get(selected_place.get(i));
                try {
                    Status.set(selected_place.get(i), Time.substring(11, 16) + " - " + (Integer.parseInt(Time.substring(11, 13)) + 1) + Time.substring(13, 16));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

                try {
                    date = dateformat.parse(Time);
                    if (DateUtils.isToday(date.getTime()))
                        TimeDay.add("Today");
                    else
                        TimeDay.add("Tomorrow");

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                TimeDay.add("null");
            }
        }
    }

    public void startMapsActivity(String category) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    public void startHelpActivity() {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (sharedPreferences.getBoolean("fromMap", false)||sharedPreferences.getBoolean("fromGraph",false)) {
//        } else {
//            sharedPreferences.edit().putBoolean("saved", false).apply();
//        }

    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (sharedPreferences.getBoolean("fromMap", false)||sharedPreferences.getBoolean("fromGraph",false)) {
//        } else {
//            sharedPreferences.edit().putBoolean("saved", false).apply();
//        }
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        sharedPreferences.edit().putBoolean("fromStart", false).apply();
        startActivity(intent);
//        finish();
    }

    public void getStoredData() {
        int ss = sharedPreferences.getInt("FavPlacesNum", 0);

        int temp = 0;
        for (int i = 0; i < ss; i++) {
            if (sharedPreferences.getString("FavPlaceNam" + i, null) == null) {
                temp--;
            } else {
                category.add(sharedPreferences.getString("FavCat" + i, null));
                place_names.add(sharedPreferences.getString("FavPlaceNam" + i, null));
                place_locs.add(sharedPreferences.getString("FavPlaceLoc" + i, null));
                Address.add(sharedPreferences.getString("FavPlaceAddress" + i, null));
                selected_place.add(i + temp);
            }
        }
    }
}
