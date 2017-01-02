package com.io.wordguard.ui.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.View;

import com.io.wordguard.R;

public class SwipeHelperCallback extends ItemTouchHelper.SimpleCallback {

    private SwipeCallback mSwipeCallback;
    private Context mContext;

    public SwipeHelperCallback(Context mContext, int dragDirs, int swipeDirs, SwipeCallback mSwipeCallback) {
        super(dragDirs, swipeDirs);
        this.mSwipeCallback = mSwipeCallback;
        this.mContext = mContext;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

        if (direction == ItemTouchHelper.LEFT) {
            mSwipeCallback.onSwiped(false, position);
        } else {
            mSwipeCallback.onSwiped(true, position);
        }
    }

    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;

            Paint paint = new Paint();
            Bitmap icon;

            if (dX > 0) {
                icon = BitmapHelper.getBitmapFromVectorDrawable(mContext, R.drawable.ic_done_white);
                paint.setColor(ContextCompat.getColor(mContext, R.color.swipe_done_green));
                // Draw Rect with varying right side, equal to displacement dX
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                        (float) itemView.getBottom(), paint);
                // Set the image icon for Left swipe
                c.drawBitmap(icon,
                        (float) itemView.getLeft() + dp2px(24),
                        (float) itemView.getTop() + ((float) itemView.getBottom() -
                                (float) itemView.getTop() - icon.getHeight()) / 2, paint);
            } else {
                icon = BitmapHelper.getBitmapFromVectorDrawable(mContext, R.drawable.ic_delete_white);
                paint.setColor(ContextCompat.getColor(mContext, R.color.swipe_delete_red));
                // Draw Rect with varying left side, equal to the item's right side
                // plus negative displacement dX
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                // Set the image icon for Right swipe
                c.drawBitmap(icon,
                        (float) itemView.getRight() - dp2px(24) - icon.getWidth(),
                        (float) itemView.getTop() + ((float) itemView.getBottom() -
                                (float) itemView.getTop() - icon.getHeight()) / 2, paint);
            }
            // Fade out the view as it is swiped out of the parent's bounds
//            final float alpha = 1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
//            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    private static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
