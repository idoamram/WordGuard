package com.io.wordguard.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.io.wordguard.R;
import com.io.wordguard.ui.util.AnimationUtils;
import com.io.wordguard.ui.util.RevealAnimationSetting;

public class CreateWordActivity extends AppCompatActivity {

    private static final String TAG = "CreateWordActivity";

    private static final String EXTRA_REVEAL_ANIM_SETTINGS = "extra_reveal_anim_settings";
    private FloatingActionButton doneFab;
    private View rootView;

    public static Intent getStartIntent(Context context, RevealAnimationSetting revealAnimationSetting) {
        return new Intent(context, CreateWordActivity.class)
                .putExtra(EXTRA_REVEAL_ANIM_SETTINGS, revealAnimationSetting);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_word);
        rootView = findViewById(R.id.rootView);
        AnimationUtils.registerCircularRevealAnimation(this, rootView,
                ((RevealAnimationSetting) getIntent().getExtras().getParcelable(EXTRA_REVEAL_ANIM_SETTINGS)),
                new AnimationUtils.AnimationFinishedListener() {
                    @Override
                    public void onAnimationFinished() {
                        Log.d(TAG, "onAnimationFinished() called");
                    }
                });

        doneFab = (FloatingActionButton) findViewById(R.id.done_fab);
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AnimationUtils.startCircularRevealExitAnimation(CreateWordActivity.this, rootView,
                RevealAnimationSetting.with(
                        (int) (doneFab.getX() + doneFab.getWidth() / 2),
                        (int) (doneFab.getY() + doneFab.getHeight() / 2),
                        rootView.getWidth(),
                        rootView.getHeight()), null);
        super.onBackPressed();
    }
}
