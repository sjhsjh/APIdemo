package com.example.apidemo.data;

import com.example.compiler.AutoBindClass;

@AutoBindClass(value = "accountbean")
public class AccountBean implements IBaseObj {
    private String name;
    private String phone;
    private String blog;

    public AccountBean() {
    }

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

    @Override
    public int getID() {
        return 6;
    }

    @Override
    public String getNick() {
        return "nick_accountbean";
    }
}
