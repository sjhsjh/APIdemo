package com.example.apidemo.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

/**
 * <br>
 * Created by jinhui.shao on 2017/4/11.
 */
public class CustomRecycleView extends RecyclerView {
    private float downY;
    private int headerViewHeight;
    private final int READY_TO_RESET = 1; // 下拉一点距离即将还原的状态
    private final int READY_TO_REFRESH = 2;   // 下拉很多距离，松开就刷新的状态
    private final int REFRESHING = 3;   // 正在刷新的状态
    private int currentState;

    public CustomRecycleView(Context context) {
        super(context);
    }

    public CustomRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        headerViewHeight = getResources().getDimensionPixelSize(R.dimen.header_height);
       this.setPadding(0, -headerViewHeight, 0 , 0);
        // setTop(-headerViewHeight);
    }

    private void animateBack(int curentPaddingTop) {
        NLog.w("sjh0", "curentPaddingTop = " + curentPaddingTop + " headerViewHeight = " + headerViewHeight);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(curentPaddingTop, -headerViewHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                CustomRecycleView.this.setPadding(0, animatedValue, 0 , 0);
                // setTop(animatedValue);
                NLog.w("sjh0", "animatedValue = " + animatedValue);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                NLog.w("sjh0", "getPaddingTop = " + getPaddingTop());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int detaY = (int) (e.getRawY() - downY);
                int paddingTop = (int) (-headerViewHeight + detaY);
                Log.w("sjh0", "findFirstVisibleItemPosition = " + ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition());
                Log.e("sjh0", "paddingTop = " + paddingTop);
                if(((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition() == 0 && paddingTop > -headerViewHeight) {     // 下拉才处理
                    this.setPadding(0, paddingTop, 0 , 0);
                    // setTop(paddingTop);
                    if(paddingTop < 0) {
                        currentState = READY_TO_RESET;
                    }
                    else {
                        currentState = READY_TO_REFRESH;
                    }
                    // return true;    // 拦截TouchMove，不让listview处理该次move事件,会造成listview无法滑动
                }
                break;
            case MotionEvent.ACTION_UP:
                int detaY_2 = (int) (e.getRawY() - downY);
                int paddingTop_2 = (int) (-headerViewHeight + detaY_2);
                if(currentState == READY_TO_RESET) {
                    //  this.setPadding(0, -headerViewHeight, 0 , 0);
                    animateBack(paddingTop_2);
                }
                else if(currentState == READY_TO_REFRESH) {
                    // this.setPadding(0, 0, 0, 0);
                     animateBack(paddingTop_2);
                    currentState = REFRESHING;
                    // listener.onPullRefresh();
                }
                // return true;

            case MotionEvent.ACTION_CANCEL:
                break;
        }

//            141         case MotionEvent.ACTION_DOWN:
//                142             //获取按下时y坐标
//                143             downY = (int) ev.getRawY();
//                144             break;
//            145         case MotionEvent.ACTION_MOVE:
//                146
//                147             if(currentState==REFRESHING){
//                    148                 //如果当前处在滑动状态，则不做处理
//                    149                 break;
//                    150             }
//                151             //手指滑动偏移量
//                152             int deltaY = (int) (ev.getRawY() - downY);
//                153
//                154             //获取新的padding值
//                155             int paddingTop = -headerViewHeight + deltaY;
//                156             if(paddingTop>-headerViewHeight && getFirstVisiblePosition()==0){
//                    157                 //向下滑，且处于顶部，设置padding值，该方法实现了顶布局慢慢滑动显现
//                    158                 headerView.setPadding(0, paddingTop, 0, 0);
//                    159
//                    160                 if(paddingTop>=0 && currentState==PULL_REFRESH){
//                        161                     //从下拉刷新进入松开刷新状态
//                        162                     currentState = RELEASE_REFRESH;
//                        163                     //刷新头布局
//                        164                     refreshHeaderView();
//                        165                 }else if (paddingTop<0 && currentState==RELEASE_REFRESH) {
//                        166                     //进入下拉刷新状态
//                        167                     currentState = PULL_REFRESH;
//                        168                     refreshHeaderView();
//                        169                 }
//                    170
//                    171
//                    172                 return true;//拦截TouchMove，不让listview处理该次move事件,会造成listview无法滑动
//                    173             }
//                174
//                175
//                176             break;
//            177         case MotionEvent.ACTION_UP:
//                178             if(currentState==PULL_REFRESH){
//                    179                 //仍处于下拉刷新状态，未滑动一定距离，不加载数据，隐藏headView
//                    180                 headerView.setPadding(0, -headerViewHeight, 0, 0);
//                    181             }else if (currentState==RELEASE_REFRESH) {
//                    182                 //滑倒一定距离，显示无padding值得headcView
//                    183                 headerView.setPadding(0, 0, 0, 0);
//                    184                 //设置状态为刷新
//                    185                 currentState = REFRESHING;
//                    186
//                    187                 //刷新头部布局
//                    188                 refreshHeaderView();
//                    189
//                    190                 if(listener!=null){
//                        191                     //接口回调加载数据
//                        192                     listener.onPullRefresh();
//                        193                 }
//                    194             }
//                195             break;
//            196         }
//        197         return super.onTouchEvent(ev);

        return super.onTouchEvent(e);

    }




}