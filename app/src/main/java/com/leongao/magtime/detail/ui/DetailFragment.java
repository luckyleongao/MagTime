package com.leongao.magtime.detail.ui;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.OneShotPreDrawListener;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.Explode;

import androidx.transition.Fade;
import androidx.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.leongao.magtime.db.entity.Download;
import com.leongao.magtime.db.entity.Favorite;
import com.leongao.magtime.detail.model.DetailPageResponse;
import com.leongao.magtime.download.DownloadButtonProgress;
import com.leongao.magtime.download.DownloadButtonStatus;
import com.leongao.magtime.download.SampleTask;
import com.leongao.magtime.home.adapter.HomeBlockRecyclerViewAdapter;
import com.leongao.magtime.home.adapter.HomeRecyclerViewAdapter;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.utils.ConstantUtil;
import com.leongao.magtime.utils.NetworkUtil;
import com.leongao.magtime.viewmodel.DetailViewModel;
import com.leongao.magtime.viewmodel.DownloadViewModel;
import com.leongao.magtime.viewmodel.FavoriteViewModel;
import com.leongao.magtime.R;
import com.leongao.magtime.databinding.FragmentDetailBinding;
import com.leongao.magtime.databinding.HomeRecyclerviewItemBinding;

import java.util.List;

import timber.log.Timber;


public class DetailFragment extends Fragment implements HomeRecyclerViewAdapter.OnItemClickListener {

    private FragmentDetailBinding binding;
    private HomeRecyclerviewItemBinding stubBinding;
    private HomeBlockRecyclerViewAdapter adapter;
    private DetailFragmentArgs args;
//    private Magazine magazine;
    private String transitionName;
    private int magId;
    private String magName;
    private String magPubDate;
    private String magCoverImgUrl;
    private View inflatedView;
    private FavoriteViewModel favoriteViewModel;
    private DownloadViewModel downloadViewModel;
    private DetailViewModel detailViewModel;
    private Favorite favorite;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.default_transition));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(LayoutInflater.from(getContext()), container, false);
        // combine view binding with stubView
        binding.sameMagBlock.setOnInflateListener((viewStub, view) -> stubBinding = HomeRecyclerviewItemBinding.bind(view));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDetailFragment();
        getContext().registerReceiver(downloadViewModel.onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void initDetailFragment() {
        initArgs();
        initView();
        initViewModel();
        initObserver();
        initFavoriteState();
        initDownloadState();
        initFavoriteMagazine();
        initOnClickListener();
    }

    private void initArgs() {
//        Bundle b = getArguments();
//        if (null == b) return;
//        transitionName = b.getString("transitionName");
//        magazine = b.getParcelable("magazine");
        args = DetailFragmentArgs.fromBundle(getArguments());
        magId = args.getMagId();
        magName = args.getMagName();
        magPubDate = args.getMagPubDate();
        magCoverImgUrl = args.getMagCoverImgUrl();
        transitionName = args.getTransitionName();
    }

    private void initView() {
        binding.magName.setText(magName);
        binding.magPubDate.setText(magPubDate);
        // set shared transitionName
        Timber.d("transitionName: %s", transitionName);
        binding.magCoverImg.setTransitionName(transitionName);
        Glide.with(getContext())
                .load(magCoverImgUrl)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.magCoverImg);
    }

    private void initViewModel() {
        favoriteViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication()).create(FavoriteViewModel.class);
        DownloadViewModel.Factory factory = new DownloadViewModel.Factory(getActivity().getApplication(), args);
        downloadViewModel = new ViewModelProvider(this, factory).get(DownloadViewModel.class);
//        downloadViewModel = ViewModelProvider.AndroidViewModelFactory
//                .getInstance(getActivity().getApplication()).create(DownloadViewModel.class);
        detailViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication()).create(DetailViewModel.class);
    }

    private void initFavoriteMagazine() {
        favorite = new Favorite();
        favorite.setMagId(magId);
        favorite.setMagName(magName);
        favorite.setMagPubDate(magPubDate);
        favorite.setMagCoverImgUrl(magCoverImgUrl);
    }

    /**
     * 初始化杂志收藏状态
     */
    private void initFavoriteState() {
        binding.magLike.setChecked(favoriteViewModel.findById(magId) != null);
    }

    /**
     * 初始化杂志下载状态
     */
    private void initDownloadState() {
        Timber.d("magId: %s", magId);
        Download download = downloadViewModel.findById(magId);
        // 未下载
        if (null == download) {
            binding.magDownload.setIdle();
            return;
        }
        Timber.d("download.getStatus(): %s", download.getStatus());
        // 已下载
        if (download.getStatus() == ConstantUtil.DOWNLOAD_STATUS.COMPLETE) {
            binding.magDownload.setFinish();
            return;
        }
        // 暂停，下载中，意外退出
        Timber.d("getDownloadedNum: %s", download.getDownloadedNum());
        Timber.d("getTotalNum: %s", download.getTotalNum());
        binding.magDownload.setResume();
//        binding.magDownload.setDeterminate();
        binding.magDownload.setCurrentProgress(
                download.getDownloadedNum() * 100 / download.getTotalNum());
    }

    private void initOnClickListener() {
        binding.detailBack.setOnClickListener(view -> {
            Navigation.findNavController(view).navigateUp();
        });
        binding.magRead.setOnClickListener(view -> {
            NavDirections action = DetailFragmentDirections
                    .actionDetailFragmentToClassicReaderFragment(magId);
            Navigation.findNavController(view).navigate(action);
        });

        binding.magLike.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                favoriteViewModel.insert(favorite);
            } else {
                favoriteViewModel.deleteById(magId);
                Timber.d("favorite mag id: %s", favorite.getMagId());
            }
        });

        binding.magDownload.addOnClickListener(new DownloadButtonProgress.OnClickListener() {
            @Override
            public void onIdleButtonClick(View view) {
                // TODO: 动态获取读取外部存储的权限
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    binding.magDownload.setIndeterminate();
                    downloadViewModel.downloadMagazine(magId);
                } else {
                    Toast.makeText(getContext(), "Network Unavailable!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResumeButtonClick(View view) {
//                binding.magDownload.setDeterminate();
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    downloadViewModel.isPaused = false;
                    downloadViewModel.downloadMagazine(magId);
                    Toast.makeText(getContext(), "on resume click", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Network Unavailable!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPauseButtonClick(View view) {
//                binding.magDownload.setResume();
                if (NetworkUtil.isNetworkAvailable(getContext())) {
                    downloadViewModel.isPaused = true;
                    Toast.makeText(getContext(), "on pause click", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Network Unavailable!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFinishButtonClick(View view) {

            }
        });

        binding.magInfo.setOnClickListener(view -> {
            showBottomSheetDialog();
        });

    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.fragment_detail_bottom_sheet);
        bottomSheetDialog.setCancelable(true); // 是否支持拖拽关闭
        bottomSheetDialog.setCanceledOnTouchOutside(true); // 是否支持点击视图外部关闭弹窗
        bottomSheetDialog.show();
    }

    private void initObserver() {
        detailViewModel.getSimilarMagazines(magName, magId).observe(getViewLifecycleOwner(), resultState -> {
            // TODO: 处理错误， 显示错误页面或弹出提示
            if (resultState.getError() != null) {
                Toast.makeText(getContext(), "no similar magazines", Toast.LENGTH_SHORT).show();
                return;
            }

            initSameMagBlock(resultState.getData().getData());
        });
//        favoriteViewModel.findById(magId).observe(getViewLifecycleOwner(), favorite -> {
//            initFavoriteState(favorite);
//        });
//        downloadViewModel.findById(magId).observe(getViewLifecycleOwner(), download -> {
//            initDownloadState(download);
//        });
        // 更新下载按钮状态
        downloadViewModel.btnStatus.observe(getViewLifecycleOwner(), downloadButtonStatus -> {
            if (downloadButtonStatus.getDownloadStatus() == ConstantUtil.DOWNLOAD_STATUS.COMPLETE) {
                binding.magDownload.setFinish();
            } else if (downloadButtonStatus.getDownloadStatus() == ConstantUtil.DOWNLOAD_STATUS.PAUSE) {
                binding.magDownload.setResume();
                binding.magDownload.setCurrentProgress(downloadButtonStatus.getProgress());
            } else if (downloadButtonStatus.getDownloadStatus() == ConstantUtil.DOWNLOAD_STATUS.IN_PROGRESS) {
                binding.magDownload.setDeterminate();
                Timber.d("progress: %s", downloadButtonStatus.getProgress());
                binding.magDownload.setCurrentProgress(downloadButtonStatus.getProgress());
            }
        });
    }

    private void initSameMagBlock(List<Magazine> dataList) {
        if (inflatedView == null) {
            inflatedView = binding.sameMagBlock.inflate();
            stubBinding.blockName.setText(R.string.detail_sameMag);

            adapter = new HomeBlockRecyclerViewAdapter(getContext(), dataList, this);
            adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.ALLOW);
            stubBinding.blockMagazine.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            stubBinding.blockMagazine.setHasFixedSize(true);
            stubBinding.blockMagazine.setAdapter(adapter);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        adapter = null;
        getContext().unregisterReceiver(downloadViewModel.onDownloadComplete);
    }

    @Override
    public void onItemClick(View view, Magazine magazine) {

    }
}