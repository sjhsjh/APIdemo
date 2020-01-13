package com.example.apidemo.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.example.apidemo.R;
import com.example.apidemo.utils.NLog;

/**
 * dependencyView大小or位置改变时，遍历CoordinatorLayout的child的behavior的layoutDependsOn，
 * 若返回true，则回调该child的behavior的onDependentViewChanged来改变child的属性。
 */
public class MyBehavior extends CoordinatorLayout.Behavior<TextView> {    // Behavior是抽象类但无抽象方法
    private int times = -1;

    // CoordinatorLayout内反射这个方法因此必须声明，否则报错NoSuchMethodException
    public MyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @return 返回child的布局是否依赖于dependency的布局；
     * 返回true时，则父view CoordinatorLayout会在dependency布局之后再布局child；
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        boolean result = dependency.getId() == R.id.button1;

        NLog.d("sjh9", "layoutDependsOn : result = " + result + " child = " + child + " dependency = " + dependency);
        return result;
        // return super.layoutDependsOn(parent, child, dependency);
    }

    /**
     * @return 当dependent view changes in size or position 时回调；OnPreDrawListener也触发一次；
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {
        NLog.i("sjh9", "onDependentViewChanged : " + " child = " + child + " dependency = " + dependency);
        if (times == 4) {
            times = 0;
        }
        child.setTranslationY(100 * ++times);
        return true;
        // return super.onDependentViewChanged(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull TextView child,
                                       @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);  // false
    }

}
