package com.example.apidemo.data;


public class AccountBean {
    private String name;
    private String phone;
    private String blog;

    public AccountBean(String name, String phone, String blog) {
        this.name = name;
        this.phone = phone;
        this.blog = blog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBlog() {
        return blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }
}
