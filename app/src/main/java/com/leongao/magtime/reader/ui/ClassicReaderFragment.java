package com.leongao.magtime.reader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.leongao.magtime.app.MyApplication;
import com.leongao.magtime.home.model.MagCoverImg;
import com.leongao.magtime.reader.adapter.ClassicReaderAdapter;
import com.leongao.magtime.reader.bean.MagContent;
import com.leongao.magtime.reader.bean.ReaderPageResponse;
import com.leongao.magtime.utils.ConstantUtil;
import com.leongao.magtime.utils.DateUtil;
import com.leongao.magtime.utils.FileUtil;
import com.leongao.magtime.utils.NetworkUtil;
import com.leongao.magtime.viewmodel.ClassicReaderViewModel;
import com.leongao.magtime.databinding.FragmentClassicReaderBinding;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import timber.log.Timber;

/**
 *
 */
public class ClassicReaderFragment extends Fragment {

    private FragmentClassicReaderBinding binding;
    private ClassicReaderViewModel classicReaderViewModel;
    private WindowInsetsControllerCompat windowInsetsController;

    // 接收系统广播，阅读页底部显示电量、网络和时间
    private final BroadcastReceiver systemStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                setTimeState();
            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                setNetworkState();
            } else if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                // 电量百分比
                int level = intent.getIntExtra("level", 0);
                setBatteryState(level);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏沉浸模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController = WindowCompat.
                    getInsetsController(getActivity().getWindow(), getActivity().getWindow().getDecorView());
            // Configure the behavior of the hidden system bars.
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        } else {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentClassicReaderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initObserver();
        initReaderMenuTopView();
    }

    private void initViewModel() {
        classicReaderViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication()).create(ClassicReaderViewModel.class);
    }

    /**
     * 初始化ClassicReaderViewModel，并获取杂志内容列表
     *
     * 阅读页显示逻辑：
     * 1. 优先显示下载；（包含部分下载情形）
     * 2. 若没有下载，则由Glide处理：或显示缓存，或请求网络，或显示错误（包含无网络情形）
     * 3. 无论是否有下载，都先获得杂志内容url列表，为传入adapter的数据做准备
     *
     */
    private void initObserver() {
        int magId = ClassicReaderFragmentArgs.fromBundle(getArguments()).getMagId();
        List<String> localMagContentUrls = classicReaderViewModel.getLocalMagContentUrls(magId);
        Timber.d("local url : %s", localMagContentUrls);

        classicReaderViewModel.getMagContentUrls(magId).observe(getViewLifecycleOwner(), readerPageResponse -> {
            // 无网络且无缓存
            if (readerPageResponse.getError() != null) {
                Toast.makeText(getContext(), "获取阅读页内容失败", Toast.LENGTH_SHORT).show();
                // 无下载，显示错误页面并弹出提示
                if (localMagContentUrls == null) return;
                // 有下载，显示下载并弹出提示
                initClassicReader(localMagContentUrls);
                return;
            }
            // 有网络或有缓存
            List<MagContent> data = readerPageResponse.getData().getMagContentList();
            if (data == null || data.size() == 0) {
                Toast.makeText(getContext(), "获取阅读页内容失败", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> adapterDataList = classicReaderViewModel.getAdapterData(localMagContentUrls,
                    data.get(0).getMagContentImgUrlList());
            Timber.d("adapterDataList : %s", adapterDataList);
            initClassicReader(adapterDataList);
        });
    }

    /**
     * 初始化自带预加载功能的阅读器
     * @param magContentList：整本杂志内容的url列表
     */
    private void initClassicReader(List<String> magContentList) {
        ListPreloader.PreloadSizeProvider<String> sizeProvider = new FixedPreloadSizeProvider<>(ConstantUtil.imageWidthPixels, ConstantUtil.imageHeightPixels);
        ClassicReaderAdapter adapter = new ClassicReaderAdapter(getContext(), magContentList, listener);
        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<>(Glide.with(this), adapter, sizeProvider, 5);
        binding.classicReader.addOnScrollListener(preloader);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                RecyclerView.HORIZONTAL, false);
        binding.classicReader.setLayoutManager(layoutManager);
        binding.classicReader.setAdapter(adapter);

        // 仿ViewPager效果
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        // 避免报错：java.lang.IllegalStateException: An instance of OnFlingListener already set
        binding.classicReader.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(binding.classicReader);
    }

    /**
     * 初始化上悬浮菜单
     */
    private void initReaderMenuTopView() {
        binding.readerMenuTopView.getBackBtn().setOnClickListener(view -> {
            Navigation.findNavController(view).navigateUp();
        });
    }


    /**
     * 显示或隐藏上悬浮菜单
     */
    private OnPhotoTapListener listener = (view, x, y) -> {
        if (x > 0.29 && x < 0.71) {
            if (binding.maskedLayer.getVisibility() == View.GONE) {
                binding.maskedLayer.setVisibility(View.VISIBLE);
                binding.readerMenuTopView.runAnimationIn();
            } else {
                binding.maskedLayer.setVisibility(View.GONE);
                binding.readerMenuTopView.runAnimationOut();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        setSystemStateReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        MyApplication.getAppContext().unregisterReceiver(systemStateReceiver);
    }

    private void setSystemStateReceiver() {
        setTimeState();
        setNetworkState();
        setBatteryState(100);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        MyApplication.getAppContext().registerReceiver(systemStateReceiver, filter);
    }

    private void setTimeState() {
        binding.readerState.timeState.setText(DateUtil.date("HH:mm"));
    }

    private void setNetworkState() {
        binding.readerState.netState.setText(NetworkUtil.getCurrentNetworkType(MyApplication.getAppContext()));
    }

    private void setBatteryState(int level) {
        Matrix matrix = new Matrix();
        // TODO: 需要再好好研究一下
        matrix.setScale(1.4f * level / 100, 2.0f);
        binding.readerState.powerState.setImageMatrix(matrix);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        // 取消全屏沉浸模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars());
        }
    }
}