package com.example.apidemo.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.apidemo.R;
import com.example.apidemo.data.AccountBean;
import com.example.apidemo.data.AccountModel;


public class BottomFragment extends Fragment {
    private AccountModel mModel;
    private TextView mText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_viewmodel, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mText = (TextView) view.findViewById(R.id.fragment_text_view);
        ((TextView) view.findViewById(R.id.frag)).setText("Fragment2");


        mModel = ViewModelProviders.of(getActivity()).get(AccountModel.class);

        view.findViewById(R.id.fragment_set_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mModel.getAccount().postValue(new AccountBean("Fragment2", "122*****222", "Fragment2 post数据"));
            }
        });

        mModel.getAccount().observe(this, new Observer<AccountBean>() {
            @Override
            public void onChanged(@Nullable AccountBean accountBean) {
                mText.setText("监听到的新数据：\n昵称:" + accountBean.getName() + "\n手机:" + accountBean.getPhone() + "\n博客:" + accountBean.getBlog());
            }
        });


    }
}
