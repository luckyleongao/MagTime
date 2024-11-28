package com.leongao.magtime.search.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.transition.TransitionInflater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leongao.magtime.R;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.interfaces.OnItemClickListener;
import com.leongao.magtime.search.adapter.SearchBlockRecyclerViewAdapter;
import com.leongao.magtime.databinding.SearchBlockmagazineBinding;
import com.leongao.magtime.search.adapter.SearchRecyclerViewAdapter;
import com.leongao.magtime.viewmodel.SearchViewModel;
import com.leongao.magtime.widgets.RecyclerViewMargin;

import java.util.List;

import timber.log.Timber;

public class SearchBlockFragment extends Fragment {

    private SearchBlockmagazineBinding binding;
    private SearchViewModel searchViewModel;
    private OnItemClickListener onItemClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementReturnTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.default_transition));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = SearchBlockmagazineBinding.inflate(LayoutInflater.from(getContext()), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 推迟共享元素转换动画，等待RecyclerView数据加载完成
        postponeEnterTransition();

        initViewModel();
        initObserver();
    }

    private void initViewModel() {
        searchViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication()).create(SearchViewModel.class);
    }

    private void initObserver() {
        int blockId = SearchBlockFragmentArgs.fromBundle(getArguments()).getBlockId();
        searchViewModel.getSearchBlockItem(blockId).observe(getViewLifecycleOwner(), searchBlockResponse -> {
            // TODO: 处理错误， 显示错误页面或弹出提示
            if (searchBlockResponse.getError() != null) {
                Toast.makeText(getContext(), "获取搜索列表失败", Toast.LENGTH_SHORT).show();
                return;
            }
            initSearchBlockPage(searchBlockResponse.getData().getData().getMagazineList(),
                    searchBlockResponse.getData().getData().getBlockName());
        });
    }

    private void initSearchBlockPage(List<Magazine> dataList, String blockName) {
        onItemClickListener = (view, pos) -> {
            // 跳转到详情页
            Magazine magazine = dataList.get(pos);
            NavDirections action = SearchBlockFragmentDirections.actionSearchBlockFragmentToDetailFragment(
                    magazine.getId(), magazine.getMagazineInfo().getMagName(),
                    magazine.getMagazineInfo().getMagPublishDate(),
                    magazine.getMagazineInfo().getMagCoverImg().getUrl(), ViewCompat.getTransitionName(view));

            // 设置共享元素转换动画
            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                    .addSharedElement(view, ViewCompat.getTransitionName(view))
                    .build();

            Navigation.findNavController(view).navigate(action, extras);
        };

        SearchBlockRecyclerViewAdapter adapter = new SearchBlockRecyclerViewAdapter(getContext(), dataList, onItemClickListener, this);
        binding.searchBlockRecyclerview.setAdapter(adapter);
        binding.searchBlockRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // 避免熄屏或刷新等操作，多次触发增加margin
        if (binding.searchBlockRecyclerview.getItemDecorationCount() == 0) {
            binding.searchBlockRecyclerview.addItemDecoration(new RecyclerViewMargin(2, (int) getResources().getDimension(R.dimen.search_page_margin), false));
        }
        binding.searchBlockName.setText(blockName);
        binding.searchBack.setOnClickListener(view -> {
            Navigation.findNavController(view).navigateUp();
        });

        // 等待RecyclerView加载完数据后，再开始转换动画
        startPostponedEnterTransition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}