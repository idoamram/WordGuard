package com.io.wordguard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.io.wordguard.ui.util.ThemeUtils;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.updateTheme(this);
        super.onCreate(savedInstanceState);
    }
}