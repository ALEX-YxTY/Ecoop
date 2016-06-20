package com.milai.ecoop.bean;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String open_id;
    private String email;
    private String username;
    private String realname;
    private String alipay_id;
    private String password;
    private String avatar;
    private String gender;
    private String newbie;
    public  String mobile;
    private String qq;
    private String money;
    private String score;
    private String zipcode;
    private String address;
    private String city_id;
    private String emailable;
    private String enable;
    private String manager;
    private String secret;
    private String recode;
    private String sns;
    private String ip;
    private String login_time;
    private String create_time;
    private String mobilecode;
    private String unionid;
    private String pay_time;

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getAlipay_id() {
        return alipay_id;
    }

    public void setAlipay_id(String alipay_id) {
        this.alipay_id = alipay_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNewbie() {
        return newbie;
    }

    public void setNewbie(String newbie) {
        this.newbie = newbie;
    }

    public String getMobile() {
        if (mobile != null) {
            return mobile;
        }else{
            return "未绑定手机";
        }
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getEmailable() {
        return emailable;
    }

    public void setEmailable(String emailable) {
        this.emailable = emailable;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getRecode() {
        return recode;
    }

    public void setRecode(String recode) {
        this.recode = recode;
    }

    public String getSns() {
        return sns;
    }

    public void setSns(String sns) {
        this.sns = sns;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLogin_time() {
        return login_time;
    }

    public void setLogin_time(String login_time) {
        this.login_time = login_time;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getMobilecode() {
        return mobilecode;
    }

    public void setMobilecode(String mobilecode) {
        this.mobilecode = mobilecode;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", open_id='" + open_id + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", realname='" + realname + '\'' +
                ", alipay_id='" + alipay_id + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", newbie='" + newbie + '\'' +
                ", mobile='" + mobile + '\'' +
                ", qq='" + qq + '\'' +
                ", money='" + money + '\'' +
                ", score='" + score + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", address='" + address + '\'' +
                ", city_id='" + city_id + '\'' +
                ", emailable='" + emailable + '\'' +
                ", enable='" + enable + '\'' +
                ", manager='" + manager + '\'' +
                ", secret='" + secret + '\'' +
                ", recode='" + recode + '\'' +
                ", sns='" + sns + '\'' +
                ", ip='" + ip + '\'' +
                ", login_time='" + login_time + '\'' +
                ", create_time='" + create_time + '\'' +
                ", mobilecode='" + mobilecode + '\'' +
                ", unionid='" + unionid + '\'' +
                ", pay_time='" + pay_time + '\'' +
                '}';
    }
}
