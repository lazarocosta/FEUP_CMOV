package pt.up.fe.up201405729.cmov1.restservices;

// Based on code of the CMOV classes of FEUP

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpAsyncTask extends AsyncTask<byte[], Void, JSONObject> {
    private String urlString;
    private String requestMethod;

    HttpAsyncTask(String url, String method) {
        this.urlString = url;
        this.requestMethod = method;
    }

    @Override
    protected JSONObject doInBackground(byte[]... byteArrays) {
        if (byteArrays.length != 1)
            throw new IllegalArgumentException("Only one byte array is expected.");

        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod(requestMethod);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);
            DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.write(byteArrays[0]);
            outputStream.flush();
            outputStream.close();
            int responseCode = urlConnection.getResponseCode();
            JSONObject response = readStream(urlConnection.getInputStream());
            if (responseCode == 200)
                return response;
            else
                System.err.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return new JSONObject();
    }

    private JSONObject readStream(InputStream in) throws JSONException {
        BufferedReader reader = null;
        String line;
        StringBuilder response = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null)
                response.append(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new JSONObject(response.toString());
    }
}
