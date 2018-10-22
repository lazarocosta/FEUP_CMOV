package pt.up.fe.up201405729.cmov1.restservices;

import java.util.ArrayList;

public class RestServices {
    private static final String databaseUrl = "https://us-central1-cmov-d52d6.cloudfunctions.net";

    public static String GET(String relativeUrl, ArrayList<String> data) {
        return RestTask(relativeUrl, "GET", data);
    }

    public static String POST(String relativeUrl, ArrayList<String> data) {
        return RestTask(relativeUrl, "POST", data);
    }

    public static String PUT(String relativeUrl, ArrayList<String> data) {
        return RestTask(relativeUrl, "PUT", data);
    }

    public static String DELETE(String relativeUrl, ArrayList<String> data) {
        return RestTask(relativeUrl, "DELETE", data);
    }

    private static String RestTask(String relativeUrl, String method, ArrayList<String> data) {
        ArrayList<String> httpAsyncTaskData = new ArrayList<>();
        httpAsyncTaskData.add(databaseUrl + relativeUrl);
        httpAsyncTaskData.add(method);
        httpAsyncTaskData.addAll(data);
        String[] httpAsyncTaskDataArray = httpAsyncTaskData.toArray(new String[0]);
        HttpAsyncTask httpAsyncTask = new HttpAsyncTask();
        return httpAsyncTask.doInBackground(httpAsyncTaskDataArray);
    }
}
