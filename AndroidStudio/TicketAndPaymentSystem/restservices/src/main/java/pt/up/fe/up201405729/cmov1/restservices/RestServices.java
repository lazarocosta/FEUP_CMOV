package pt.up.fe.up201405729.cmov1.restservices;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class RestServices {
    // The official production server is https://us-central1-cmov-d52d6.cloudfunctions.net
    // The development server is http://luisbarbosa.ddns.net:5000/cmov-d52d6/us-central1
    private static final String databaseUrl = "https://us-central1-cmov-d52d6.cloudfunctions.net";

    public static JSONObject GET(String relativeUrl, JSONObject data) {
        return RestTask(relativeUrl, "GET", data.toString().getBytes());
    }

    public static JSONObject POST(String relativeUrl, JSONObject data) {
        return RestTask(relativeUrl, "POST", data.toString().getBytes());
    }

    public static JSONObject POST(String relativeUrl, byte[] data) {
        return RestTask(relativeUrl, "POST", data);
    }

    public static JSONObject PUT(String relativeUrl, JSONObject data) {
        return RestTask(relativeUrl, "PUT", data.toString().getBytes());
    }

    public static JSONObject DELETE(String relativeUrl, JSONObject data) {
        return RestTask(relativeUrl, "DELETE", data.toString().getBytes());
    }

    private static JSONObject RestTask(String relativeUrl, String method, byte[] data) {
        String url = databaseUrl + relativeUrl;
        HttpAsyncTask httpAsyncTask = new HttpAsyncTask(url, method);
        AsyncTask<byte[], Void, JSONObject> execute = httpAsyncTask.execute(data);
        try {
            return execute.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }
}
