package com.example.apidemo.activity;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Placeholder;
import android.view.View;
import android.widget.Button;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.IGSON;
import com.example.apidemo.Son;
import com.example.apidemo.utils.NLog;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by Administrator on 2017/4/18 0018.
 */
public class GsonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.constraint_layout);

        IGSON ig = new Son();
        Gson gson = new Gson();
        String str = gson.toJson(ig);
        NLog.i("sjh0", "toJson str = " + str);

        try {
            Gson gson2 = new Gson();
            IGSON ig2 = gson2.fromJson(str, IGSON.class);   // 包含(接口.class).newInstance。  Son才可以
            // Book ig2 = gson2.fromJson(str, Book.class);  将A对象转json再还原成B对象竟然没有报错，但B的内容应该是错的
            NLog.i("sjh0", "fromJson ig2 = " + ig2);
            // ig2.add();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            NLog.e("sjh0", "Exception = " + e);
            e.printStackTrace();
            /*   (接口.class).newInstance会怎么样？
            Caused by: java.lang.RuntimeException: Unable to invoke no-args constructor for interface com.example.apidemo.view.IGSON.
            Register an InstanceCreator with Gson for this type may fix this problem.
            at com.google.gson.internal.ConstructorConstructor$14.construct(ConstructorConstructor.java:226)*/
        }


        final Placeholder placeholder = findViewById(R.id.placeholder);
        final Button btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placeholder.getContent() == null) {
                    placeholder.setContentId(R.id.btn3);
                } else {
                    placeholder.setContentId(-1);
                    ((ConstraintLayout) findViewById(R.id.constraintLayout)).requestLayout();   // 让之前的mContentView回到原来的位置！
                }
            }
        });


    }

}
