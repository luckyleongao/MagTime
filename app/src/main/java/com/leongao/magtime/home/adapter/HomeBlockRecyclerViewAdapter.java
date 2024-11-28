package com.leongao.magtime.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.leongao.magtime.R;
import com.leongao.magtime.home.model.Magazine;
import com.leongao.magtime.home.adapter.HomeRecyclerViewAdapter.OnItemClickListener;
import com.leongao.magtime.databinding.HomeBlockmagazineItemBinding;

import java.util.List;

public class HomeBlockRecyclerViewAdapter extends RecyclerView.Adapter<HomeBlockRecyclerViewAdapter.HomeBlockViewHolder> {

    private HomeBlockmagazineItemBinding binding;
    private Context mContext;
    private List<Magazine> magazineList;
    private OnItemClickListener mListener;
    public HomeBlockRecyclerViewAdapter(Context context, List<Magazine> dataList, OnItemClickListener listener) {
        mContext = context;
        magazineList = dataList;
        mListener = listener;
    }

    @NonNull
    @Override
    public HomeBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = HomeBlockmagazineItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
        return new HomeBlockViewHolder(binding.getRoot(), mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeBlockViewHolder holder, int position) {
        Magazine magazine = magazineList.get(position);
        if (magazine != null && magazine.getMagazineInfo() != null) {
            // set transitionName
            ViewCompat.setTransitionName(binding.magCoverImg, "sharedImage" + magazine.getId());
            Glide.with(mContext)
                    .load(magazine.getMagazineInfo().getMagCoverImg().getUrl())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.magCoverImg);
            binding.magName.setText(magazine.getMagazineInfo().getMagName());
            binding.magPubDate.setText(magazine.getMagazineInfo().getMagPublishDate());
        }
    }

    @Override
    public int getItemCount() {
        return (magazineList == null) ? 0 : magazineList.size();
    }

    public class HomeBlockViewHolder extends RecyclerView.ViewHolder {
        public HomeBlockViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onItemClick(view.findViewById(R.id.magCoverImg), magazineList.get(pos));
                    }
                }
            });
        }
    }

 }
