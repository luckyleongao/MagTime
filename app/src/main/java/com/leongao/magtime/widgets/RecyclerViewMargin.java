package com.leongao.magtime.widgets;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewMargin extends RecyclerView.ItemDecoration {

    private int columns;
    private int margins;
    // searchBlockFragment两列显示，无需设置 margins / 2，but why？
    private boolean isHalf;

    public RecyclerViewMargin(int columns, int margins, boolean isHalf) {
        this.columns = columns;
        this.margins = margins;
        this.isHalf = isHalf;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);
        // 设置所有底margin
        outRect.bottom = margins;
        // 第一列设置左margin
        if (position % columns == 0) outRect.left = margins;
        // 第二列设置一半左margin -> 一行两列，左中右三等份margin
        if (position % columns == 1) outRect.left = isHalf ? margins / 2 : margins;
    }
}
