package com.io.wordguard.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import com.io.wordguard.R;
import com.io.wordguard.ui.activities.ViewWordActivity;
import com.io.wordguard.word.Word;
import java.util.ArrayList;

public class WordListRecyclerAdapter extends RecyclerView.Adapter<WordListRecyclerAdapter.ViewHolder> {

    ArrayList<Word> mItems;
    Activity mActivity;

    public WordListRecyclerAdapter(ArrayList<Word> mItems, Activity mActivity) {
        this.mItems = mItems;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.reset();
        holder.contentRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                // Ordinary Intent for launching a new activity
                Intent intent = new Intent(context, ViewWordActivity.class);

                // Get the transition name from the string
                String transitionName = context.getString(R.string.transition_word_view_string);

                // Define the view that the animation will start from
                ActivityOptionsCompat options =

                        ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity,
                                holder.cardView,   // Starting view
                                transitionName    // The String
                        );
                //Start the Intent
                ActivityCompat.startActivity(mActivity, intent, options.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    public void removeItemAt(int position) {
//        mItems.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Interpolator interpolator = new DecelerateInterpolator(2f);
        private View contentRoot;
        private View leftImage;
        private View rightImage;
        private View view;
        private CardView cardView;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            contentRoot = view.findViewById(R.id.content_root);
            leftImage = view.findViewById(R.id.left_image);
            rightImage = view.findViewById(R.id.right_image);
            cardView = (CardView) view.findViewById(R.id.rootView);
        }

        public View getView() {
            return view;
        }

        public void handleSwipeGesture(float dX) {
            float height = contentRoot.getHeight();
            float maxAbsXDiff = contentRoot.getWidth() / 2f;
            float factor = interpolator.getInterpolation(Math.min(Math.abs(dX), maxAbsXDiff) / maxAbsXDiff);
            float diffX = factor * height;
            if (dX < 0) {
                diffX *= -1;
            }
            contentRoot.setTranslationX(diffX);
            if (dX < 0) {
                rightImage.setAlpha(factor);
                rightImage.setTranslationX((height - Math.abs(diffX)) / 2f);
            } else {
                leftImage.setAlpha(factor);
                leftImage.setTranslationX(Math.abs(height - diffX) / -2f);
            }
        }

        public void reset() {
            contentRoot.setTranslationX(0);
            rightImage.setAlpha(1f);
            rightImage.setTranslationX(0);
            leftImage.setAlpha(1f);
            leftImage.setTranslationX(0);
        }
    }
}
