package com.google.ar.sceneform.samples.hellosceneform;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostAsyncTask extends AsyncTask<String, Void, String> {

    private static String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(strings[0]).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (urlConnection != null) {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
            }

            OutputStreamWriter writer = null;
            try {
                if (urlConnection != null) {
                    writer = new OutputStreamWriter(urlConnection.getOutputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (writer != null) {
                    writer.write("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (writer != null) {
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            InputStream in = null;
            try {
                if (urlConnection != null) {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (in != null) {
                readStream(in);
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
