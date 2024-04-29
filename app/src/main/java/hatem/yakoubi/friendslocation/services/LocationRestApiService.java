package hatem.yakoubi.friendslocation.services;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocationRestApiService {

    private static final String Msg = "";

    public void sendLocationToServer(double latitude, double longitude) {
        new SendLocationTask().execute(latitude, longitude);
    }
        private static class SendLocationTask extends AsyncTask<Double, Void, Void> {

            @Override
            protected Void doInBackground(Double... params) {
                double latitude = params[0];
                double longitude = params[1];
                try {
                    // Construct the URL
                    URL url = new URL("http://192.168.1.18/services/addPosition.php");
                    // Create connection
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json");

                    // Create JSON object to send data
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("latitude", latitude);
                    jsonObject.put("longitude", longitude);

                    // Convert JSON object to string
                    String jsonInputString = jsonObject.toString();

                    // Enable writing to the connection output stream
                    urlConnection.setDoOutput(true);

                    // Write data to the connection output stream
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Check the response code
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        Log.d(Msg, "Location sent successfully");
                    } else {
                        Log.e(Msg, "Error sending location. Response code: " + responseCode);
                    }

                } catch (Exception e) {
                    Log.e(Msg, "Error sending location: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        }
    public List<LatLng> getFriendsLocations() {
        List<LatLng> positions = new ArrayList<>();
        try {
            URL url = new URL("http://192.168.1.18/services/getAllPosition.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
                int success = jsonResponse.getInt("success");
                if (success == 1) {
                    JSONArray positionsArray = jsonResponse.getJSONArray("positions");
                    for (int i = 0; i < positionsArray.length(); i++) {
                        JSONObject positionObject = positionsArray.getJSONObject(i);
                        double latitude = Double.parseDouble(positionObject.getString("latitude"));
                        double longitude = Double.parseDouble(positionObject.getString("longitude"));
                        positions.add(new LatLng(latitude, longitude));
                        Log.d(Msg, "Location: " + latitude + ", " + longitude);
                    }
                } else {
                    Log.e(Msg, "Unsuccessful response: " + jsonResponse.toString());
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return positions;
    }
    public void removeLocation(LatLng location) {
        new RemoveLocationTask().execute(location);
    }
    private static class RemoveLocationTask extends AsyncTask<LatLng, Void, Void> {

        @Override
        protected Void doInBackground(LatLng... params) {
            LatLng location = params[0];
            try {
                URL url = new URL("http://192.168.1.18/services/deletePosition.php/?latitude=" + location.latitude + "&longitude=" + location.longitude);

                // Create connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                // Check the response code
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Delete successful
                    Log.d(Msg, "Location deleted successfully");
                } else {
                    Log.e(Msg, "Error deleting location. Response code: " + responseCode);
                }
            } catch (IOException e) {
                Log.e(Msg, "Error deleting location: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }

}
