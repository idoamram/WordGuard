package com.io.wordguard.ui.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.io.wordguard.R;
import com.io.wordguard.db.Word;

public class ViewWordActivity extends AppCompatActivity {

    public static final String EXTRA_WORD = "extra_word";
    private Word mWord;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView mViewType, mViewDescription, mViewDeadline, mViewLocation,
            mViewContactName, mViewContactNumber, mViewContactEmail;
    private ImageView mToolbarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_word);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWord = getIntent().getParcelableExtra(EXTRA_WORD);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mToolbarImage = (ImageView) findViewById(R.id.toolbar_image);

        mViewType = (TextView) findViewById(R.id.word_view_type_title);
        mViewDescription = (TextView) findViewById(R.id.word_view_description_title);
        mViewDeadline = (TextView) findViewById(R.id.word_view_deadline_title);
        mViewLocation = (TextView) findViewById(R.id.word_view_location_title);
        mViewContactName = (TextView) findViewById(R.id.word_view_contact_name_title);
        mViewContactNumber = (TextView) findViewById(R.id.word_view_contact_number_title);
        mViewContactEmail = (TextView) findViewById(R.id.word_view_contact_email_title);

        if (mWord != null) {
            mViewType.setText(mWord.getType() == 0 ? getString(
                    R.string.private_words) : getString(R.string.public_words));
            if (mWord.getTitle() != null) {
                mCollapsingToolbarLayout.setTitle(mWord.getTitle());
            }
            if (mWord.getDescription() != null) {
                mViewDescription.setText(mWord.getDescription());
            }
            if (mWord.getDeadLine() > 0) {
                mViewDeadline.setText(DateFormat.getDateFormat(
                        getApplicationContext()).format(mWord.getDeadLine()));
            }
            if (mWord.getLocationAddress() != null) {
                mViewLocation.setText(mWord.getLocationAddress());
            }
            if (mWord.getLocationLatitude() != 0 && mWord.getLocationLongitude() != 0) {
                Glide.with(this).load(getMapImageFromCoordinates()).into(mToolbarImage);
                findViewById(R.id.scrim).setVisibility(View.VISIBLE);
            }
            if (mWord.getContactName() != null) {
                mViewContactName.setText(mWord.getContactName());
            }
            if (mWord.getContactPhoneNumber() != null) {
                mViewContactNumber.setText(mWord.getContactPhoneNumber());
            }
            if (mWord.getContactEmail() != null) {
                mViewContactEmail.setText(mWord.getContactEmail());
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewWordActivity.this, WordEditActivity.class);
                intent.putExtra(WordEditActivity.EXTRA_EDIT_MODE, WordEditActivity.EDIT_MODE_UPDATE);
                intent.putExtra(WordEditActivity.EXTRA_WORD, mWord);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_view_menu, menu);

        menu.findItem(R.id.word_view_menu_delete).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                finish();
                return true;
            }
        });
        return true;
    }

    private String getMapImageFromCoordinates() {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" +
                mWord.getLocationLatitude() + "," + mWord.getLocationLongitude() +
                "&markers=color:red%7C" + mWord.getLocationLatitude() + "," + mWord.getLocationLongitude() +
                "&zoom=15&size=600x300&key=" + getString(R.string.google_maps_key);
    }
}
