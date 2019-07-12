package com.homecare.app;

import java.util.*;
import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.homecare.app.model.Constants;
import com.homecare.app.model.InfoContainer;
import com.homecare.app.model.SGInfo;
import com.homecare.app.service.CloudService;
import com.homecare.app.util.ExceptionUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public class HomeCareActivity extends Activity implements Constants {

    private Timer timer;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            timer.cancel();
            Intent intent = new Intent(HomeCareActivity.this, MainActivity.class);
            startActivity(intent);
            InfoContainer.elderlyId = (String) msg.obj;
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);

        HomeCareApp.sp = getSharedPreferences(HomeCareApp.SP_NAME, 0);
        final String account = HomeCareApp.sp.getString(HomeCareApp.ACCOUNT_KEY, "");
        if (!account.equals(InfoContainer.elderlyId)) {
            String password = HomeCareApp.sp.getString(HomeCareApp.PASSWORD_KEY, "");
            if (StringUtils.isEmpty(account) && StringUtils.isEmpty(password)) {
                startLoginActivity(null);
                return;
            }
            if (StringUtils.isEmpty(account)) {
                startLoginActivity("请输入手机号！");
                return;
            }
            if (StringUtils.isEmpty(password)) {
                startLoginActivity("请输入密码！");
                return;
            }
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }
            String phoneId = getPhoneId();
            String result = CloudService.getInstance().validateLogin(account, password, phoneId);
            if (result == null) {
                startLoginActivity("网络异常！");
                return;
            }
            if (result.equals("")) {
                startLoginActivity("手机或密码错误，请重新登录！");
                return;
            }
            if (!result.equals(phoneId)) {
                startLoginActivity("账号：" + account + " 已经在另一部手机上登录！");
                return;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SGInfo newInfo = CloudService.getInstance().getSGInfo(account, 2000);
                if (InfoContainer.sgInfo == null) {
                    InfoContainer.sgInfo = newInfo;
                } else {
                    if (newInfo != null) {
                        newInfo.updateWithExistInfo(InfoContainer.sgInfo);
                        InfoContainer.sgInfo = newInfo;
                    }
                }
            }
        }).start();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.obj = account;
                msg.setTarget(handler);
                handler.sendMessage(msg);
            }
        }, 2000);

        ExceptionUtil.trackUncaughtException(HomeCareActivity.class.getSimpleName());
    }

    private String getPhoneId() {
        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        return DigestUtils.md5Hex(TelephonyMgr.getDeviceId());
    }

    private void startLoginActivity(String errMsg) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        if (errMsg != null) {
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}
