package com.leongao.magtime.search.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.leongao.magtime.R;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.interfaces.OnItemClickListener;
import com.leongao.magtime.databinding.SearchBlockmagazineItemBinding;
import com.leongao.magtime.search.ui.SearchBlockFragment;
import com.leongao.magtime.utils.ScreenUtil;

import java.util.List;

import timber.log.Timber;

public class SearchBlockRecyclerViewAdapter extends RecyclerView.Adapter<SearchBlockRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Magazine> mDataList;
    private OnItemClickListener mListener;
    private SearchBlockFragment fragment;
    private SearchBlockmagazineItemBinding itemViewBinding;
    private int magImgWidth;
    private int magImgHeight;
    public SearchBlockRecyclerViewAdapter(Context context,
                                          List<Magazine> dataList,
                                          OnItemClickListener listener,
                                          SearchBlockFragment fragment) {
        mContext = context;
        mDataList = dataList;
        mListener = listener;
        this.fragment = fragment;
        magImgWidth = (int) mContext.getResources().getDimension(R.dimen.block_magazine_imgWidth);
        magImgHeight = (int) mContext.getResources().getDimension(R.dimen.block_magazine_imgHeight);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemViewBinding = SearchBlockmagazineItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new ViewHolder(itemViewBinding.getRoot(), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Magazine magazine = mDataList.get(position);
        // set transitionName
        ViewCompat.setTransitionName(itemViewBinding.magCoverImg, "sharedImage" + magazine.getId());
//        itemViewBinding.magCoverImg.setTransitionName("sharedImage" + magazine.getId());
        Glide.with(mContext)
                .load(magazine.getMagazineInfo().getMagCoverImg().getUrl())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(itemViewBinding.magCoverImg);
        itemViewBinding.magName.setText(magazine.getMagazineInfo().getMagName());
        itemViewBinding.magPubDate.setText(magazine.getMagazineInfo().getMagPublishDate());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view.findViewById(R.id.magCoverImg), pos);
                    }
                }
            });
            // 根据屏幕宽度，缩放图片尺寸，适配多分辨率屏幕
            ViewGroup.LayoutParams params = itemViewBinding.magCover.getLayoutParams();
            Timber.d("screen width: %s", ScreenUtil.getScreenWidth());
            Timber.d("margin: %s", (int) mContext.getResources().getDimension(R.dimen.search_page_margin));
            params.width = (ScreenUtil.getScreenWidth() - (int) mContext.getResources().getDimension(R.dimen.search_page_margin) * 3) / 2;
            params.height = params.width * magImgHeight / magImgWidth;
            Timber.d("img width: %s", params.width);
            Timber.d("img height: %s", params.height);
            itemViewBinding.magCover.setLayoutParams(params);
        }

    }

    @Override
    public int getItemCount() {
        return (mDataList == null) ? 0 : mDataList.size();
    }
}
