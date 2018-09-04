package com.example.apidemo.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;


public class AccountModel extends AndroidViewModel{
    private MutableLiveData<AccountBean> mAccount = new MutableLiveData<>();

    public AccountModel(@NonNull Application application) {
        super(application);
    }


    public void setAccount(String name, String phone, String blog){
        mAccount.setValue(new AccountBean(name, phone, blog));
    }

    public MutableLiveData<AccountBean> getAccount(){
        return mAccount;
    }

    // 当MyActivity被销毁时，Framework会调用ViewModel的onCleared()
    @Override
    protected void onCleared() {
        Log.e("sjh0", "==========onCleared()==========");
        super.onCleared();
    }


}
