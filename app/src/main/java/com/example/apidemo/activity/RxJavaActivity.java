package com.example.apidemo.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.example.apidemo.BaseActivity;
import com.example.apidemo.utils.NLog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/12/6.
 */
public class RxJavaActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //创建一个上游 Observable，上游就是被观察者：
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                NLog.d("sjh0", "subscribe() ");
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        });
        Observable.zip
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
            public void onComplete() {
                NLog.d("sjh0", "complete");
            }
        };

        //建立连接并开始发送事件
        observable.subscribe(observer);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                NLog.d("sjh0", "subscribe() " + Thread.currentThread());
                e.onNext("onNext-string");
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())    // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber（Observer）的回调发生在主线程
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
                        NLog.i"sjh0", "action run().");
                    }
                }, new Consumer<Disposable>() {    // onSubscribe
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        NLog.i("sjh0", "onSubscribe() ");
                    }
                });
    }
    }

}
