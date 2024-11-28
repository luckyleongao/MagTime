package com.leongao.magtime.reader.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.leongao.magtime.home.model.MagCoverImg;
import com.leongao.magtime.utils.ConstantUtil;
import com.leongao.magtime.databinding.FragmentClassicReaderItemBinding;

import java.util.Collections;
import java.util.List;
import java.util.Timer;

import timber.log.Timber;

public class ClassicReaderAdapter extends RecyclerView.Adapter<ClassicReaderAdapter.ReaderViewHolder>
        implements ListPreloader.PreloadModelProvider<String> {

    private Context context;
    private List<String> magCoverImgList;
    private FragmentClassicReaderItemBinding binding;
    private OnPhotoTapListener listener;
    public ClassicReaderAdapter(Context context,
                                List<String> magCoverImgList,
                                OnPhotoTapListener listener) {
        this.context = context;
        this.magCoverImgList = magCoverImgList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = FragmentClassicReaderItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ReaderViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ReaderViewHolder holder, int position) {
        Glide.with(context)
                .load(magCoverImgList.get(position))
                .override(ConstantUtil.imageWidthPixels, ConstantUtil.imageHeightPixels)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(binding.classicReaderImg);
        Timber.d("getItemCount is: %s", getItemCount());
        Timber.d("abs position is: %s", holder.getAbsoluteAdapterPosition());
    }

    @Override
    public int getItemCount() {
        return (magCoverImgList == null) ? 0 : magCoverImgList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        String url = magCoverImgList.get(position);
        if (TextUtils.isEmpty(url)) return Collections.emptyList();
        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        return Glide.with(context)
                .load(item)
                .override(ConstantUtil.imageWidthPixels, ConstantUtil.imageHeightPixels)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
    }

    public class ReaderViewHolder extends RecyclerView.ViewHolder {

        public ReaderViewHolder(@NonNull View itemView) {
            super(itemView);

            binding.classicReaderImg.setOnPhotoTapListener((view, x, y) -> {
                listener.onPhotoTap(view, x, y);
            });
        }
    }

}
