package com.example.apidemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.data.Student;
import com.example.apidemo.utils.XmlUtils;
import java.util.List;


public class XmlParseActivity extends BaseActivity {
    private XmlUtils xmlUtils;
    private List<Student> students;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);
        textView = (TextView) findViewById(R.id.textView1);
        xmlUtils = new XmlUtils();


        ((Button)findViewById(R.id.button1)).setText("DOM 解析");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    students = xmlUtils.dom2xml(getResources().getAssets().open("student.xml"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textView.setText("DOM: " + students.toString());
            }
        });

        ((Button)findViewById(R.id.button2)).setText("SAX 解析");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    students = xmlUtils.sax2xml(getResources().getAssets().open("student.xml"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textView.setText("SAX: " + students.toString());
            }
        });
        ((Button)findViewById(R.id.button3)).setText("PULL 解析");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    students = xmlUtils.pull2xml(getResources().getAssets().open("student.xml"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textView.setText("PULL: " + students.toString());
            }
        });

    }



}
