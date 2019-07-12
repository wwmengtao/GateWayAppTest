package com.homecare.app.widget.register;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.homecare.app.R;
import com.homecare.app.model.RegisterInfo;
import com.homecare.app.service.CloudService;
import org.apache.commons.lang3.StringUtils;

public class AccountFragment extends Fragment {

    private View rootView;

    private static final String REGISTER_INFO = "AccountFragment.registerInfo";

    public static AccountFragment newInstance(RegisterInfo registerInfo) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(REGISTER_INFO, registerInfo);
        AccountFragment fragment = new AccountFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_account, container, false);
        Bundle arguments = getArguments();
        final RegisterInfo registerInfo = (RegisterInfo) arguments.getSerializable(REGISTER_INFO);
        final EditText account = (EditText) rootView.findViewById(R.id.registerAccount);
        account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String mobile = account.getText().toString();
                    if (StringUtils.isEmpty(mobile)) {
                        Toast.makeText(rootView.getContext(), "手机号不能为空！", Toast.LENGTH_SHORT).show();
                    } else {
                        if (mobile.length() != 11) {
                            Toast.makeText(rootView.getContext(), "手机号应该是11位！", Toast.LENGTH_SHORT).show();
                        } else {
                            Boolean valid = CloudService.getInstance().validateMobile(mobile);
                            if (valid == null) {
                                Toast.makeText(rootView.getContext(), "网络异常！", Toast.LENGTH_SHORT).show();
                            } else if (!valid) {
                                Toast.makeText(rootView.getContext(), "手机号已经被注册！", Toast.LENGTH_SHORT).show();
                            } else {
                                registerInfo.setMobile(mobile);
                            }
                        }
                    }
                }
            }
        });
        final EditText password = (EditText) rootView.findViewById(R.id.registerPassword);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String passContent = password.getText().toString();
                    if (StringUtils.isEmpty(passContent)) {
                        Toast.makeText(rootView.getContext(), "不能输入空密码！", Toast.LENGTH_SHORT).show();
                    } else {
                        if (passContent.length() != 6) {
                            Toast.makeText(rootView.getContext(), "密码应该是6位数字！", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        final EditText passwordConfirm = (EditText) rootView.findViewById(R.id.registerPasswordConfirm);
        passwordConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String passContent = password.getText().toString();
                    String passConfirmContent = passwordConfirm.getText().toString();
                    if (StringUtils.isNotEmpty(passContent) && StringUtils.isNotEmpty(passConfirmContent)) {
                        if (!passContent.equals(passConfirmContent)) {
                            Toast.makeText(rootView.getContext(), "两次输入密码不一致！", Toast.LENGTH_SHORT).show();
                        } else {
                            registerInfo.setPassword(passContent);
                        }
                    } else {
                        Toast.makeText(rootView.getContext(), "不能输入空密码！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return rootView;
    }

}
