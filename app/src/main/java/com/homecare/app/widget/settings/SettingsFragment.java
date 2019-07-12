package com.homecare.app.widget.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.homecare.app.MainActivity;
import com.homecare.app.R;
import com.homecare.app.model.Constants;
import com.homecare.app.model.ElderlyConfig;
import com.homecare.app.model.InfoContainer;
import com.homecare.app.model.SGInfo;
import com.homecare.app.model.ble.ElderlyInfo;
import com.homecare.app.model.ble.HealthThresholdConfig;
import com.homecare.app.model.ble.RegularReportConfig;
import com.homecare.app.service.CloudService;

import org.apache.commons.lang3.StringUtils;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Constants {

    private EditTextPreference height;
    private EditTextPreference weight;
    private EditTextPreference hrFast;
    private EditTextPreference hrSlow;
    private ListPreference monitorInterval;
    private Preference firmwareVersion;
    private Preference token;
    private Preference appVersion;

    private static final String HEIGHT_KEY = "setting_height";
    private static final String WEIGHT_KEY = "setting_weight";
    private static final String HR_FAST_KEY = "setting_hrFast";
    private static final String HR_SLOW_KEY = "setting_hrSlow";
    private static final String MONITOR_INTERVAL_KEY = "setting_monitorInterval";
    private static final String FIRMWARE_VERSION_KEY = "setting_wearable_version";
    private static final String TOKEN_KEY = "setting_token";
    private static final String APP_VERSION_KEY = "setting_app_version";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        initSettings();
    }

    private void initSettings() {
        SGInfo info = InfoContainer.sgInfo;
        ElderlyInfo elderlyInfo = info.getElderlyInfo();
        this.height = (EditTextPreference) findPreference(HEIGHT_KEY);
        if (elderlyInfo != null) {
            this.height.setTitle("身高：" + elderlyInfo.getHeight());
            this.height.setText(elderlyInfo.getHeight() + "");
        } else {
            this.height.setTitle("身高：");
        }
        this.weight = (EditTextPreference) findPreference(WEIGHT_KEY);
        if (elderlyInfo != null) {
            this.weight.setTitle("体重：" + elderlyInfo.getWeight());
            this.weight.setText(elderlyInfo.getWeight() + "");
        } else {
            this.weight.setTitle("体重：");
        }
        ElderlyConfig config = info.getElderlyConfig();
        this.hrFast = (EditTextPreference) findPreference(HR_FAST_KEY);
        if (config != null) {
            this.hrFast.setTitle("心率过快阈值：" + config.getHrFastThreshold());
            this.hrFast.setText(config.getHrFastThreshold() + "");
        } else {
            this.hrFast.setTitle("心率过快阈值：");
        }
        this.hrSlow = (EditTextPreference) findPreference(HR_SLOW_KEY);
        if (config != null) {
            this.hrSlow.setTitle("心率过慢阈值：" + config.getHrSlowThreshold());
            this.hrSlow.setText(config.getHrSlowThreshold() + "");
        } else {
            this.hrSlow.setTitle("心率过慢阈值：");
        }
        this.monitorInterval = (ListPreference) findPreference(MONITOR_INTERVAL_KEY);
        if (config != null) {
            this.monitorInterval.setTitle("采集间隔：" + config.getHrCollectInterval());
            this.monitorInterval.setValue(config.getHrCollectInterval() + "");
        } else {
            this.monitorInterval.setTitle("采集间隔：");
        }
        this.firmwareVersion = findPreference(FIRMWARE_VERSION_KEY);
        this.firmwareVersion.setTitle("固件版本：" + info.getWearableInfo().getFirmwareVersion() + "   AMS固件版本：" + info.getWearableInfo().getAmsVersion());
        this.token = findPreference(TOKEN_KEY);
        this.token.setTitle("亲情码：" + info.getToken());
        this.appVersion = findPreference(APP_VERSION_KEY);
        this.appVersion.setTitle("应用版本：" + info.getAppVersion());
    }

    @Override
    public void onResume() {
        super.onResume();

        // attach the preference change listener. It will update the summary below interval preference
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        // unregister listener
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        String newValue = sharedPreferences.getString(key, "");
        SGInfo sgInfo = InfoContainer.sgInfo;
        String elderlyId = InfoContainer.elderlyId;
        ElderlyInfo elderlyInfo = sgInfo.getElderlyInfo();
        ElderlyConfig elderlyConfig = sgInfo.getElderlyConfig();
        final PreferenceScreen screen = getPreferenceScreen();

        if (key.equals(HEIGHT_KEY)) {
            elderlyInfo.setHeight(Integer.parseInt(newValue));
            MainActivity.sendAckedRequest(elderlyInfo, 3);
            CloudService.getInstance().updateSGInfo(elderlyId, SG_ATTR_HEIGHT, newValue);
            screen.findPreference(key).setTitle("身高：" + newValue);
        }
        if (key.equals(WEIGHT_KEY)) {
            elderlyInfo.setWeight(Integer.parseInt(newValue));
            MainActivity.sendAckedRequest(elderlyInfo, 3);
            CloudService.getInstance().updateSGInfo(elderlyId, SG_ATTR_WEIGHT, newValue);
            screen.findPreference(key).setTitle("体重：" + newValue);
        }
        if (key.equals(HR_FAST_KEY)) {
            int newHrFast = Integer.parseInt(newValue);
            HealthThresholdConfig hrThresholdConfig = new HealthThresholdConfig(HealthThresholdConfig.THRESHOLD_ID_HIGH_HEART_RATE, newHrFast);
            MainActivity.sendAckedRequest(hrThresholdConfig, 3);
            CloudService.getInstance().updateSGInfo(elderlyId, SG_ATTR_HRFAST, newHrFast);
            elderlyConfig.setHrFastThreshold(newHrFast);
            screen.findPreference(key).setTitle("心率过快阈值：" + newValue);
        }
        if (key.equals(HR_SLOW_KEY)) {
            int newHrSlow = Integer.parseInt(newValue);
            HealthThresholdConfig hrThresholdConfig = new HealthThresholdConfig(HealthThresholdConfig.THRESHOLD_ID_LOW_HEART_RATE, newHrSlow);
            MainActivity.sendAckedRequest(hrThresholdConfig, 3);
            CloudService.getInstance().updateSGInfo(elderlyId, SG_ATTR_HRSLOW, newHrSlow);
            elderlyConfig.setHrSlowThreshold(newHrSlow);
            screen.findPreference(key).setTitle("心率过慢阈值：" + newValue);
        }
        if (key.equals(MONITOR_INTERVAL_KEY)) {
            int newMonitorInterval = Integer.parseInt(newValue);
            RegularReportConfig regularReportConfig = new RegularReportConfig(newMonitorInterval, true, true, true, true, true, true, true, true, true, false, true, false, false);
            MainActivity.sendAckedRequest(regularReportConfig, 3);
            CloudService.getInstance().updateSGInfo(elderlyId, SG_ATTR_MONITORINTERVAL, newValue);
            elderlyConfig.setHrCollectInterval(newMonitorInterval);
            screen.findPreference(key).setTitle("采集间隔：" + newValue);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, @NonNull Preference preference) {
        String key = preference.getKey();
        if (key.equals(FIRMWARE_VERSION_KEY)) {
            MainActivity.checkFirmwareVersion(getActivity(), true);
        }
        if (key.equals(TOKEN_KEY)) {
            String token = CloudService.getInstance().generateToken(InfoContainer.elderlyId);
            if (StringUtils.isNotEmpty(token)) {
                InfoContainer.sgInfo.setToken(token);
                preference.setTitle("亲情码：" + token);
            } else {
                Toast.makeText(getActivity(), "生成亲情码失败：网络异常！", Toast.LENGTH_SHORT).show();
            }
        }
        if (key.equals(APP_VERSION_KEY)) {
            MainActivity.checkAppVersion(getActivity(), true);
        }
        return true;
    }
}
