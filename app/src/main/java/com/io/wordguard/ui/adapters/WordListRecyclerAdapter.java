package com.io.wordguard.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.TextView;
import com.io.wordguard.R;
import com.io.wordguard.word.Word;
import java.util.ArrayList;

public class WordListRecyclerAdapter extends RecyclerView.Adapter<WordListRecyclerAdapter.ViewHolder> {

    ArrayList<Word> mItems;

    public WordListRecyclerAdapter(ArrayList<Word> items) {
        this.mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Interpolator interpolator = new DecelerateInterpolator(2f);
        private TextView nameTextView;
        private View leftImage;
        private View rightImage;
        private View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            nameTextView = (TextView) view.findViewById(R.id.share_link_name_text);
            leftImage = view.findViewById(R.id.left_image);
            rightImage = view.findViewById(R.id.right_image);
        }

        public void setName(String name) {
            nameTextView.setText(name);
        }

        public View getView() {
            return view;
        }

        public void handleSwipeGesture(float dX) {
            float height = nameTextView.getHeight();
            float maxAbsXDiff = nameTextView.getWidth() / 2f;
            float factor = interpolator.getInterpolation(Math.min(Math.abs(dX), maxAbsXDiff) / maxAbsXDiff);
            float diffX = factor * height;
            if (dX < 0) {
                diffX *= -1;
            }
            nameTextView.setTranslationX(diffX);
            if (dX < 0) {
                rightImage.setAlpha(factor);
                rightImage.setTranslationX((height - Math.abs(diffX)) / 2f);
            } else {
                leftImage.setAlpha(factor);
                leftImage.setTranslationX(Math.abs(height - diffX) / -2f);
            }
        }

        public void reset() {
            nameTextView.setTranslationX(0);
            rightImage.setAlpha(1f);
            rightImage.setTranslationX(0);
            leftImage.setAlpha(1f);
            leftImage.setTranslationX(0);
        }
    }
}
