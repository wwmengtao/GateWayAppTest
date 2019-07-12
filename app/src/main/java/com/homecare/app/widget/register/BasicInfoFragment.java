package com.homecare.app.widget.register;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.homecare.app.R;
import com.homecare.app.model.RegisterInfo;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BasicInfoFragment extends Fragment {

    private View rootView;

    private static final String REGISTER_INFO = "BasicInfoFragment.registerInfo";

    public static BasicInfoFragment newInstance(RegisterInfo registerInfo) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(REGISTER_INFO, registerInfo);
        BasicInfoFragment fragment = new BasicInfoFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_basicinfo, container, false);
        Bundle arguments = getArguments();
        final RegisterInfo registerInfo = (RegisterInfo) arguments.getSerializable(REGISTER_INFO);
        final EditText name = (EditText) rootView.findViewById(R.id.registerName);
        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String nameContent = name.getText().toString();
                    if (StringUtils.isEmpty(nameContent)) {
                        Toast.makeText(rootView.getContext(), "姓名不能为空！", Toast.LENGTH_SHORT).show();
                    } else {
                        registerInfo.setName(nameContent);
                    }
                }
            }
        });
        RadioButton male = (RadioButton) rootView.findViewById(R.id.register_btn_man);
        if (male.isChecked()) {
            registerInfo.setMale(true);
        } else {
            registerInfo.setMale(false);
        }
        registerInfo.setBirthday("1960/01/01");
        DatePicker datePicker = (DatePicker) rootView.findViewById(R.id.registerBirthday);
        datePicker.init(1960, 0, 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String birthday = sdf.format(c.getTime());
                registerInfo.setBirthday(birthday);
            }
        });
        return rootView;
    }

}
