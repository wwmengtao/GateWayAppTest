package com.homecare.app.model;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.Serializable;

public class RegisterInfo implements Serializable {
    private String mobile;
    private String password;
    private String name;
    private boolean isMale;
    private String birthday;
    private int height;
    private int weight;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String toCloudMessage() {
        return name + "," + isMale + "," + birthday + "," + height + "," + weight + "," + mobile + "," + DigestUtils.md5Hex(password);
    }
}
