package com.homecare.app.widget.register;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.homecare.app.HomeCareActivity;
import com.homecare.app.HomeCareApp;
import com.homecare.app.R;
import com.homecare.app.model.InfoContainer;
import com.homecare.app.model.RegisterInfo;
import com.homecare.app.service.CloudService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public class PhysicalInfoFragment extends Fragment {

    private View rootView;

    private static final String REGISTER_INFO = "PhysicalInfoFragment.registerInfo";

    public static PhysicalInfoFragment newInstance(RegisterInfo registerInfo) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(REGISTER_INFO, registerInfo);
        PhysicalInfoFragment fragment = new PhysicalInfoFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_physicalinfo, container, false);
        Bundle arguments = getArguments();
        final RegisterInfo registerInfo = (RegisterInfo) arguments.getSerializable(REGISTER_INFO);
        final EditText height = (EditText) rootView.findViewById(R.id.registerHeight);
        height.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String heightContent = height.getText().toString();
                    if (StringUtils.isEmpty(heightContent)) {
                        Toast.makeText(rootView.getContext(), "身高不能为空！", Toast.LENGTH_SHORT).show();
                    } else {
                        int heightValue = Integer.parseInt(heightContent);
                        if (heightValue <= 0) {
                            Toast.makeText(rootView.getContext(), "身高应该大于0！", Toast.LENGTH_SHORT).show();
                        } else {
                            if (registerInfo != null) {
                                registerInfo.setHeight(heightValue);
                            }
                        }
                    }
                }
            }
        });
        final EditText weight = (EditText) rootView.findViewById(R.id.registerWeight);
        weight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String weightContent = weight.getText().toString();
                    if (StringUtils.isEmpty(weightContent)) {
                        Toast.makeText(rootView.getContext(), "体重不能为空！", Toast.LENGTH_SHORT).show();
                    } else {
                        int weightValue = Integer.parseInt(weightContent);
                        if (weightValue <= 0) {
                            Toast.makeText(rootView.getContext(), "体重应该大于0！", Toast.LENGTH_SHORT).show();
                        } else {
                            if (registerInfo != null) {
                                registerInfo.setWeight(weightValue);
                            }
                        }
                    }
                }
            }
        });
        final Button complete = (Button) rootView.findViewById(R.id.registerComplete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerInfo == null) {
                    return;
                }
                if (registerInfo.getMobile() == null || registerInfo.getMobile().length() != 11) {
                    Toast.makeText(rootView.getContext(), "手机号输入错误！", Toast.LENGTH_SHORT).show();
                } else if (registerInfo.getPassword() == null || registerInfo.getPassword().length() != 6) {
                    Toast.makeText(rootView.getContext(), "密码输入错误！", Toast.LENGTH_SHORT).show();
                } else if (registerInfo.getName() == null) {
                    Toast.makeText(rootView.getContext(), "姓名输入错误！", Toast.LENGTH_SHORT).show();
                } else if (registerInfo.getHeight() == 0) {
                    Toast.makeText(rootView.getContext(), "身高输入错误！", Toast.LENGTH_SHORT).show();
                } else if (registerInfo.getWeight() == 0) {
                    String weightContent = weight.getText().toString();
                    if (StringUtils.isEmpty(weightContent)) {
                        Toast.makeText(rootView.getContext(), "体重不能为空！", Toast.LENGTH_SHORT).show();
                    } else {
                        int weightValue = Integer.parseInt(weightContent);
                        if (weightValue <= 0) {
                            Toast.makeText(rootView.getContext(), "体重应该大于0！", Toast.LENGTH_SHORT).show();
                        } else {
                            registerInfo.setWeight(weightValue);
                            register(registerInfo, container);
                        }
                    }
                } else {
                    register(registerInfo, container);
                }
            }
        });
        return rootView;
    }

    private void register(RegisterInfo registerInfo, ViewGroup container) {
        Boolean succeed = CloudService.getInstance().register(registerInfo);
        if (succeed == null) {
            Toast.makeText(rootView.getContext(), "网络异常！", Toast.LENGTH_SHORT).show();
        } else if (succeed) {
            SharedPreferences.Editor editor = HomeCareApp.sp.edit();
            // 保存账号和密码
            editor.putString(HomeCareApp.ACCOUNT_KEY, registerInfo.getMobile());
            editor.putString(HomeCareApp.PASSWORD_KEY, DigestUtils.md5Hex(registerInfo.getPassword()));
            editor.apply();
            Intent intent = new Intent(container.getContext(), HomeCareActivity.class);
            startActivity(intent);
            InfoContainer.elderlyId = registerInfo.getMobile();
            ((Activity) container.getContext()).finish();
        }
    }

}
