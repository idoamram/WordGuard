package com.io.wordguard.ui.componenets;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;

import com.io.wordguard.ui.adapters.WordListRecyclerAdapter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SwipeRecycleView extends RecyclerView {
    public interface OnSwipeActionListener {
        @Retention(RetentionPolicy.SOURCE)
        @IntDef({LEFT, RIGHT})
        @interface SwipeDirection {
        }

        int LEFT = 0;
        int RIGHT = 1;

        void onSwipe(int position, @SwipeDirection int direction);
    }

    OnSwipeActionListener listener;

    public void setSwipeListener(OnSwipeActionListener swipeListener) {
        listener = swipeListener;
    }


    public SwipeRecycleView(Context context) {
        super(context);
        setupSwipeGesture();
    }

    public SwipeRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupSwipeGesture();
    }

    public SwipeRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupSwipeGesture();
    }

    private void setupSwipeGesture() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = createSwipeItemTouchHelper();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(this);
    }

    private ItemTouchHelper.SimpleCallback createSwipeItemTouchHelper() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                //Reset the state
                getAdapter().notifyItemChanged(position);
                if (listener != null) {
                    listener.onSwipe(position, direction == ItemTouchHelper.LEFT ? OnSwipeActionListener.LEFT : OnSwipeActionListener.RIGHT);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && viewHolder instanceof WordListRecyclerAdapter.ViewHolder) {
                    WordListRecyclerAdapter.ViewHolder holder = (WordListRecyclerAdapter.ViewHolder) viewHolder;
                    holder.handleSwipeGesture(dX);
                }
            }
        };
    }
}