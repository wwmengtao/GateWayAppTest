package com.homecare.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.homecare.app.ble.profile.BleProfileService;
import com.homecare.app.ble.scanner.DeviceListAdapter;
import com.homecare.app.ble.scanner.ScannerFragment;
import com.homecare.app.ble.uart.UARTInterface;
import com.homecare.app.ble.uart.UARTService;
import com.homecare.app.model.Constants;
import com.homecare.app.model.InfoContainer;
import com.homecare.app.model.SGInfo;
import com.homecare.app.model.WearableInfo;
import com.homecare.app.model.ble.BatteryStatus;
import com.homecare.app.model.ble.BatteryStatusRequest;
import com.homecare.app.model.ble.DfuRequest;
import com.homecare.app.model.ble.ExchangeInfo;
import com.homecare.app.model.ble.HeartRateRequest;
import com.homecare.app.model.ble.HeartRateResult;
import com.homecare.app.model.ble.UnBondRequest;
import com.homecare.app.model.ble.WallClockConfig;
import com.homecare.app.service.BatteryService;
import com.homecare.app.service.CloudService;
import com.homecare.app.service.ConnectionService;
import com.homecare.app.service.GpsService;
import com.homecare.app.service.SdService;
import com.homecare.app.util.ExceptionUtil;
import com.homecare.app.util.FileUtil;
import com.homecare.app.util.TimeUtil;
import com.homecare.app.widget.BaseScrollLayout;
import com.homecare.app.widget.settings.SettingsActivity;
import com.igexin.sdk.PushManager;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import no.nordicsemi.android.nrftoolbox.dfu.DfuActivity;

public class MainActivity extends AppCompatActivity implements OnViewChangeListener, View.OnClickListener, ScannerFragment.OnDeviceSelectedListener, Constants {

    private BaseScrollLayout mScrollLayout;
    private LinearLayout[] mImageViews;
    private int mViewCount;
    private int mCurSel;

    private TextView monitorTab;
    private TextView wearableTab;

    private static TextView wearableConnectView;
    private static TextView cloudConnectView;
    private static TextView wearableBatteryView;
    private static TextView heartRateView;
    private static TextView sqView;
    private static TextView tsView;
    public static Handler wearableViewHandler = new WearableViewHandler();
    public static Handler cloudViewHandler = new CloudViewHandler();

    private void onLogout() {
        InfoContainer.elderlyId = null;
        if (daemonScheduler != null) {
            daemonScheduler.shutdown();
            daemonScheduler = null;
        }
    }

    private static class WearableViewHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (wearableConnectView == null || wearableBatteryView == null || heartRateView == null || sqView == null || tsView == null) {
                return;
            }
            Object info = msg.obj;
            WearableInfo wearableInfo = InfoContainer.sgInfo.getWearableInfo();
            if (info instanceof Boolean) {
                boolean connect = (boolean) info;
                if (connect) {
                    wearableConnectView.setText(R.string.connect_ok);
                    String text = wearableConnectView.getText().toString();
                    wearableConnectView.setText(text + "    ");
                } else {
                    if (DeviceListAdapter.getBondedDeviceCount() > 0) {
                        wearableConnectView.setText(R.string.connect_fail);
                        String text = wearableConnectView.getText().toString();
                        wearableConnectView.setText(text + "    ");
                    } else {
                        wearableConnectView.setText(R.string.connect_none);
                    }
                }
            } else if (info instanceof BatteryStatus) {
                BatteryStatus batteryStatus = (BatteryStatus) info;
                int percent = batteryStatus.getPercent();
                wearableInfo.setLastBattery(percent);
                wearableBatteryView.setText(fillBatteryViewText(percent));
            } else if (info instanceof HeartRateResult) {
                HeartRateResult heartRateResult = (HeartRateResult) info;
                int hr = heartRateResult.getHearRate();
                int sq = heartRateResult.getSq();
                long timestamp = heartRateResult.getTimestamp();
                wearableInfo.setLastHr(hr);
                wearableInfo.setLastSq(sq);
                wearableInfo.setLastHrTs(timestamp);
                heartRateView.setText(fillHRViewText(hr));
                sqView.setText(fillHRViewText(sq));
                tsView.setText(fillTsViewText(TimeUtil.displayByDateAndHourAndMinuteAndSecond(timestamp)));
            }
        }
    }

    private static class CloudViewHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (cloudConnectView == null) {
                return;
            }
            Object info = msg.obj;
            if (info instanceof Boolean) {
                boolean connect = (boolean) info;
                if (connect) {
                    cloudConnectView.setText(R.string.connect_ok);
                } else {
                    cloudConnectView.setText(R.string.connect_fail);
                }
            }
        }
    }

    private static ScheduledExecutorService daemonScheduler = null;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (InfoContainer.sgInfo == null) {
            Toast.makeText(getApplicationContext(), "网络异常！", Toast.LENGTH_LONG).show();
            onLogout();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (StringUtils.isNotEmpty(InfoContainer.sgInfo.getElderlyInfo().getName())) {
            setTitle("Kiwi - " + InfoContainer.sgInfo.getElderlyInfo().getName());
        }
        initWidgets();
        initDemonServices();
        initPush();
        setVersion();
        daemonScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                if (InfoContainer.infoInvalid()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            checkFirmwareVersion(MainActivity.this, false);
                            checkAppVersion(MainActivity.this, false);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
            }
        }, 30, TimeUnit.SECONDS);
        ExceptionUtil.trackUncaughtException(MainActivity.class.getSimpleName());
    }

    private void setVersion() {
        String versionName = "";
        PackageManager manager = getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        InfoContainer.sgInfo.setAppVersion(versionName);
    }

    public static void checkFirmwareVersion(final Context context, boolean showTips) {
        SGInfo sgInfo = InfoContainer.sgInfo;
        String lastVersion = CloudService.getInstance().getLastWearableFirmwareVersion(sgInfo.getWearableInfo().getHardwareVersion());
        if (lastVersion == null) {
            Toast.makeText(context, "检查固件更新失败：网络异常！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastVersion.equals(sgInfo.getWearableInfo().getFirmwareVersion())) {
            if (showTips) {
                Toast.makeText(context, "手环固件已经是最新版本", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (StringUtils.isNotEmpty(lastVersion) && StringUtils.isNotEmpty(sgInfo.getWearableInfo().getFirmwareVersion())) {
                int phoneBattery = sgInfo.getPhoneBattery();
                if (phoneBattery > PHONE_LOWEST_BATTERY) {
                    Dialog dialog = new AlertDialog.Builder(context).setTitle("固件更新").setMessage("发现新版本：" + lastVersion)
                            .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pBar = new ProgressDialog(context);
                                    pBar.setCanceledOnTouchOutside(false);
                                    pBar.setTitle("正在下载");
                                    pBar.setMessage("请稍候...");
                                    pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    downloadFirmware(context);
                                }
                            })
                            .setNegativeButton("取消", null).create();
                    dialog.show();
                } else {
                    if (showTips) {
                        Toast.makeText(context, "手机电量不足，请先给手机充电再升级", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private static ProgressDialog pBar;

    public static void checkAppVersion(final Context context, boolean showTips) {
        String lastVersion = CloudService.getInstance().getLastMobileAppVersion();
        if (lastVersion == null) {
            Toast.makeText(context, "检查APP更新失败：网络异常！", Toast.LENGTH_SHORT).show();
            return;
        }
        SGInfo sgInfo = InfoContainer.sgInfo;
        if (lastVersion.equals(sgInfo.getAppVersion())) {
            if (showTips) {
                Toast.makeText(context, "APP已经是最新版本", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (StringUtils.isNotEmpty(lastVersion)) {
                if (sgInfo.getPhoneBattery() > PHONE_LOWEST_BATTERY) {
                    Dialog dialog = new AlertDialog.Builder(context).setTitle("APP更新").setMessage("发现新版本：" + lastVersion)
                            .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pBar = new ProgressDialog(context);
                                    pBar.setCanceledOnTouchOutside(false);
                                    pBar.setTitle("正在下载");
                                    pBar.setMessage("请稍候...");
                                    pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    downloadApk(context);
                                }
                            })
                            .setNegativeButton("取消", null).create();
                    dialog.show();
                } else {
                    if (showTips) {
                        Toast.makeText(context, "手机电量不足，请先给手机充电再升级", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private static void downloadApk(final Context context) {
        pBar.show();
        new Thread() {
            public void run() {
                if (!FileUtil.mkdir(SdService.SD_PATH)) {
                    return;
                }
                File localFile = new File(SdService.SD_PATH + "/" + APK_FILE_NAME);
                boolean download = CloudService.getInstance().downloadApk(localFile);
                pBar.cancel();
                if (download) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(localFile), "application/vnd.android.package-archive");
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "APP更新失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    private static void downloadFirmware(final Context context) {
        pBar.show();
        new Thread() {
            public void run() {
                if (!FileUtil.mkdir(SdService.SD_PATH)) {
                    return;
                }
                String hardwareVersion = InfoContainer.sgInfo.getWearableInfo().getHardwareVersion();
                File localFile = new File(SdService.SD_PATH + "/" + FIRMWARE_FILE_NAME);
                boolean download = CloudService.getInstance().downloadFirmware(hardwareVersion, localFile);
                pBar.cancel();
                if (download) {
                    DfuRequest dfuRequest = new DfuRequest();
                    sendAckedRequest(dfuRequest, 3);
                    Intent intent = new Intent(context, DfuActivity.class);
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "固件更新失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    private void initWidgets() {
        monitorTab = (TextView) findViewById(R.id.monitor);
        wearableTab = (TextView) findViewById(R.id.wearable);

        wearableConnectView = (TextView) findViewById(R.id.wearableConnectView2);
        cloudConnectView = (TextView) findViewById(R.id.cloudConnectView2);
        wearableBatteryView = (TextView) findViewById(R.id.wearableBatteryView2);
        heartRateView = (TextView) findViewById(R.id.hrView2);
        sqView = (TextView) findViewById(R.id.sqView2);
        tsView = (TextView) findViewById(R.id.tsView2);

        WearableInfo wearableInfo = InfoContainer.sgInfo.getWearableInfo();
        if (wearableInfo.getLastBattery() < 0) {
            wearableBatteryView.setText(getString(R.string.empty_wearable_view));
        } else {
            wearableBatteryView.setText(fillBatteryViewText(wearableInfo.getLastBattery()));
        }
        if (wearableInfo.getLastHr() < 0) {
            heartRateView.setText(getString(R.string.empty_wearable_view));
        } else {
            heartRateView.setText(fillHRViewText(wearableInfo.getLastHr()));
        }
        if (wearableInfo.getLastSq() < 0) {
            sqView.setText(getString(R.string.empty_wearable_view));
        } else {
            sqView.setText(fillHRViewText(wearableInfo.getLastSq()));
        }
        if (wearableInfo.getLastHrTs() <= 0) {
            tsView.setText(getString(R.string.empty_wearable_view) + "                ");
        } else {
            tsView.setText(fillTsViewText(TimeUtil.displayByDateAndHourAndMinuteAndSecond(wearableInfo.getLastHrTs())));
        }
        checkConnect(InfoContainer.elderlyId, false);

        mScrollLayout = (BaseScrollLayout) findViewById(R.id.ScrollLayout);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lllayout);
        mViewCount = mScrollLayout.getChildCount();
        mImageViews = new LinearLayout[mViewCount];
        for (int i = 0; i < mViewCount; i++) {
            mImageViews[i] = (LinearLayout) linearLayout.getChildAt(i);
            mImageViews[i].setEnabled(true);
            mImageViews[i].setOnClickListener(this);
            mImageViews[i].setTag(i);
        }
        mCurSel = 0;
        mImageViews[mCurSel].setEnabled(false);
        mScrollLayout.SetOnViewChangeListener(this);
    }

    private static String fillHRViewText(int value) {
        int count;
        if (value < 10) {
            count = 10;
        } else if (value < 100) {
            count = 8;
        } else {
            count = 6;
        }
        StringBuilder text = new StringBuilder();
        text.append(value);
        for (int i = 0; i < count; i++) {
            text.append(" ");
        }
        return text.toString();
    }

    private static String fillTsViewText(String text) {
        return text + "  ";
    }

    private static String fillBatteryViewText(int value) {
        int count;
        if (value < 10) {
            count = 7;
        } else if (value < 100) {
            count = 5;
        } else {
            count = 3;
        }
        StringBuilder text = new StringBuilder();
        text.append(value).append("%");
        for (int i = 0; i < count; i++) {
            text.append(" ");
        }
        return text.toString();
    }

    private void initPush() {
        PushManager.getInstance().initialize(getApplicationContext());
    }

    private void initDemonServices() {
        final SGInfo sgInfo = InfoContainer.sgInfo;
        final String elderlyId = InfoContainer.elderlyId;
        final String deviceAddress = sgInfo.getWearableInfo().getDeviceAddress();
        if (deviceAddress != null && !isDeviceConnected()) {
            startUARTService(deviceAddress);
        }
        Intent bsi = new Intent(MainActivity.this, BatteryService.class);
        startService(bsi);
        Intent csi = new Intent(MainActivity.this, ConnectionService.class);
        startService(csi);
        Intent gsi = new Intent(MainActivity.this, GpsService.class);
        startService(gsi);
        if (daemonScheduler == null) {
            daemonScheduler = Executors.newSingleThreadScheduledExecutor();
            int runInterval = calcRunIntervalForDaemonScheduler();
            daemonScheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    String deviceAddress = sgInfo.getWearableInfo().getDeviceAddress();
                    boolean connect = CloudService.getInstance().sendHeartbeat(elderlyId);
                    if (connect) {
                        CloudService.getInstance().uploadFromLocalStorage(elderlyId);
                    }
                    if (deviceAddress != null) {
                        if (!isDeviceConnected()) {
                            startUARTService(deviceAddress);
                        }
                        if (sgInfo.getLastWallClockTs() < (System.currentTimeMillis() - ONE_DAY_IN_MS)) {
                            WallClockConfig wallClockConfig = new WallClockConfig();
                            sendAckedRequest(wallClockConfig, 3);
                            sgInfo.setLastWallClockTs(System.currentTimeMillis());
                        }
                    }
                }
            }, 0, runInterval, TimeUnit.MINUTES);
            daemonScheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    String deviceAddress = sgInfo.getWearableInfo().getDeviceAddress();
                    if (deviceAddress != null && isDeviceConnected()) {
                        sendAckedRequest(new BatteryStatusRequest(), 1);
                    }
                }
            }, 10, 10, TimeUnit.MINUTES);
        }
    }

    private int calcRunIntervalForDaemonScheduler() {
        long monitorInterval = InfoContainer.sgInfo.getMonitorInterval();
        if (monitorInterval == 0) {
            monitorInterval = 10;
        }
        return (int) monitorInterval / 2 - 1;
    }

    private void setCurPoint(int index) {
        if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
            return;
        }
        mImageViews[mCurSel].setEnabled(true);
        mImageViews[index].setEnabled(false);
        mCurSel = index;

        if (index == 0) {
            monitorTab.setTextColor(0xff228B22);
            wearableTab.setTextColor(Color.BLACK);
        } else {
            monitorTab.setTextColor(Color.BLACK);
            wearableTab.setTextColor(0xff228B22);
        }
    }

    @Override
    public void OnViewChange(int view) {
        setCurPoint(view);
    }

    @Override
    public void onClick(View v) {
        int pos = (Integer) (v.getTag());
        setCurPoint(pos);
        mScrollLayout.snapToScreen(pos);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return (keyCode == KeyEvent.KEYCODE_MENU) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String elderlyId = InfoContainer.elderlyId;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_scan:
                if (isBLEEnabled()) {
                    showDeviceScanningDialog(getFilterUUID(), isDiscoverableRequired());
                }
                return true;
            case R.id.action_settings:
                Intent sa = new Intent(this, SettingsActivity.class);
                this.startActivity(sa);
                return true;
            case R.id.action_check:
                if (mCurSel == 0) {
                    if (!isDeviceConnected()) {
                        Toast.makeText(this, "未连接手环，无法开始检测", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    Toast.makeText(this, "开始检测老人心率...", Toast.LENGTH_SHORT).show();
//                    wearableBatteryView.setText(getString(R.string.waiting));
                    final String lastHrViewValue = heartRateView.getText().toString();
                    final String lastSqViewValue = sqView.getText().toString();
                    final String lastTsViewValue = tsView.getText().toString();
                    heartRateView.setText(getString(R.string.waiting));
                    sqView.setText(getString(R.string.waiting));
                    tsView.setText(getString(R.string.waiting));
//                    BatteryStatusRequest batteryStatusRequest = new BatteryStatusRequest();
//                    sendAckedRequest(batteryStatusRequest, 1);
                    HeartRateRequest heartRateRequest = new HeartRateRequest();
                    sendAckedRequest(heartRateRequest, 1);
                    daemonScheduler.schedule(new Runnable() {
                        @Override
                        public void run() {
                            if (getString(R.string.waiting).equals(heartRateView.getText())) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(MainActivity.this, "手环未返回心率检测值", Toast.LENGTH_SHORT).show();
                                            heartRateView.setText(lastHrViewValue);
                                            sqView.setText(lastSqViewValue);
                                            tsView.setText(lastTsViewValue);
                                        } catch (Exception e) {
                                            Log.e(TAG, e.getMessage());
                                        }
                                    }
                                });
                            }
                        }
                    }, 120, TimeUnit.SECONDS);
                } else {
                    checkConnect(elderlyId, true);
                }
                return true;
            case R.id.action_exit:
                CloudService.getInstance().logout(elderlyId);
                onLogout();
                SharedPreferences.Editor editor = HomeCareApp.sp.edit();
                editor.putString(HomeCareApp.ACCOUNT_KEY, elderlyId);
                editor.putString(HomeCareApp.PASSWORD_KEY, "");
                editor.apply();
                Intent la = new Intent(this, LoginActivity.class);
                //this.startActivity(la);
                //this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkConnect(String elderlyId, boolean showTips) {
        if (showTips) {
            Toast.makeText(this, "开始测试与手环/云端的连接 ...", Toast.LENGTH_LONG).show();
        }
        cloudConnectView.setText(getString(R.string.waiting));
        wearableConnectView.setText(getString(R.string.waiting));
        boolean cloudConnected = CloudService.getInstance().sendHeartbeat(elderlyId, 2000);
        boolean wearableConnected = mService != null && mService.isConnected();
        if (cloudConnected && wearableConnected) {
            if (showTips) {
                Toast.makeText(this, "手环/云端连接正常", Toast.LENGTH_SHORT).show();
            }
            cloudConnectView.setText(R.string.connect_ok);
            wearableConnectView.setText(getString(R.string.connect_ok) + "    ");
        } else {
            if (!cloudConnected) {
                if (showTips) {
                    Toast.makeText(this, "云端连接已断开", Toast.LENGTH_SHORT).show();
                }
                cloudConnectView.setText(R.string.connect_fail);
            } else {
                if (showTips) {
                    Toast.makeText(this, "云端连接正常", Toast.LENGTH_SHORT).show();
                }
                cloudConnectView.setText(R.string.connect_ok);
            }
            if (!wearableConnected) {
                if (InfoContainer.sgInfo.getWearableInfo().getDeviceAddress() != null) {
                    if (showTips) {
                        Toast.makeText(this, "手环连接已断开", Toast.LENGTH_SHORT).show();
                    }
                    wearableConnectView.setText(getString(R.string.connect_fail) + "    ");
                } else {
                    if (showTips) {
                        Toast.makeText(this, "未连接手环", Toast.LENGTH_SHORT).show();
                    }
                    wearableConnectView.setText(R.string.connect_none);
                }
            } else {
                if (showTips) {
                    Toast.makeText(this, "手环连接正常", Toast.LENGTH_SHORT).show();
                }
                wearableConnectView.setText(getString(R.string.connect_ok) + "    ");
            }
        }
    }

    private static final int REQUEST_ENABLE_BT = 1;

    private static UARTService.LocalBinder mService;

    private boolean isBLEEnabled() {
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        Context context = getApplicationContext();
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "手机不支持BLE！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "手机不支持蓝牙！", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        return true;
    }

    private UUID getFilterUUID() {
        return null;
    }

    private boolean isDiscoverableRequired() {
        return false;
    }

    private void showDeviceScanningDialog(final UUID filter, final boolean discoverableRequired) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ScannerFragment dialog = ScannerFragment.getInstance(filter, discoverableRequired);
                dialog.show(getSupportFragmentManager(), "scan_fragment");
            }
        });
    }

    @Override
    public void onDeviceSelectedToBond(final BluetoothDevice device, final String name) {
        startUARTService(device.getAddress());
    }

    private void startUARTService(String deviceAddress) {
        // The device may not be in the range but the service will try to connect to it if it reach it
        final Intent service = new Intent(this, UARTService.class);
        service.putExtra(BleProfileService.EXTRA_DEVICE_ADDRESS, deviceAddress);
        stopService(service);
        startService(service);
        bindService(service, new ServiceConnection() {
            @SuppressWarnings("unchecked")
            @Override
            public void onServiceConnected(final ComponentName name, final IBinder service) {
                mService = (UARTService.LocalBinder) service;
            }
            @Override
            public void onServiceDisconnected(final ComponentName name) {
                mService = null;
            }
        }, 0);
    }

    @Override
    public void onDeviceSelectedToUnbond(final BluetoothDevice device) {
        SGInfo sgInfo = InfoContainer.sgInfo;
        String bondToken = sgInfo.getBondToken();
        if (StringUtils.isNotEmpty(bondToken)) {
            UnBondRequest unBondRequest = new UnBondRequest(sgInfo.getBondToken());
            sendAckedRequest(unBondRequest, 3);
        }
        CloudService.getInstance().unbondDevice(InfoContainer.elderlyId);
        sgInfo.getWearableInfo().setDeviceAddress(null);
//        if (mService != null) {
//            mService.disconnect();
//        }
    }

    @Override
    public void onDialogCanceled() {
    }

    @Override
    public boolean isDeviceConnected() {
        return isBLEConnected();
    }

    public static boolean isBLEConnected() {
        return mService != null && mService.isConnected();
    }

    public static boolean sendAckedRequest(ExchangeInfo request, int maxTryTimes) {
        if (mService != null) {
            ((UARTInterface) mService).sendAckedRequest(request, maxTryTimes);
            return true;
        } else {
            return false;
        }
    }
}
