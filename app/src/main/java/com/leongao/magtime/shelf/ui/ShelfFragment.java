package com.leongao.magtime.shelf.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leongao.magtime.shelf.adapter.ShelfViewPagerAdapter;
import com.leongao.magtime.utils.ConstantUtil;
import com.google.android.material.tabs.TabLayoutMediator;
import com.leongao.magtime.databinding.FragmentShelfBinding;

public class ShelfFragment extends Fragment {
    private FragmentShelfBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShelfBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewPager();
    }

    private void initViewPager() {
        ShelfViewPagerAdapter adapter = new ShelfViewPagerAdapter(getActivity());
        binding.viewpager.setAdapter(adapter);
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tab,
                binding.viewpager, (tab, position) -> {
                    tab.setText(ConstantUtil.viewPagerNames[position]);
                });
        mediator.attach();
    }
}