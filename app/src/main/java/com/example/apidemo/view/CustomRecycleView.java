package com.example.apidemo.view;

import android.animation.ValueAnimator;
import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.example.apidemo.R;
import com.example.apidemo.adapter.RecycleViewAdapter;
import com.example.apidemo.utils.NLog;

/**
 * <br>
 * Created by jinhui.shao on 2017/4/11.
 */
public class CustomRecycleView extends RecyclerView {
    private int mLastTouchY;
    private int headerViewHeight;
    private final int READY_TO_RESET = 1; // 下拉一点距离即将还原的状态（包含最初状态）
    private final int READY_TO_REFRESH = 2;   // 下拉很多距离，松开就刷新的状态
    private final int REFRESHING = 3;   // 正在刷新的状态
    private int currentState;
    private RecycleViewAdapter.HeaderViewHolder mHeaderViewHolder;
    private LinearLayoutManager mLayoutManager;

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
        setY(-headerViewHeight);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if(layout instanceof LinearLayoutManager){
            mLayoutManager = (LinearLayoutManager) layout;
        }
    }

    public void animateBack() {
      //  animateBack(-headerViewHeight);

//        onStatusChange(READY_TO_RESET);
//        // scrollBy(0, 0);
//        // smoothScrollToPosition(2);   // 滑动到尚未可见的item的顶部
//        // scrollToPosition(0);
//        setY(-headerViewHeight);
//        mLayoutManager.scrollToPositionWithOffset(0, 0); // position的顶部
//        // ((LinearLayoutManager)getLayoutManager()).setStackFromEnd(true);

        if(mLayoutManager.findFirstVisibleItemPosition() == 0){
            ValueAnimator valueAnimator = ValueAnimator.ofInt(0, getChildAt(0).getTop() - headerViewHeight);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    CustomRecycleView.this.setY(animatedValue);
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    NLog.w("sjh0", "2222" );
                    onStatusChange(READY_TO_RESET);
                    setY(-headerViewHeight);
                    mLayoutManager.scrollToPositionWithOffset(0, 0);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            // valueAnimator.setDuration(10);
            valueAnimator.start();
        }
        else {
            onStatusChange(READY_TO_RESET);
            setY(-headerViewHeight);
            mLayoutManager.scrollToPositionWithOffset(0, 0);
        }

    }

    private void animateBack(int targetY) {
        NLog.e("sjh0", "animateBack getY() = " + getY() + " headerViewHeight = " + headerViewHeight);
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) getY(), targetY);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                CustomRecycleView.this.setY(animatedValue);
            }
        });
//        valueAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                NLog.w("sjh0", "" );
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });
        valueAnimator.start();
    }

    // y - downY这种方式有弊端：若move的过程中view的位置发生改变，View的初始Y的downY都要重新赋值才行。
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        if(e.getAction() != MotionEvent.ACTION_DOWN && currentState == REFRESHING) {    // down事件都要记下mLastTouchY！
            mLastTouchY = y;
            return super.onTouchEvent(e);
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                NLog.d("sjh0", "y = "  + y + " mLastTouchY = " + mLastTouchY);
                NLog.d("sjh0", "canScrollVertically = "  + canScrollVertically(-1)
                        // 即使第一个可见item在屏幕外看不到，但是findFirstVisibleItemPosition是指在RecycleView中第一个可见的item！
                        + " findFirstVisibleItemPosition = " + ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition());
                        // + (((TextView)((RelativeLayout)getChildAt(0)).getChildAt(0)).getText())
                int detaY = y - mLastTouchY;
                int currentY = (int) (getY() + detaY);
                // if(getFirstVisiblePosition() == 0 && getChildAt(0).getTop == 0 && getTopScrollY() >= -300 ){ // ListView的判断
                if (!canScrollVertically(-1) && currentY >= -headerViewHeight) {
                    NLog.w("sjh0", "currentY = " + currentY);
                    setY(Math.max(-headerViewHeight, currentY));
                    if (currentY < 0) {
                        onStatusChange(READY_TO_RESET);
                    } else {
                        onStatusChange(READY_TO_REFRESH);
                    }
                    mLastTouchY = y;
                    return true;    // 拦截Touch move，不让listview处理该次move事件,会造成listview无法滑动
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(currentState == READY_TO_RESET) {
                    animateBack();
                }
                else if(currentState == READY_TO_REFRESH) {
                    animateBack(0);
                    onStatusChange(REFRESHING);
                    if(myRecyclerViewListener != null){
                        myRecyclerViewListener.onRefresh();
                    }
                }
                return true;

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
        mLastTouchY = y;
        return super.onTouchEvent(e);
    }

    public void onStatusChange(int newStatus){
        if(newStatus == currentState){
            return;
        }
        currentState = newStatus;
        if(mHeaderViewHolder == null){
            mHeaderViewHolder = (RecycleViewAdapter.HeaderViewHolder) findViewHolderForAdapterPosition(0);
        }
        switch (newStatus){
            case READY_TO_RESET :
                mHeaderViewHolder.mHeaderTips.setText("下拉刷新");
                break;
            case READY_TO_REFRESH :
                mHeaderViewHolder.mHeaderTips.setText("松开刷新");
                break;
            case REFRESHING :
                mHeaderViewHolder.mHeaderTips.setText("正在刷新...");
                // getAdapter().notifyItemChanged(5);  // 触发onCreateViewHolder和onBindViewHolder
                break;
        }
    }

    private MyRecyclerViewListener myRecyclerViewListener;

    public void setRecyclerViewListener(MyRecyclerViewListener listener){
        myRecyclerViewListener = listener;
    }

    public interface MyRecyclerViewListener {
        void onRefresh();

        void onLoadMore();
    }


}