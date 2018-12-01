package com.example.apidemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <br> 必须实现Parcelable接口才能被AIDL接口文件
 * tmp5
 *  2017/2/22.
 */
public class Book implements Parcelable {
    private int number;

    public Book(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    protected Book(Parcel in) {
        number = in.readInt();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
    }


}