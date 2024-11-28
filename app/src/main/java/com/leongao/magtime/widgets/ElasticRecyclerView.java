package com.leongao.magtime.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import timber.log.Timber;

public class ElasticRecyclerView extends RecyclerView {

    private static final float OVER_SCROLL_FACTOR = 0.5f;
    private static final int TOUCH_SLOP = 0;
    private float mLastY;
    private boolean mIsDragged;
    public ElasticRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public ElasticRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ElasticRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 启用底部弹性效果
    private void init() {
        Timber.d("init");
        setOverScrollMode(OVER_SCROLL_ALWAYS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = e.getRawY();
                mIsDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = e.getRawY();
                float deltaY = currentY - mLastY;
                Timber.d("deltaY: %s", deltaY);
                // canScrollVertically(-1): scroll to top
                // canScrollVertically(1): scroll to bottom
                if (deltaY > TOUCH_SLOP && canScrollVertically(1)) {
                    mIsDragged = true;
                    mLastY = currentY;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsDragged = false;
                break;
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        return mIsDragged || super.onInterceptTouchEvent(e);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        Timber.d("clampedY: %s", clampedY);
        if (clampedY) {
            Timber.d("scrollY: %s", scrollY);
            Timber.d("offset: %s", computeVerticalScrollOffset());

            float deltaY = scrollY - computeVerticalScrollOffset();
            if (deltaY > 0) {
                int newScrollY = Math.round(deltaY * OVER_SCROLL_FACTOR);

                if (newScrollY != 0) {
                    scrollBy(0, newScrollY);
                }
            }
        }
    }
}
