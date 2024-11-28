package com.leongao.magtime.shelf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.leongao.magtime.R;
import com.leongao.magtime.shelf.model.DownloadMagazine;
import com.leongao.magtime.shelf.model.FavoriteMagazine;
import com.leongao.magtime.shelf.ui.DownloadFragment;
import com.leongao.magtime.shelf.ui.FavoriteFragment;
import com.leongao.magtime.utils.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

public class DownloadRecyclerViewAdapter extends RecyclerView.Adapter<DownloadRecyclerViewAdapter.DownloadViewHolder> {

    private Context mContext;
    private List<DownloadMagazine> magazineList;
    private OnItemClickListener listener;
    private final AsyncListDiffer<DownloadMagazine> mDiffer = new AsyncListDiffer<>(this, DIFF_CALLBACK);

    public DownloadRecyclerViewAdapter(Context context, OnItemClickListener listener) {
        mContext = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.shelf_favorite_item, parent, false);
        return new DownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return mDiffer.getCurrentList().size();
    }

    public DownloadMagazine getItem(int position) {
        return mDiffer.getCurrentList().get(position);
    }

    public void submitList(List<DownloadMagazine> list) {
        magazineList = list;
        mDiffer.submitList(list);
    }

    public class DownloadViewHolder extends RecyclerView.ViewHolder {

        // 使用viewBinding时，导致选中错乱且无法多选，改为findViewById，可以实现多选item
        // 2024.2.29
        private ImageView magCoverImg;
        private ImageView selected;
        private TextView magName;
        private TextView magPubDate;
        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            magCoverImg = itemView.findViewById(R.id.magCoverImg);
            selected = itemView.findViewById(R.id.selected);
            magName = itemView.findViewById(R.id.magName);
            magPubDate = itemView.findViewById(R.id.magPubDate);
        }

        public void bind(DownloadMagazine magazine) {
            Glide.with(mContext)
                    .load(magazine.getMagCoverImgUrl())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(magCoverImg);
            magName.setText(magazine.getMagName());
            magPubDate.setText(magazine.getMagPubDate());
            selected.setVisibility(View.VISIBLE);
            selected.setVisibility(magazine.isSelected() ? View.VISIBLE : View.INVISIBLE);
            itemView.setOnClickListener(view -> {
                if (DownloadFragment.ACTION.equals(ConstantUtil.DONE)) {
                    magazine.setSelected(!magazine.isSelected());
                    selected.setVisibility(magazine.isSelected() ? View.VISIBLE : View.INVISIBLE);
                } else if (DownloadFragment.ACTION.equals(ConstantUtil.DELETE)) {
                    // 跳转到详情页
                    if (listener != null) {
                        int pos = getBindingAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onItemClick(itemView, magazineList.get(pos));
                        }
                    }
                }
            });
        }
    }

    public static final DiffUtil.ItemCallback<DownloadMagazine> DIFF_CALLBACK = new DiffUtil.ItemCallback<DownloadMagazine>() {
        @Override
        public boolean areItemsTheSame(@NonNull DownloadMagazine oldItem, @NonNull DownloadMagazine newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DownloadMagazine oldItem, @NonNull DownloadMagazine newItem) {
            return oldItem.equals(newItem);
        }
    };

    public List<DownloadMagazine> getAllUnselected() {
        List<DownloadMagazine> result = new ArrayList<>();
        for (DownloadMagazine magazine : magazineList) {
            if (!magazine.isSelected()) {
                result.add(magazine);
            }
        }
        return result;
    }

    public List<Integer> getAllSelectedIds() {
        List<Integer> result = new ArrayList<>();
        for (DownloadMagazine magazine : magazineList) {
            if (magazine.isSelected()) {
                result.add(magazine.getMagId());
            }
        }
        return result;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, DownloadMagazine magazine);
    }
}
