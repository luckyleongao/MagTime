package com.leongao.magtime.home.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.OneShotPreDrawListener;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.Explode;

import androidx.transition.Fade;
import androidx.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.leongao.magtime.R;
import com.leongao.magtime.home.adapter.HomeRecyclerViewAdapter;
import com.leongao.magtime.home.model.Block;
import com.leongao.magtime.home.model.BlockItem;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.utils.RecyclerViewUtils;
import com.leongao.magtime.viewmodel.HomeViewModel;
import com.leongao.magtime.databinding.FragmentHomeBinding;

import java.util.List;
import java.util.Timer;

import timber.log.Timber;


public class HomeFragment extends Fragment implements HomeRecyclerViewAdapter.OnGetAllMagazines,
        HomeRecyclerViewAdapter.OnItemClickListener {

    private FragmentHomeBinding homeBinding;
    private HomeViewModel homeViewModel;
    private HomeRecyclerViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.default_transition));
//        setExitTransition(new Fade());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return homeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 推迟共享元素转换动画，等待RecyclerView数据加载完成
        postponeEnterTransition();

        homeBinding.homeSwipeRefresh.setColorSchemeColors(Color.rgb(47, 223, 189));
        homeBinding.homeSwipeRefresh.setOnRefreshListener(() -> {
            homeViewModel.getHomePage();
        });

        initViewModel();
        initObserver();
        homeViewModel.getHomePage();
    }

    private void initObserver() {
//        homeViewModel.getHomePage().observe(getViewLifecycleOwner(), homePageResponse -> {
//            homeBinding.homeSwipeRefresh.setRefreshing(false);
//            showHomePage(homePageResponse.getData());
//        });

        homeViewModel.homeResponse.observe(getViewLifecycleOwner(), homePageResponse -> {
            homeBinding.homeSwipeRefresh.setRefreshing(false);
            showHomePage(homePageResponse.getData());
        });
        homeViewModel.error.observe(getViewLifecycleOwner(), s -> {
            homeBinding.homeSwipeRefresh.setRefreshing(false);
        });
    }

    private void initViewModel() {
        homeViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication()).create(HomeViewModel.class);
    }

    private void showHomePage(List<Block> dataList) {
        adapter = new HomeRecyclerViewAdapter(getContext(), this,
                dataList.get(0).getBlockItemList(), this, this);
        // restores the RecyclerView state only when the adapter is not empty
        // 官方提供，用于替代 onSaveInstanceState/onRestoreInstanceState
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        homeBinding.homeRecyclerview.setAdapter(adapter);
        homeBinding.homeRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        homeBinding.homeRecyclerview.setHasFixedSize(true);
        RecyclerViewUtils.enforceSingleScrollDirection(homeBinding.homeRecyclerview);

        // 等待RecyclerView加载完数据后，再开始转换动画
        startPostponedEnterTransition();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        homeBinding = null;
        adapter = null;
    }

    @Override
    public void onGetAllMagazines(View view, BlockItem blockItem) {
        NavDirections action = HomeFragmentDirections
                .actionHomeDestToSearchBlockFragment(blockItem.getId());
        Navigation.findNavController(view).navigate(action);
    }

    @Override
    public void onItemClick(View view, Magazine magazine) {
        NavDirections action = HomeFragmentDirections.actionHomeDestToDetailFragment(magazine.getId(),
                magazine.getMagazineInfo().getMagName(), magazine.getMagazineInfo().getMagPublishDate(),
                magazine.getMagazineInfo().getMagCoverImg().getUrl(), ViewCompat.getTransitionName(view));

//        ViewCompat.setTransitionName(view.findViewById(R.id.magCoverImg), "sharedImage" + magazine.getId());

        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(view, ViewCompat.getTransitionName(view))
                .build();
        Navigation.findNavController(view).navigate(action, extras);
    }
}