package com.rtsoftware.order.model.data;

public class User {
    /**
     * Thành : thanhnd@order.com
     * Vân Anh: anhmtv@order.com
     * Hoàng: hoangbd@order.com
     * Vân: vanut@order.com
     * Duyên: duyenntp@order.com
     * Dung: dungbt@order.com
     *
     *
     * Pass: 88888888
     */

    public static final String ADMIN= "admin";
    public static final String ORDER= "order";
    public static final String KITCHEN= "kitchen";
    public static final String CASHIER= "cashier";
//    name: users
//    folder: userId

    String userId;
    String userName;
    String passWord;
    String email;
    String fullName;
    //1997/12/01
    String dateOfBirth;

    /*
    Phân quyền người dùng
    ADMIN: full permission
    ORDER: chạy bàn order
    KITCHEN: nhân viên bếp
    CASHIER: thu ngân
     */
    String permission;


    public User(){}

    public User(String userId, String userName, String passWord, String email, String fullName, String dateOfBirth, String permission) {
        this.userId = userId;
        this.userName = userName;
        this.passWord = passWord;
        this.email = email;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.permission = permission;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
