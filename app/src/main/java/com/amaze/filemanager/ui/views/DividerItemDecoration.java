package com.amaze.filemanager.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amaze.filemanager.adapters.RecyclerAdapter;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private final Drawable mDivider;

    private final boolean show;
    private final boolean showtopbottomdividers;
    private final int leftPaddingPx;
    private final int rightPaddingPx;

    public DividerItemDecoration(Context context, boolean showtopbottomdividers, boolean show) {
        final TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
        mDivider = typedArray.getDrawable(0);
        typedArray.recycle();
        this.show = show;
        this.showtopbottomdividers = showtopbottomdividers;
        leftPaddingPx = (int) (72 * (context.getResources().getDisplayMetrics().densityDpi / 160f));
        rightPaddingPx = (int) (16 * (context.getResources().getDisplayMetrics().densityDpi / 160f));
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        if (!show) return;
        if (mDivider != null) drawVertical(c, parent);
    }

    /**
     * Draws the divider on the canvas provided by RecyclerView Be advised - divider will be drawn
     * before the views, hence it'll be below the views of adapter
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft() + leftPaddingPx;
        final int right = parent.getWidth() - parent.getPaddingRight() - rightPaddingPx;

        final int childCount = parent.getChildCount();
        for (int i = showtopbottomdividers ? 0 : 1; i < childCount - 1; i++) {

            final View child = parent.getChildAt(i);

            int viewType = parent.getChildViewHolder(child).getItemViewType();
            if (viewType == RecyclerAdapter.TYPE_HEADER_FILES
                    || viewType == RecyclerAdapter.TYPE_HEADER_FOLDERS) {
                // no need to decorate header views
                continue;
            }

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(
            @NonNull Rect outRect,
            @NonNull View view,
            @NonNull RecyclerView parent,
            @NonNull RecyclerView.State state
    ) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {

            // not to draw an offset at the top of recycler view
            return;
        }

        outRect.top = mDivider.getIntrinsicHeight();
    }
}
