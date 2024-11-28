package com.leongao.magtime.shelf.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.leongao.magtime.shelf.ui.DownloadFragment;
import com.leongao.magtime.shelf.ui.FavoriteFragment;

public class ShelfViewPagerAdapter extends FragmentStateAdapter {
    private final int PAGE_NUM = 2;
    public ShelfViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new FavoriteFragment();
        }
        return new DownloadFragment();
    }

    @Override
    public int getItemCount() {
        return PAGE_NUM;
    }
}
