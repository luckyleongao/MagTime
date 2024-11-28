package com.leongao.magtime.search.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Parcelable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leongao.magtime.R;
import com.leongao.magtime.app.DataStoreHelper;
import com.leongao.magtime.app.DataStoreSingleton;
import com.leongao.magtime.app.SharedPreferenceHelper;
import com.leongao.magtime.databinding.FragmentSearchHistoryBinding;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.viewmodel.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SearchHistoryFragment extends Fragment {

    private FragmentSearchHistoryBinding binding;
    private SearchViewModel searchViewModel;
//    private DataStoreHelper dataStoreHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        initSearchView();
//        initDataStore();
        initSearchHistoryTags();
        initDeleteBtn();
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
        // 进入页面自动获取焦点，并展开软键盘
        binding.searchBar.requestFocus();
        binding.searchBar.setOnQueryTextFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) showKeyboard(view.findFocus());
        });
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) submitSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Timber.d("newText: " + newText);
                return false;
            }
        });
    }

//    private void initDataStore() {
//        dataStoreHelper = new DataStoreHelper(getActivity(), DataStoreSingleton.getInstance().getDataStore());
//    }

    private void initSearchHistoryTags() {
        List<String> tagList = SharedPreferenceHelper.getAllSearchHistoryTags();
        if (tagList.size() == 0) return;
        Timber.d("tag list size: %s", tagList.size());
        Timber.d("tag list 0: %s", tagList.get(0));
//        String[] strs = new String[]{"Java", "Android", "人", "经济", "球", "国史大纲", "算法导论", "设计模式", "统计方法"};
        for (String tag : tagList) {
            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setTextColor(getResources().getColor(R.color.nav_grey));
            textView.setBackgroundResource(R.drawable.search_history_tag_bg);
            textView.setText(tag);
            textView.setOnClickListener(view -> submitSearch(tag));
            binding.searchHistoryTags.addView(textView);
        }
    }

    private void initDeleteBtn() {
        binding.delete.setOnClickListener( view -> {
            SharedPreferenceHelper.deleteALlSearchHistoryTags();
            binding.searchHistoryTags.removeAllViews();
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
            navigateToSearchResult(customizedSearchResponse.getData().getMagazineList());
        });
    }

    private void navigateToSearchResult(List<Magazine> magazineList) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("magazineList", (ArrayList<? extends Parcelable>) magazineList);
        Navigation.findNavController(getView()).navigate(R.id.action_searchHistoryFragment_to_searchResultFragment, bundle);
    }


    private void showKeyboard (View view) {
        InputMethodManager im = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im != null) im.showSoftInput(view, 0);
    }

}