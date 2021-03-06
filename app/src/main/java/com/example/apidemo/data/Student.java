package com.example.apidemo.data;

import com.example.compiler.AutoBindClass;
import com.example.compiler.AutoBindField;
import com.example.compiler.AutoBindMethod;

@AutoBindClass(value = "student")
public class Student implements IBaseObj {

    @AutoBindField(value = "nameX")
    private String name;
    private String sex;
    private String nickName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @AutoBindMethod(value = "getsex")
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", nickname='" + nickName + '\'' +
                '}';
    }

    @Override
    public int getID() {
        return 5;
    }

    @Override
    public String getNick() {
        return "nick_student";
    }
}
