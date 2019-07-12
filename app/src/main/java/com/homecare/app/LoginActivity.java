package com.homecare.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.homecare.app.util.ExceptionUtil;

import org.apache.commons.codec.digest.DigestUtils;

public class LoginActivity extends Activity {

    private EditText account;
    private EditText password;
    private Button login;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // 初始化账号、密码、记住密码、自动登录、登录按钮
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);

        account.setText(HomeCareApp.sp.getString(HomeCareApp.ACCOUNT_KEY, ""));
        password.setText("");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String accountValue = account.getText().toString();
                String passwordValue = password.getText().toString();
                SharedPreferences.Editor editor = HomeCareApp.sp.edit();

                // 保存账号和密码
                editor.putString(HomeCareApp.ACCOUNT_KEY, accountValue);
                editor.putString(HomeCareApp.PASSWORD_KEY, DigestUtils.md5Hex(passwordValue));
                editor.apply();

                // 跳转
                Intent intent = new Intent(LoginActivity.this, HomeCareActivity.class);
                startActivity(intent);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ExceptionUtil.trackUncaughtException(LoginActivity.class.getSimpleName());
    }

}
