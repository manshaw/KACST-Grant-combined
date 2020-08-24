package cz.covid19cz.nebojsa.utility;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class MapsUtil {
    private final static String MapsUtil = "EME.MapsUtil";

    private static final String TAG = "MapsUtil";

    public static class GetLatLngByAddress extends
            AsyncTask<String, Void, LatLng> {

        private IMaps imap;

        public GetLatLngByAddress(IMaps imap) {
            this.imap = imap;
        }

        @Override
        protected LatLng doInBackground(String... params) {
            try {
                String strAddress = params[0];
                if (strAddress.contains(" ")) {
                    strAddress = strAddress.replace(" ", "%20");
                }
                String uri = "https://maps.google.com/maps/api/geocode/json?address="
                        + strAddress + "&sensor=false" + "&key=AIzaSyD_mKiJ_8oekgpe2z4PkYQ31CBXeIGWViA";
//                String uri = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
//                        + strAddress + "&key=AIzaSyD_mKiJ_8oekgpe2z4PkYQ31CBXeIGWViA";
                HttpGet httpGet = new HttpGet(uri);
                HttpClient client = new DefaultHttpClient();
                HttpResponse response;
                StringBuilder stringBuilder = new StringBuilder();

                try {
                    response = client.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    InputStream stream = entity.getContent();
                    int b;
                    while ((b = stream.read()) != -1) {
                        stringBuilder.append((char) b);
                    }
                    Log.v(TAG, "String Bulilder" + stringBuilder.toString());
                } catch (Exception ex) {
                    Log.e(TAG, "MESSAGE: " + ex.getMessage());
                    return new LatLng(0.0, 0.0);
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(stringBuilder.toString());

                    Log.v(TAG, "JSON" + jsonObject.toString());
                    Log.v(TAG, "JSON" + jsonObject.get("results").toString());
                    double lng = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").getDouble("lng");

                    double lat = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").getDouble("lat");

                    return new LatLng(lat, lng);
                } catch (Exception ex) {
                    Log.v(TAG, "Error: ", ex);
                    Log.v(TAG, "Error Message: " + ex.getMessage());
                    return new LatLng(0.0, 0.0);
                }
            } catch (Exception ex) {
                Log.v(TAG, "Error MessagE: " + ex.getMessage());
                return new LatLng(0.0, 0.0);
            }
        }

        @Override
        protected void onPostExecute(LatLng result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.v("result is: ", result.toString());
            try {
                imap.processFinished(result);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static class GetAddressByLatLng extends
            AsyncTask<LatLng, Void, String> {

        private IMaps imap;

        public GetAddressByLatLng(IMaps imap) {
            this.imap = imap;
        }

        @Override
        protected String doInBackground(LatLng... params) {
            try {
                LatLng latLng = params[0];

                String uri = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                        + latLng.latitude
                        + ","
                        + latLng.longitude
                        + "&sensor=true";

                HttpParams par = new BasicHttpParams();
                HttpProtocolParams.setVersion(par, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(par, "UTF-8");
                par.setBooleanParameter("http.protocol.expect-continue", false);

                HttpGet httpGet = new HttpGet(uri);
                HttpClient client = new DefaultHttpClient(par);
                HttpResponse response;

                String strResponse = "";
                try {
                    response = client.execute(httpGet);
                    HttpEntity entity = response.getEntity();

                    strResponse = EntityUtils.toString(entity, HTTP.UTF_8);
                } catch (Exception ex) {
                    return "";
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(strResponse);

                    String address = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0).getString("formatted_address");

                    return address;
                } catch (Exception ex) {
                    return "";
                }
            } catch (Exception ex) {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                imap.processFinished(result);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
