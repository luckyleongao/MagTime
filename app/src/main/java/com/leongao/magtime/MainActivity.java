package com.leongao.magtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private NavHostFragment hostFragment;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 状态栏透明，且字体设置未黑色
        // TODO: 没有达到预期效果，后续需要改进
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 表示我們的 UI 是 LIGHT 的 style，icon 就會呈現深色系。

        setContentView(R.layout.activity_main);

        hostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        navController = hostFragment.getNavController();
        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.classicReaderFragment ||
                    navDestination.getId() == R.id.detailFragment) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // 控制底部导航栏跳转
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            int currentId = navController.getCurrentDestination().getId();
            if (id == R.id.home_dest && currentId != R.id.home_dest) {
                navController.navigate(R.id.home_dest);
            } else if (id == R.id.search_dest && currentId != R.id.search_dest) {
                navController.navigate(R.id.search_dest);
            } else if (id == R.id.shelf_dest && currentId != R.id.shelf_dest) {
                navController.navigate(R.id.shelf_dest);
            } else if (id == R.id.setting_dest && currentId != R.id.setting_dest) {
                navController.navigate(R.id.setting_dest);
            }
            return true;
        });

    }


    /**
     * 全局范围判断，是否隐藏软键盘
     * 目前适用于搜索页面搜索框
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
                // 隐藏焦点
                v.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，
     * 来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if ((v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}