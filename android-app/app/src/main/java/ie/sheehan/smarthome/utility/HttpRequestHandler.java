package ie.sheehan.smarthome.utility;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ie.sheehan.smarthome.model.EnvironmentReading;


public class HttpRequestHandler {

    private static final String DOMAIN = "192.167.1.31";

    private static final String ENVPOINT_ENVIRONMENT = "/environment";
    private static final String ENDPOINT_SECURITY = "/security";
    private static final String ENDPOINT_STOCK = "/stock";


    private static final HttpRequestHandler instance = new HttpRequestHandler();

    private HttpRequestHandler() {}

    public static HttpRequestHandler getInstance() { return instance; }


    public EnvironmentReading getEnvironmentReading() {
        JSONObject json;
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();
        EnvironmentReading envReading = null;

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENVPOINT_ENVIRONMENT, "/get");

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null) {
                response.append(nextLine);
            }

            json = new JSONObject(response.toString());
            envReading = new EnvironmentReading(json);
        } catch (MalformedURLException e) {
            Log.e("ERROR", "MALFORMED URL ERROR");
        } catch (IOException e) {
            Log.e("ERROR", "IO ERROR");
        } catch (JSONException e) {
            Log.e("ERROR", "JSON ERROR");
        } catch (Exception e) {
            Log.e("ERROR", "UNKNOWN ERROR");
        }

        return envReading;
    }


    public List<EnvironmentReading> getEnvironmentReadingsInRange(Date from, Date to) {
        JSONArray json;
        HttpURLConnection connection;
        StringBuilder response = new StringBuilder();
        List<EnvironmentReading> envReadings = new ArrayList<>();

        String target = String.format("http://%s:8080%s%s", DOMAIN, ENVPOINT_ENVIRONMENT, "/get/range");
        target += String.format(Locale.getDefault(), "?from=%d&to=%d", (from.getTime() / 1000L), (to.getTime() / 1000L));

        try {
            connection = (HttpURLConnection) new URL(target).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String nextLine;

            while ((nextLine = reader.readLine()) != null){
                response.append(nextLine);
            }

            json = new JSONArray(response.toString());

            for (int i = 0 ; i < json.length() ; i++){
                envReadings.add(new EnvironmentReading(json.getJSONObject(i)));
            }

        } catch (Exception e){
            Log.e("HTTP REQUEST ERROR", e.toString());
        }

        return envReadings;
    }

}
