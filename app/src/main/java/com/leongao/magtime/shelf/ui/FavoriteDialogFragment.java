package com.leongao.magtime.shelf.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leongao.magtime.databinding.FragmentFavoriteDialogBinding;
import com.leongao.magtime.utils.ConstantUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FavoriteDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteDialogFragment extends DialogFragment {

    private FragmentFavoriteDialogBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String DIALOG_TITLE = "dialog_title";

    // TODO: Rename and change types of parameters
    private String title;

    public FavoriteDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param title Parameter 1.
     * @return A new instance of fragment FavoriteDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavoriteDialogFragment newInstance(String title) {
        FavoriteDialogFragment fragment = new FavoriteDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(DIALOG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoriteDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(ConstantUtil.ALL_DELETE);
        builder.setPositiveButton(ConstantUtil.DELETE_ALL, (dialogInterface, i) -> {
            sendBackResult(ConstantUtil.DELETE_ALL);
        });
        builder.setNegativeButton(ConstantUtil.SELECT_SOME, (dialogInterface, i) -> {
            sendBackResult(ConstantUtil.SELECT_SOME);
        });
        builder.setNeutralButton(ConstantUtil.CANCEL, (dialog, i) -> {
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    // send result back to parent fragment
    private void sendBackResult(String action) {
        DialogCallbackListener listener = (DialogCallbackListener) getTargetFragment();
        listener.onDialogFinished(action);
        dismiss();
    }

    public interface DialogCallbackListener {
        void onDialogFinished(String actionName);
    }
}