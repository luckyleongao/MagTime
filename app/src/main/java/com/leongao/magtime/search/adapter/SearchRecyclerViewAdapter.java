package com.leongao.magtime.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.leongao.magtime.R;
import com.leongao.magtime.home.model.Block;
import com.leongao.magtime.interfaces.OnItemClickListener;
import com.leongao.magtime.databinding.SearchRecyclerviewItemBinding;
import com.leongao.magtime.utils.ScreenUtil;

import java.util.List;

import timber.log.Timber;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Block> mDataList;
    private OnItemClickListener mListener;
    private SearchRecyclerviewItemBinding itemViewBinding;
    private int blockImgWidth;
    private int blockImgHeight;
    public SearchRecyclerViewAdapter(Context context, List<Block> dataList, OnItemClickListener listener) {
        mContext = context;
        mDataList = dataList;
        mListener = listener;
        blockImgWidth = (int) mContext.getResources().getDimension(R.dimen.search_category_imgWidth);
        blockImgHeight = (int) mContext.getResources().getDimension(R.dimen.search_category_imgHeight);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemViewBinding = SearchRecyclerviewItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new ViewHolder(itemViewBinding.getRoot(), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Block block = getItem(position);
        Glide.with(mContext)
                .load(block.getBlockImg().getUrl())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(itemViewBinding.blockImgCover);
        itemViewBinding.blockName.setText(block.getBlockItemList().get(0).getBlockName());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view, pos);
                    }
                }
            });
            // 根据屏幕宽度，缩放图片尺寸，适配多分辨率屏幕
            ViewGroup.LayoutParams params = itemViewBinding.blockImg.getLayoutParams();
            params.width = (ScreenUtil.getScreenWidth() - (int) mContext.getResources().getDimension(R.dimen.search_page_margin) * 3) / 2;
            params.height = params.width * blockImgHeight / blockImgWidth;
            Timber.d("img width: %s", params.width);
            Timber.d("img height: %s", params.height);
            itemViewBinding.blockImg.setLayoutParams(params);
        }

    }

    @Override
    public int getItemCount() {
        return (mDataList == null) ? 0 : mDataList.size();
    }

    public Block getItem(int position) {
        return mDataList.get(position);
    }
}
