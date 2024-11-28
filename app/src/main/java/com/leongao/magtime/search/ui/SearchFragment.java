package com.leongao.magtime.search.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leongao.magtime.R;
import com.leongao.magtime.home.model.Block;
import com.leongao.magtime.search.adapter.SearchRecyclerViewAdapter;
import com.leongao.magtime.databinding.FragmentSearchBinding;
import com.leongao.magtime.search.model.CustomizedSearchResponse;
import com.leongao.magtime.viewmodel.SearchViewModel;
import com.leongao.magtime.widgets.RecyclerViewMargin;

import java.util.List;

import timber.log.Timber;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private SearchViewModel searchViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initObserver();
        initSearchView();
    }

    private void initViewModel() {
        searchViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication()).create(SearchViewModel.class);
    }

    private void initObserver() {
        searchViewModel.getSearchCategories().observe(getViewLifecycleOwner(), searchPageResponse -> {
            // TODO: 处理错误， 显示错误页面或弹出提示
            if (searchPageResponse.getError() != null) {
                Toast.makeText(getContext(), "获取搜索类别失败", Toast.LENGTH_SHORT).show();
                return;
            }
            initSearchPage(searchPageResponse.getData().getData());
        });
    }

    private void initSearchPage(List<Block> mDataList) {
        SearchRecyclerViewAdapter adapter = new SearchRecyclerViewAdapter(getContext(), mDataList, (view, pos) -> {
            NavDirections action = SearchFragmentDirections
                    .actionSearchDestToSearchBlockFragment(mDataList.get(pos).getId());
            Navigation.findNavController(view).navigate(action);
        });
        binding.searchRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // 避免熄屏或刷新等操作，多次触发增加margin
        if (binding.searchRecyclerview.getItemDecorationCount() == 0) {
            binding.searchRecyclerview.addItemDecoration(new RecyclerViewMargin(2, (int) getResources().getDimension(R.dimen.search_page_margin), true));
        }
        binding.searchRecyclerview.setAdapter(adapter);
    }


    private void initSearchView() {
        binding.searchBar.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            // 跳转到带有搜索历史的搜索页面
            if (hasFocus) {
                NavDirections action = SearchFragmentDirections.actionSearchDestToSearchHistoryFragment();
                Navigation.findNavController(view).navigate(action);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}