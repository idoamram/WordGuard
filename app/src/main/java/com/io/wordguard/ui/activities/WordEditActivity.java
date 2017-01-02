package com.io.wordguard.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.io.wordguard.R;

public class WordEditActivity extends AppCompatActivity {

    public static final String EXTRA_WORD = "extra_word";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_edit);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_edit_menu, menu);

        menu.findItem(R.id.word_edit_menu_save).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                finish();
                return true;
            }
        });

        return true;
    }
}
