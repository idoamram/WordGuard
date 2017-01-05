package com.io.wordguard.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.io.wordguard.R;
import com.io.wordguard.db.Word;

public class ViewWordActivity extends AppCompatActivity {

    public static final String EXTRA_WORD = "extra_word";
    private Word mWord;
    private TextView mViewType, mViewDescription, mViewDeadline, mViewLocation,
            mViewContactName, mViewContactNumber, mViewContactEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_word);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mWord = getIntent().getParcelableExtra(EXTRA_WORD);

        mViewType = (TextView) findViewById(R.id.word_view_type_title);
        mViewDescription = (TextView) findViewById(R.id.word_view_description_title);
        mViewDeadline = (TextView) findViewById(R.id.word_view_deadline_title);
        mViewLocation = (TextView) findViewById(R.id.word_view_location_title);
        mViewContactName = (TextView) findViewById(R.id.word_view_contact_name_title);
        mViewContactNumber = (TextView) findViewById(R.id.word_view_contact_number_title);
        mViewContactEmail = (TextView) findViewById(R.id.word_view_contact_email_title);

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
}
