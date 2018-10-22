package pt.up.fe.up201405729.cmov1.restservices;

// Based on code of the CMOV classes of FEUP

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpAsyncTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String[] data) {
        String urlString = data[0];
        String requestMethod = data[1];
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
            StringBuilder payload = new StringBuilder();
            payload.append("\"");
            for (int i = 2; i < data.length; i++)
                payload.append(data[i]);
            payload.append("\"");
            outputStream.writeBytes(payload.toString());
            outputStream.flush();
            outputStream.close();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200)
                return readStream(urlConnection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }

    private String readStream(InputStream in) {
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
        return response.toString();
    }
}
