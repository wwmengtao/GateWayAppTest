package com.homecare.app.util;

import android.util.Log;

import java.io.*;
import java.net.*;

public class HttpUtil {

    public static final String GET = "GET";
    public static final String POST = "POST";

    private static final int CONNECTION_TIMEOUT = 1000;
    private static final int SOCKET_TIMEOUT = 1000;

    private static final String TAG = HttpUtil.class.getSimpleName();

    public static String sendRequest(String uri, String requestMethod) throws IOException {
        return sendRequest(uri, requestMethod, null, -1);
    }

    public static String sendRequest(String uri, String requestMethod, int timeout) throws IOException {
        return sendRequest(uri, requestMethod, null, timeout);
    }

    public static String sendRequest(String uri, String requestMethod, String requestBody) throws IOException {
        return sendRequest(uri, requestMethod, requestBody, -1);
    }

    private static String sendRequest(String uri, String requestMethod, String requestBody, int timeout) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection conn = null;
        OutputStream out = null;
        BufferedReader in = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(requestMethod);
            if (requestMethod.equals(POST)) {
                conn.setDoOutput(true);
            }
            conn.setDoInput(true);
            if (timeout > 0) {
                conn.setConnectTimeout(timeout / 2);
                conn.setReadTimeout(timeout / 2);
            } else {
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setReadTimeout(SOCKET_TIMEOUT);
            }
            conn.setRequestProperty("Connection", "Keep-Alive");
            if (requestBody != null) {
                conn.setRequestProperty("Content-Type", "text/plain");
                out = conn.getOutputStream();
                out.write(requestBody.getBytes());
                out.flush();
            }
            conn.connect();
            StringBuilder content = new StringBuilder();
            if (conn.getResponseCode() == 200) {
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine).append("\n");
                }
                if (content.length() > 0) {
                    content.deleteCharAt(content.length() - 1);
                }
            }
            else {
                Log.e(TAG, "Failed to get http response from: " + uri + ", response code: " + conn.getResponseCode());
                throw new IOException(conn.getResponseCode() + "");
            }
            return content.toString();
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to get http response from: " + uri + ", due to: " + e.getMessage());
            if (conn != null) {
                conn.disconnect();
            }
            throw new IOException(e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    public static void download(String uri, File localFile) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection conn = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            if (localFile.exists()) {
                boolean deleted = localFile.delete();
                if (!deleted) {
                    return;
                }
            }
            boolean created = localFile.createNewFile();
            if (created) {
                int bytes;
                out = new FileOutputStream(localFile);
                byte[] buffer = new byte[1024 * 1024];
                while ((bytes = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes);
                }
                out.flush();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Failed to get http response from: " + uri + ", due to: " + e.getMessage());
            if (conn != null) {
                conn.disconnect();
            }
            throw new IOException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

}
