package com.leongao.magtime.home.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leongao.magtime.home.model.BlockItem;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.databinding.HomeRecyclerviewItemBinding;
import com.leongao.magtime.home.ui.HomeFragment;

import java.util.HashMap;
import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.HomeViewHolder> {

    private Context mContext;
    private HomeFragment fragment;
    private List<BlockItem> blockItemList;
    private OnGetAllMagazines onGetAllListener;
    private OnItemClickListener mListener;
    private HomeRecyclerviewItemBinding binding;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
//    private HashMap<String, Parcelable> scrollState = new HashMap<>();
    public HomeRecyclerViewAdapter(Context context,
                                   HomeFragment fragment,
                                   List<BlockItem> dataList,
                                   OnGetAllMagazines getAllListener,
                                   OnItemClickListener listener) {
        mContext = context;
        this.fragment = fragment;
        blockItemList = dataList;
        onGetAllListener = getAllListener;
        mListener = listener;
    }

    @Override
    public void onViewRecycled(@NonNull HomeViewHolder holder) {
        super.onViewRecycled(holder);
        // save horizontal scroll state
//        String key = getSectionId(holder.getLayoutPosition());
//        scrollState.put(key, binding.blockMagazine.getLayoutManager().onSaveInstanceState());
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = HomeRecyclerviewItemBinding
                .inflate(LayoutInflater.from(mContext), parent, false);
        // inner recyclerview share the same view pool -> improve performance
        binding.blockMagazine.setRecycledViewPool(viewPool);
        return new HomeViewHolder(binding.getRoot(), onGetAllListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        BlockItem item = blockItemList.get(position);
        binding.blockName.setText(item.getBlockName());
        HomeBlockRecyclerViewAdapter adapter = new HomeBlockRecyclerViewAdapter(mContext, item.getMagazineList(), mListener);
        // restores the RecyclerView state only when the adapter is not empty
        // 官方提供，用于替代 onSaveInstanceState/onRestoreInstanceState
        adapter.setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        binding.blockMagazine.setAdapter(adapter);
        binding.blockMagazine.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        binding.blockMagazine.setHasFixedSize(true);

        // restore horizontal scroll state
//        Parcelable state = scrollState.get(getSectionId(position));
//        if (state != null) {
//            binding.blockMagazine.getLayoutManager().onRestoreInstanceState(state);
//        } else {
//            binding.blockMagazine.getLayoutManager().scrollToPosition(0);
//        }
    }

//    private String getSectionId(int position) {
//        return String.valueOf(blockItemList.get(position).getId());
//    }

    @Override
    public int getItemCount() {
        return (blockItemList == null) ? 0 : blockItemList.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        public HomeViewHolder(@NonNull View itemView, OnGetAllMagazines getAllListener) {
            super(itemView);
            binding.getAll.setOnClickListener(view -> {
                if (getAllListener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        getAllListener.onGetAllMagazines(itemView, blockItemList.get(pos));
                    }
                }
            });
        }
    }

    public interface OnGetAllMagazines {
        void onGetAllMagazines(View view, BlockItem blockItem);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Magazine magazine);
    }

}
