package com.unibamberg.ddt.inventoryqrscanner;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class SendRequest extends AsyncTask<String, Void, String> {
    @Override
    protected void onPreExecute(){}

    @Override
    protected String doInBackground(String... scannedData) {
        String returnString;

        try {
            URL url = new URL("https://script.google.com/macros/s/AKfycbwxMET5gAMQ5HdS8OWJok8cT4M9THzAsyieWZdM/exec");

            String data = scannedData[0];
            if (!data.startsWith("(uniba-iadk)")) {
                returnString = "invalid signature";

            } else {
                String json = String.format("{\"sdata\": \"%s\"}", data.substring(12));
                JSONObject postDataParams = new JSONObject(json);

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    for (String line = in.readLine(); line != null; line = in.readLine()) {
                        sb.append(line);
                    }
                    in.close();

                    returnString = sb.toString();

                } else {
                    returnString = "http response not ok: " + responseCode;
                }
            }

        } catch(Exception e){
            returnString = "Exception: " + e.getMessage();
        }

        return returnString;
    }

    @Override
    protected void onPostExecute(String result) {
    }

    private String getPostDataString(JSONObject params) throws Exception {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){
            String key= itr.next();
            Object value = params.get(key);

            if (first) { first = false; }
            else { result.append("&"); }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

}
