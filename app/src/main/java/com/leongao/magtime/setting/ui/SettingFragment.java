package com.leongao.magtime.setting.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.leongao.magtime.databinding.FragmentSettingBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    private FragmentSettingBinding binding;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setting.setTransitionName("sharedImage1");

        binding.setting.setOnClickListener(view1 -> {

//            ViewCompat.setTransitionName(binding.setting, "sharedImage1");

//            NavDirections action = SettingFragmentDirections.actionSettingDestToTextFragment();

//            FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
//                    .addSharedElement(binding.setting, "sharedImage1")
//                    .build();
//
//            Navigation.findNavController(view).navigate(action, extras);
        });

    }
}
