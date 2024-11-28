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
import android.widget.Toast;

import com.leongao.magtime.R;
import com.leongao.magtime.databinding.FragmentDownloadBinding;
import com.leongao.magtime.databinding.FragmentFavoriteBinding;
import com.leongao.magtime.db.entity.Download;
import com.leongao.magtime.db.entity.Favorite;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.shelf.adapter.DownloadRecyclerViewAdapter;
import com.leongao.magtime.shelf.adapter.FavoriteRecyclerViewAdapter;
import com.leongao.magtime.shelf.model.DownloadMagazine;
import com.leongao.magtime.shelf.model.FavoriteMagazine;
import com.leongao.magtime.utils.ConstantUtil;
import com.leongao.magtime.viewmodel.DownloadViewModel;
import com.leongao.magtime.viewmodel.FavoriteViewModel;

import java.util.ArrayList;
import java.util.List;

public class DownloadFragment extends Fragment implements FavoriteDialogFragment.DialogCallbackListener {

    private FragmentDownloadBinding binding;
    private DownloadRecyclerViewAdapter adapter;

    private List<DownloadMagazine> downloadMagazineList = new ArrayList<>();
    private DownloadViewModel downloadViewModel;

    // 此字段用于在adapter中判断，执行item的“选择”还是“跳转”操作
    public static String ACTION = ConstantUtil.DELETE;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDownloadBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDownloadPage();
    }

    private void initDownloadPage() {
        adapter = new DownloadRecyclerViewAdapter(getContext(), (view, item) -> {
//            NavDirections action = ShelfFragmentDirections.actionShelfDestToDetailFragment(
//                    item.getMagId(), item.getMagName(),
//                    item.getMagPubDate(),
//                    item.getMagCoverImgUrl());
//            Bundle bundle = new Bundle();
//            Magazine magazine = new Magazine();
//            magazine.setId(item.getMagId());
//            magazine.set
//            bundle.putSerializable("magazine", item);
//            Navigation.findNavController(view).navigate(R.id.action_shelf_dest_to_detailFragment, bundle);

        });
        binding.downloadRecyclerview.setLayoutManager(new GridLayoutManager(getContext(), 2));

        DownloadViewModel.Factory factory = new DownloadViewModel.Factory(getActivity().getApplication(), null);
        downloadViewModel = new ViewModelProvider(this, factory).get(DownloadViewModel.class);

        downloadViewModel.findAll().observe(getViewLifecycleOwner(), resultState -> {
            // TODO: 处理错误， 显示错误页面或弹出提示
            if (resultState.getError() != null) {
                Toast.makeText(getContext(), "获取下载列表失败", Toast.LENGTH_SHORT).show();
                return;
            }

            downloadMagazineList.clear();
            // TODO: 父类复制给子类，替代方法？
            for (Download download : resultState.getData()) {
                DownloadMagazine magazine = new DownloadMagazine();
                magazine.setSelected(false);
                magazine.setMagId(download.getMagId());
                magazine.setMagName(download.getMagName());
                magazine.setMagPubDate(download.getMagPubDate());
                magazine.setMagCoverImgUrl(download.getMagCoverImgUrl());
                downloadMagazineList.add(magazine);
            }
            adapter.submitList(downloadMagazineList);
            binding.downloadRecyclerview.setAdapter(adapter);
        });

        binding.downloadAction.setOnClickListener(view -> {
            if (binding.downloadAction.getText().equals(ConstantUtil.DONE)) {
                // 在数据库删除选中的杂志
                downloadViewModel.deleteByIds(adapter.getAllSelectedIds().stream().mapToInt(i->i).toArray());
                // 在外部存储路径删除选中的杂志
                downloadViewModel.deleteFromStorageByIds(adapter.getAllSelectedIds().stream().mapToInt(i->i).toArray());

                if (adapter.getAllUnselected().size() != 0) {
                    adapter.submitList(adapter.getAllUnselected());
                }
                // 重置编辑状态
                binding.downloadAction.setText(ConstantUtil.DELETE);
                ACTION = ConstantUtil.DELETE;
            } else if (binding.downloadAction.getText().equals(ConstantUtil.DELETE)) {
                // 生成弹窗
                FavoriteDialogFragment dialogFragment = FavoriteDialogFragment.newInstance(ConstantUtil.DELETE_DOWNLOAD);
                dialogFragment.setTargetFragment(DownloadFragment.this, 300);
                dialogFragment.show(getActivity().getSupportFragmentManager(), ConstantUtil.DELETE_DOWNLOAD);
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
            binding.downloadAction.setText(ConstantUtil.DONE);
            ACTION = ConstantUtil.DONE;
        } else if (actionName.equals(ConstantUtil.DELETE_ALL)) {
            // 删除全部
            adapter.submitList(null);
            // 删除后重置状态
            binding.emptyDownload.setVisibility(View.VISIBLE);
            binding.downloadAction.setVisibility(View.GONE);
            // 删除数据库中的全部收藏数据
            downloadViewModel.deleteAll();
            // 删除外部存储中的全部杂志
            downloadViewModel.deleteAllFromStorage();
        }
    }
}