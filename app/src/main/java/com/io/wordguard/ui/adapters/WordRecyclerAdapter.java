package com.io.wordguard.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.io.wordguard.R;
import com.io.wordguard.word.Word;
import com.io.wordguard.ui.activities.ViewWordActivity;
import com.io.wordguard.ui.util.DateHelper;

import java.util.ArrayList;

public class WordRecyclerAdapter extends RecyclerView.Adapter<WordRecyclerAdapter.WordHolder> {

    ArrayList<Word> mWords;
    private Context mContext;

    public WordRecyclerAdapter(Context context, ArrayList<Word> mWords) {
        this.mContext = context;
        this.mWords = mWords;
    }

    @Override
    public WordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_recycler_item, parent, false);
        return new WordHolder(view);
    }

    @Override
    public void onBindViewHolder(final WordHolder holder, int position) {
        Word word = mWords.get(position);

        holder.title.setText(word.getTitle());
        if (word.getDeadLine() != null) {
            holder.deadLine.setVisibility(View.VISIBLE);
            holder.deadLine.setText(DateHelper.dateToString(word.getDeadLine(), DateHelper.DATE_FORMAT_ONLY_TIME));
        } else holder.deadLine.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(word.getContactName())) {
            holder.contactContainer.setVisibility(View.VISIBLE);
            holder.contactDetailIcon.setImageResource(R.drawable.ic_person);
            holder.contactDetail.setText(word.getContactName());
        }
        else if (!TextUtils.isEmpty(word.getContactPhoneNumber())) {
            holder.contactDetail.setText(word.getContactPhoneNumber());
            holder.contactContainer.setVisibility(View.VISIBLE);
            holder.contactDetailIcon.setImageResource(R.drawable.ic_phone);
        }
        else if (!TextUtils.isEmpty(word.getContactPhoneNumber())) {
            holder.contactDetail.setText(word.getContactPhoneNumber());
            holder.contactContainer.setVisibility(View.VISIBLE);
            holder.contactDetailIcon.setImageResource(R.drawable.ic_email);
        } else holder.contactContainer.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(word.getLocationAddress())) {
            holder.locationContainer.setVisibility(View.VISIBLE);
            holder.location.setText(word.getLocationAddress());
        } else holder.locationContainer.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewWordActivity.class);
                intent.putExtra(ViewWordActivity.EXTRA_WORD, mWords.get(holder.getAdapterPosition()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mWords.size();
    }

    public void addItem(Word word) {
        mWords.add(word);
        notifyItemInserted(mWords.size());
    }

    public void removeItem(int position) {
        mWords.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mWords.size());
    }

    public void setData(ArrayList<Word> data) {
        mWords = data;
        notifyDataSetChanged();
    }

    public Word getWordAt(int position) {return mWords.get(position);}

    class WordHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView deadLine;
        TextView location;
        TextView contactDetail;
        ImageView contactDetailIcon;
        LinearLayout locationContainer;
        LinearLayout contactContainer;

        public WordHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.word_recycler_item_title);
            deadLine = (TextView) itemView.findViewById(R.id.word_recycler_item_deadline);
            location = (TextView) itemView.findViewById(R.id.word_recycler_item_location);
            contactDetail = (TextView) itemView.findViewById(R.id.word_recycler_item_contact_detail);
            contactDetailIcon = (ImageView) itemView.findViewById(R.id.word_recycler_item_contact_icon);
            locationContainer = (LinearLayout) itemView.findViewById(R.id.word_recycler_item_location_container);
            contactContainer = (LinearLayout) itemView.findViewById(R.id.word_recycler_item_contact_container);
        }
    }
}
