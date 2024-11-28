package com.leongao.magtime.search.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leongao.magtime.R;
import com.leongao.magtime.app.DataStoreHelper;
import com.leongao.magtime.app.DataStoreSingleton;
import com.leongao.magtime.app.SharedPreferenceHelper;
import com.leongao.magtime.databinding.FragmentSearchResultBinding;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.search.adapter.CustomizedSearchRecyclerViewAdapter;
import com.leongao.magtime.viewmodel.SearchViewModel;
import com.leongao.magtime.widgets.RecyclerViewMargin;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SearchResultFragment extends Fragment {
    private FragmentSearchResultBinding binding;
    private SearchViewModel searchViewModel;
//    private DataStoreHelper dataStoreHelper;
    private List<Magazine> magazineList = new ArrayList<>();
    private CustomizedSearchRecyclerViewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initSearchView();
        initView();
//        initDataStore();
    }

    private void initViewModel() {
        searchViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication()).create(SearchViewModel.class);
    }

    private void initSearchView() {
        // Toolbar返回按钮点击事件
        binding.toolbar.setNavigationOnClickListener(view -> {
            // 如果搜索框中有文字，则清除其中的文字
            if (!TextUtils.isEmpty(binding.searchBar.getQuery())) {
                binding.searchBar.setQuery("", false);
            } else {
                // 否则关闭搜索框
                Navigation.findNavController(view).navigateUp();
            }
        });
        // Toolbar搜索框事件
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) submitSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Timber.d("newText: %s", newText);
                return false;
            }
        });
    }

    private void submitSearch(String keywords) {
        // 搜索关键词写入磁盘，用于展示历史搜索
//        dataStoreHelper.putStringValue(keywords, keywords);
        SharedPreferenceHelper.putSearchHistoryTag(keywords);
        // 执行搜索操作
        searchViewModel.getSearchResultByKeywords(keywords).observe(getViewLifecycleOwner(), customizedSearchResponse -> {
            // TODO: 处理错误， 显示错误页面或弹出提示
            if (customizedSearchResponse.getError() != null) {
                Toast.makeText(getContext(), "获取自定义搜索列表失败", Toast.LENGTH_SHORT).show();
                return;
            }
            if (customizedSearchResponse.getData().getMagazineList() != null
                    && customizedSearchResponse.getData().getMagazineList().size() > 0) {
                displayResultPage(false);
                // 更新搜索结果
                adapter.submitList(customizedSearchResponse.getData().getMagazineList());
            } else {
                displayResultPage(true);
            }
        });
    }

//    private void initDataStore() {
//        dataStoreHelper = new DataStoreHelper(getActivity(), DataStoreSingleton.getInstance().getDataStore());
//    }

    private void initView() {
        Bundle bundle = getArguments();
        if (bundle != null) magazineList = bundle.getParcelableArrayList("magazineList");
        boolean isEmpty = (bundle == null || magazineList == null || magazineList.size() == 0);

        initSearchPage();
        displayResultPage(isEmpty);
    }

    private void initSearchPage() {
        adapter = new CustomizedSearchRecyclerViewAdapter(getContext(), (view, magazine) -> {
            if (magazine != null) {
                NavDirections action = SearchResultFragmentDirections.actionSearchResultFragmentToDetailFragment(
                        magazine.getId(), magazine.getMagazineInfo().getMagName(), magazine.getMagazineInfo().getMagPublishDate(),
                        magazine.getMagazineInfo().getMagCoverImg().getUrl(), "");
                Navigation.findNavController(view).navigate(action);
            }
        });
        adapter.submitList(magazineList);
        binding.searchRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // 避免熄屏或刷新等操作，多次触发增加margin
        if (binding.searchRecyclerview.getItemDecorationCount() == 0) {
            binding.searchRecyclerview.addItemDecoration(new RecyclerViewMargin(2, (int) getResources().getDimension(R.dimen.search_page_margin), true));
        }
        binding.searchRecyclerview.setAdapter(adapter);
    }

    private void displayResultPage(boolean isEmpty) {
        binding.searchRecyclerview.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.searchNotFound.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

}