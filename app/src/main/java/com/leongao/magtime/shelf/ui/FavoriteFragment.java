package com.leongao.magtime.shelf.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leongao.magtime.databinding.FragmentFavoriteBinding;
import com.leongao.magtime.db.entity.Favorite;
import com.leongao.magtime.shelf.adapter.FavoriteRecyclerViewAdapter;
import com.leongao.magtime.shelf.model.FavoriteMagazine;
import com.leongao.magtime.utils.ConstantUtil;
import com.leongao.magtime.viewmodel.FavoriteViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FavoriteFragment extends Fragment implements FavoriteDialogFragment.DialogCallbackListener {

    private FragmentFavoriteBinding binding;
    private FavoriteRecyclerViewAdapter adapter;

    private List<FavoriteMagazine> favoriteMagazineList = new ArrayList<>();
    private FavoriteViewModel favoriteViewModel;

    // 此字段用于在adapter中判断，执行item的“选择”还是“跳转”操作
    public static String ACTION = ConstantUtil.DELETE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFavoritePage();
    }

    private void initFavoritePage() {
        adapter = new FavoriteRecyclerViewAdapter(getContext(), (view, item) -> {
            NavDirections action = ShelfFragmentDirections.actionShelfDestToDetailFragment(
                    item.getMagId(), item.getMagName(),
                    item.getMagPubDate(),
                    item.getMagCoverImgUrl(), "");

            Navigation.findNavController(view).navigate(action);
        });
        binding.favoriteRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));

        favoriteViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(FavoriteViewModel.class);

        favoriteViewModel.findAll().observe(getViewLifecycleOwner(), favorites -> {
            favoriteMagazineList.clear();
            // TODO: 父类复制给子类，替代方法？
            for (Favorite favorite : favorites) {
                FavoriteMagazine magazine = new FavoriteMagazine();
                magazine.setSelected(false);
                magazine.setMagId(favorite.getMagId());
                magazine.setMagName(favorite.getMagName());
                magazine.setMagPubDate(favorite.getMagPubDate());
                magazine.setMagCoverImgUrl(favorite.getMagCoverImgUrl());
                favoriteMagazineList.add(magazine);
            }
            adapter.submitList(favoriteMagazineList);
            binding.favoriteRecyclerview.setAdapter(adapter);
        });

        binding.favoriteAction.setOnClickListener(view -> {
            if (binding.favoriteAction.getText().equals(ConstantUtil.DONE)) {
                // 在数据库删除选中的收藏数据
                favoriteViewModel.deleteByIds(adapter.getAllSelectedIds().stream().mapToInt(i->i).toArray());

                if (adapter.getAllUnselected().size() != 0) {
                    adapter.submitList(adapter.getAllUnselected());
                }
                // 重置编辑状态
                binding.favoriteAction.setText(ConstantUtil.DELETE);
                ACTION = ConstantUtil.DELETE;
            } else if (binding.favoriteAction.getText().equals(ConstantUtil.DELETE)) {
                // 生成弹窗
                FavoriteDialogFragment dialogFragment = FavoriteDialogFragment.newInstance(ConstantUtil.DELETE_FAVORITE);
                dialogFragment.setTargetFragment(FavoriteFragment.this, 300);
                dialogFragment.show(getActivity().getSupportFragmentManager(), ConstantUtil.DELETE_FAVORITE);
            }

        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onDialogFinished(String actionName) {
        if (actionName == null) return;
        if (actionName.equals(ConstantUtil.SELECT_SOME)) {
            binding.favoriteAction.setText(ConstantUtil.DONE);
            ACTION = ConstantUtil.DONE;
        } else if (actionName.equals(ConstantUtil.DELETE_ALL)) {
            // 删除全部
            adapter.submitList(null);
            // 删除后重置状态
            binding.emptyFavorite.setVisibility(View.VISIBLE);
            binding.favoriteAction.setVisibility(View.GONE);
            // 删除数据库中的全部收藏数据
            favoriteViewModel.deleteAll();
        }
    }
}