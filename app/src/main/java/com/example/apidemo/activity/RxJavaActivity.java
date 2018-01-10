package com.example.apidemo.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.apidemo.BaseActivity;
import com.example.apidemo.R;
import com.example.apidemo.newwork.IRequest;
import com.example.apidemo.utils.NLog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/12/6.
 */
public class RxJavaActivity extends BaseActivity{
    private static final String ACCUWEATHER_APIKEY = "af7408e9f4d34fa6a411dd92028d4630";
    private static final String BaseUrl = "http://api.accuweather.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);

        ((Button)findViewById(R.id.button1)).setText("RxJavaSample1");
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RxJavaSample1();
            }
        });

        ((Button)findViewById(R.id.button2)).setText("RxJavaSample2");
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RxJavaSample2();
            }
        });

        ((Button)findViewById(R.id.button3)).setText("RxJavaMap");
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RxJavaMap();
            }
        });

        ((Button)findViewById(R.id.button4)).setText("Retrofit request");
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RetrofitRequest();
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager)RxJavaActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                // inputManager.showSoftInput(((EditText)findViewById(R.id.edittext)), 0);
                inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);    // 不延时直接弹出软键盘则会出现自动弹起输入法后又收回去的现象。

                // 由于刚跳到一个新的界面，界面未加载完全而无法弹出软键盘。因此此时赋予焦点并没有用。
//                ((EditText)findViewById(R.id.edittext)).setFocusable(true);   // 这3句话是获取焦点的所需语句
//                ((EditText)findViewById(R.id.edittext)).setFocusableInTouchMode(true);
//                ((EditText)findViewById(R.id.edittext)).requestFocus();
                // 隐藏输入法
                // inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                // inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }, 500);

        ((EditText)findViewById(R.id.edittext)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    NLog.w("sjh7", "actionId yy " + actionId);
                }
                return false;    // 返回false会收起输入法
            }
        });

    }

    private void RxJavaSample1(){
        //创建一个上游 Observable，上游就是被观察者：
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                NLog.d("sjh0", "subscribe() ");
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onComplete();
                emitter.onNext(3);  // 能发送但不再被接收
            }
        });

        //创建一个下游 Observer
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {    // subscribe设置监听后api内部先回调这里的observer.onSubscribe();再调用Observable的subscribe。
                NLog.d("sjh0", "onSubscribe");
            }

            @Override
            public void onNext(Integer value) {
                NLog.d("sjh0", "onNext " + value);
            }

            @Override
            public void onError(Throwable e) {
                NLog.d("sjh0", "error");
            }

            @Override
            public void onComplete() {  // onComplete()之后会触发dispose();dispose之后onNext等所有回调不能再被触发。
                NLog.d("sjh0", "complete");
            }
        };

        //注册监听并开始发送事件
        observable.subscribe(observer);
    }


    private void RxJavaSample2(){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                NLog.d("sjh0", "subscribe() " + Thread.currentThread());
                // TUDO：此处由subscribeOn指定为IO线程，因此可以进行网络请求、读取数据库等耗时操作。

                e.onNext("onNext-string");  // 回调到被observeOn指定为主线程的Observer中！！！
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())    // 指定 subscribe() 发生在 IO 线程，以第一次指定的线程为准。
                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber（Observer）的回调发生在主线程，以最新指定的线程为准。
                .subscribe(new Consumer<String>() {    // onNext
                    @Override
                    public void accept(@NonNull String text) throws Exception {
                        NLog.i("sjh0", Thread.currentThread() + " accept-text = " + text);
                    }
                }, new Consumer<Throwable>() {    // onError
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        NLog.i("sjh0", Thread.currentThread() + " throwable = " + throwable);
                    }
                }, new Action() {    // onComplete
                    @Override
                    public void run() throws Exception {
                        NLog.i("sjh0", "action run().");
                    }
                }, new Consumer<Disposable>() {    // onSubscribe
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        NLog.i("sjh0", "onSubscribe() ");
                    }
                });
    }

    /**
     * RxJava map和flatMap的使用方法.
     */
    private void RxJavaMap(){
        Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
                e.onComplete();
            }
        })
        // map()用于值or类型的转换！由类ObservableMap中的源码可知，map的作用实际是执行apply方法转换数据之后再回调onNext():
        // actual.onNext(function.apply(t));
//        .map(new Function<Integer, String>() {
//                @Override
//                public String apply(Integer integer) throws Exception {
//                    return "apply " + integer;
//                }
//        })
//        .subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                NLog.d("sjh0", "map(). onNext-s = " + s);
//            }
//
//        });
        // flatMap的作用实际上是遍历取出集合类型然后调用onNext().
        // concatMap与flatMap的区别仅仅是concatMap是严格按照上游发送的顺序来发送的
        .flatMap(new Function<Integer, ObservableSource<Double>>() {
            @Override
            public ObservableSource<Double> apply(Integer integer) throws Exception {
                final List list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add(integer + 10.0);
                }
                return Observable.fromIterable(list).delay(10, TimeUnit.MILLISECONDS);
            }
        })
        .subscribe(new Consumer<Double>() {
            @Override
            public void accept(Double aDouble) throws Exception {
                NLog.i("sjh0", "flatMap(). onNext-double = " + aDouble);
            }
        });

    }


    private void RetrofitRequest(){
        IRequest iRequest = produceRetrofit().create(IRequest.class);
        final Call<ResponseBody> call = iRequest.getWeather("beijing", ACCUWEATHER_APIKEY);
        // 方法①
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    NLog.w("sjh0", "onResponse = " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });

        // 方法②
//                Thread thread = new Thread(){
//                    @Override
//                    public void run() {
//                        // IOException: java.net.UnknownHostException: Unable to resolve host "api.accuweather.com": No address associated with hostname（断网）
//                        retrofit2.Response<ResponseBody> response = null;   // 注意这不是okhttp3的Response！！
//                        try {
//                            response = call.execute();
//                            if(response.isSuccessful()){
//                                NLog.w("sjh0", response.body().string());
//                                // locationEntity = response.body();   // 混淆了bean的话locationEntity也不为null，只是内容全为null
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//                thread.start();

    }


    public class RequestIntercepteor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request oldRequest = chain.request();
            Request newRequest = oldRequest.newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Connection", "Keep-alive")
                    .build();
            return chain.proceed(newRequest);
        }
    }

    private OkHttpClient produceHttpClient(){
        // OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.addInterceptor(new RequestIntercepteor());
        return builder.build();
    }

    private Retrofit produceRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .client(produceHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit;
    }


}
