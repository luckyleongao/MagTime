package com.leongao.magtime.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.leongao.magtime.R;
import com.leongao.magtime.databinding.SearchBlockmagazineItemBinding;
import com.leongao.magtime.databinding.SearchRecyclerviewItemBinding;
import com.leongao.magtime.home.model.Block;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.interfaces.OnItemClickListener;
import com.leongao.magtime.shelf.model.FavoriteMagazine;
import com.leongao.magtime.utils.ScreenUtil;

import java.util.List;

import timber.log.Timber;

public class CustomizedSearchRecyclerViewAdapter extends RecyclerView.Adapter<CustomizedSearchRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private OnItemClickListener mListener;
    private int magImgWidth;
    private int magImgHeight;
    private final AsyncListDiffer<Magazine> mDiffer = new AsyncListDiffer<>(this, DIFF_CALLBACK);

    public static final DiffUtil.ItemCallback<Magazine> DIFF_CALLBACK = new DiffUtil.ItemCallback<Magazine>() {
        @Override
        public boolean areItemsTheSame(@NonNull Magazine oldItem, @NonNull Magazine newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Magazine oldItem, @NonNull Magazine newItem) {
            return oldItem.equals(newItem);
        }
    };

    public CustomizedSearchRecyclerViewAdapter(Context context, OnItemClickListener listener) {
        mContext = context;
        mListener = listener;
        magImgWidth = (int) mContext.getResources().getDimension(R.dimen.block_magazine_imgWidth);
        magImgHeight = (int) mContext.getResources().getDimension(R.dimen.block_magazine_imgHeight);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用viewBinding时，导致结果更新混乱，改为findViewById，无此问题
        // 2024.6.13 by Leon
//        binding = SearchBlockmagazineItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
//        return new ViewHolder(binding.getRoot(), mListener);
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_blockmagazine_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView magCover;
        private TextView magName;
        private TextView magPubDate;
        private ImageView magCoverImg;
        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            magCover = itemView.findViewById(R.id.magCover);
            magName = itemView.findViewById(R.id.magName);
            magPubDate = itemView.findViewById(R.id.magPubDate);
            magCoverImg = itemView.findViewById(R.id.magCoverImg);
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view, getItem(pos));
                    }
                }
            });
            // 根据屏幕宽度，缩放图片尺寸，适配多分辨率屏幕
            ViewGroup.LayoutParams params = magCover.getLayoutParams();
            params.width = (ScreenUtil.getScreenWidth() - (int) mContext.getResources().getDimension(R.dimen.search_page_margin) * 3) / 2;
            params.height = params.width * magImgHeight / magImgWidth;
            magCover.setLayoutParams(params);
        }

        public void bind(Magazine magazine) {
            magName.setText(magazine.getMagazineInfo().getMagName());
            magPubDate.setText(magazine.getMagazineInfo().getMagPublishDate());
            Glide.with(mContext)
                    .load(magazine.getMagazineInfo().getMagCoverImg().getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(magCoverImg);
        }

    }

    /**
     * 向 adapter 提交新的数据源
     * @param list
     */
    public void submitList(List<Magazine> list) {
        mDiffer.submitList(list);
    }

    @Override
    public int getItemCount() {
        // mDiffer.getCurrentList()得到的数据集不会为null， 但可能未empty
        return mDiffer.getCurrentList().size();
    }

    public Magazine getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Magazine magazine);
    }
}
