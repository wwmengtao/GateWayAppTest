package com.homecare.app.service;

import android.os.Environment;
import com.homecare.app.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

public class SdService {

    public static final int REGULAR_DATA = 1;
    public static final int SLEEP_DATA = 2;
    public static final int NOTE = 3;
    public static final int ALERT = 4;
    public static final int BATTERY = 5;
    public static final int RAW_DATA_LOG = 6;
    public static final int APP_ERROR_LOG = 7;
    public static final int MOTION_DATA = 8;
    public static final int RAW_BATTERY = 9;
    public static final int REGULAR_RESULT = 10;

    private static final Object REGULAR_DATA_LOCK = new Object();
    private static final Object SLEEP_DATA_LOCK = new Object();
    private static final Object NOTE_LOCK = new Object();
    private static final Object ALERT_LOCK = new Object();
    private static final Object BATTERY_LOCK = new Object();
    private static final Object RAW_DATA_LOG_LOCK = new Object();
    private static final Object APP_ERROR_LOG_LOCK = new Object();
    private static final Object MOTION_DATA_LOCK = new Object();
    private static final Object RAW_BATTERY_LOCK = new Object();
    private static final Object REGULAR_RESULT_LOCK = new Object();

    public static final String SD_PATH = Environment.getExternalStorageDirectory() + "/Kiwi";

    private static final SdService instance = new SdService();

    private SdService() {}

    public static SdService getInstance() {
        return instance;
    }

    public List<String> getLocalContentList(int localTarget) {
        Object lock = getLock(localTarget);
        if (lock == null) {
            return new LinkedList<>();
        }
        synchronized (lock) {
            String fileContent = FileUtil.readContentFromLocalFile(SD_PATH, getFileName(localTarget));
            if (fileContent != null) {
                String[] contents = StringUtils.split(fileContent, '\n');
                if (contents != null) {
                    List<String> localContentList = new LinkedList<>();
                    for (String content : contents) {
                        localContentList.add(content);
                    }
                    return localContentList;
                }
            }
            return new LinkedList<>();
        }
    }

    private Object getLock(int localTarget) {
        Object lock = null;
        switch (localTarget) {
            case REGULAR_DATA: {
                lock = REGULAR_DATA_LOCK;
                break;
            }
            case SLEEP_DATA: {
                lock = SLEEP_DATA_LOCK;
                break;
            }
            case NOTE: {
                lock = NOTE_LOCK;
                break;
            }
            case ALERT: {
                lock = ALERT_LOCK;
                break;
            }
            case BATTERY: {
                lock = BATTERY_LOCK;
                break;
            }
            case RAW_DATA_LOG: {
                lock = RAW_DATA_LOG_LOCK;
                break;
            }
            case APP_ERROR_LOG: {
                lock = APP_ERROR_LOG_LOCK;
                break;
            }
            case MOTION_DATA: {
                lock = MOTION_DATA_LOCK;
                break;
            }
            case RAW_BATTERY: {
                lock = RAW_BATTERY_LOCK;
                break;
            }
            case REGULAR_RESULT: {
                lock = REGULAR_RESULT_LOCK;
                break;
            }
        }
        return lock;
    }

    private String getFileName(int localTarget) {
        String fileName = "";
        switch (localTarget) {
            case REGULAR_DATA: {
                fileName = "regulardata.txt";
                break;
            }
            case SLEEP_DATA: {
                fileName = "sleepdata.txt";
                break;
            }
            case NOTE: {
                fileName = "note.txt";
                break;
            }
            case ALERT: {
                fileName = "alert.txt";
                break;
            }
            case BATTERY: {
                fileName = "battery.txt";
                break;
            }
            case RAW_DATA_LOG: {
                fileName = "rawdatalog.txt";
                break;
            }
            case APP_ERROR_LOG: {
                fileName = "apperrorlog.txt";
                break;
            }
            case MOTION_DATA: {
                fileName = "motiondata.txt";
                break;
            }
            case RAW_BATTERY: {
                fileName = "rawbattery.txt";
                break;
            }
            case REGULAR_RESULT: {
                fileName = "regularresult.txt";
                break;
            }
        }
        return fileName;
    }

    public void storeContentListToLocal(List<String> localContentList, int localTarget) {
        Object lock = getLock(localTarget);
        if (lock == null) {
            return;
        }
        synchronized (lock) {
            StringBuilder content = new StringBuilder();
            for (String localContent : localContentList) {
                content.append(localContent).append("\n");
            }
            FileUtil.writeContentToLocalFile(SD_PATH, getFileName(localTarget), content.toString());
        }
    }

    public void appendContentToLocal(String content, int localTarget) {
        List<String> localContentList = getLocalContentList(localTarget);
        localContentList.add(content);
        storeContentListToLocal(localContentList, localTarget);
    }

}
