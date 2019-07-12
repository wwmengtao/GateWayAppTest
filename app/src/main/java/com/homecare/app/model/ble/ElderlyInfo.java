package com.homecare.app.model.ble;

import com.homecare.app.ble.util.ParserUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ElderlyInfo extends AbstractRequest {
    private boolean isMale;
    private String birthday;
    private int height;
    private int weight;
    private int age;
    private String name;

    public ElderlyInfo(boolean isMale, String birthday, int height, int weight) {
        this.isMale = isMale;
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.age = getAge();
    }

    public boolean isMale() {
        return isMale;
    }

    public void setMale(boolean isMale) {
        this.isMale = isMale;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
        this.age = getAge();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date birthDate = null;
        try {
            birthDate = sdf.parse(birthday);
        } catch (ParseException e) {
        }
        Calendar now = Calendar.getInstance();
        if (now.before(birthDate)) {
            return 0;
        }
        int yearNow = now.get(Calendar.YEAR);
        int monthNow = now.get(Calendar.MONTH);
        int dayOfMonthNow = now.get(Calendar.DAY_OF_MONTH);

        now.setTime(birthDate);
        int yearBirth = now.get(Calendar.YEAR);
        int monthBirth = now.get(Calendar.MONTH);
        int dayOfMonthBirth = now.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirth;

        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }

        return age;
    }

    public String toBinaryString(){
        return TRANSPORT_HEAD_ACKED_LINK_REQUEST + getSequence() + TRANSPORT_HEAD_LENGTH_USERINFO_SETTING + TRANSPORT_HEAD_DEFAULT_CRC
                + TRANSPORT_PAYLOAD_APP_SUB_USERINFO_SETTING + ParserUtil.intTotoBinaryString(age, 8)
                + ParserUtil.intTotoBinaryString(height,8) +ParserUtil.intTotoBinaryString(weight,8);
    }
}
