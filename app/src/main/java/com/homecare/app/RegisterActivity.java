package com.homecare.app;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import com.homecare.app.model.RegisterInfo;
import com.homecare.app.util.ExceptionUtil;
import com.homecare.app.widget.register.*;

public class RegisterActivity extends FragmentActivity {

    private StepPagerStrip stepPagerStrip;
    private ViewPager viewPager;
    private WizardAdapter adapter;
    private RegisterInfo registerInfo = new RegisterInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new WizardAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        stepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        stepPagerStrip.setPageCount(adapter.getCount());

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                stepPagerStrip.setCurrentPage(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        ExceptionUtil.trackUncaughtException(RegisterActivity.class.getSimpleName());
    }

    public class WizardAdapter extends SmartFragmentStatePagerAdapter {
        public WizardAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return AccountFragment.newInstance(registerInfo);
                case 1:
                    return BasicInfoFragment.newInstance(registerInfo);
                case 2:
                    return PhysicalInfoFragment.newInstance(registerInfo);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
