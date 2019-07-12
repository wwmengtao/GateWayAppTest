package com.homecare.app.util;

import java.io.*;
import android.util.Log;

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static String readContentFromLocalFile(String path, String fileName) {
        if (!mkdir(path)) {
            return null;
        }
        File file = new File(path + "/" + fileName);
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (!created) {
                    return null;
                }
            }
            catch (IOException e) {
                Log.e(TAG, "Read file: " + path + "/" + fileName + " failed, due to: " + e.getMessage());
                return null;
            }
        }
        InputStream is = null;
        ByteArrayOutputStream bos = null;
        try {
            is = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] array = new byte[1024];
            int len;
            while( (len = is.read(array)) != -1){
                bos.write(array, 0, len);
            }
        } catch (IOException e) {
            Log.e(TAG, "Read file: " + path + "/" + fileName + " failed, due to: " + e.getMessage());
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                }
                catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        if (bos != null) {
            return bos.toString();
        } else {
            return null;
        }
    }

    public static void writeContentToLocalFile(String path, String fileName, String content) {
        if (!mkdir(path)) {
            return;
        }
        File file = new File(path + "/" + fileName);
        if (!file.exists()) {
            try {
                boolean created = file.createNewFile();
                if (!created) {
                    return;
                }
            }
            catch (IOException e) {
                Log.e(TAG, "Write file: " + path + "/" + fileName + "failed, due to: " + e.getMessage());
                return;
            }
        }
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(file);
            fout.write(content.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Write file: " + path + "/" + fileName + " failed, due to: " + e.getMessage());
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                }
                catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    public static boolean mkdir(String path) {
        File dir = new File(path);
        return dir.exists() || dir.mkdir();
    }

}
