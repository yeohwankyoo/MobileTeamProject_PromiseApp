package com.example.promise;

/**
 * Created by Yeohwankyoo on 2016-06-13.
 */
public class DataVo {
    private String img;
    private String name;
    private int age;
    private String email;
    private String userID;
    private String phoneNum;


    public String getName() {
        return name;
    }
    public void setName(String name) {this.name = name;}

    public void setEmail(String email) {
        this.email = email;
    }
    public void setUserID(String userID){this.userID = userID;}
    public String getphoneNum() {return phoneNum;}
    public void setPhoneNum(String phoneNum) {this.phoneNum = phoneNum;}

    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {this.age = age;}
    public String getEmail() {return email;}

    public String getUserID(){return userID;}
}
