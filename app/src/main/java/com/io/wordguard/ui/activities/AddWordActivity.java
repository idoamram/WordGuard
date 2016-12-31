package com.io.wordguard.ui.activities;

import android.app.Activity;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.io.wordguard.R;
import com.io.wordguard.ui.transitions.FabTransform;
import com.io.wordguard.ui.transitions.MorphTransform;

public class AddWordActivity extends AppCompatActivity {
    private ViewGroup mContainer;
    private AutoCompleteTextView mDescription;
    private Button mAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        mContainer = (ViewGroup) findViewById(R.id.container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!FabTransform.setup(this, mContainer)) {
                MorphTransform.setup(this, mContainer,
                        ContextCompat.getColor(this, R.color.cards_and_dialogs_color),
                        getResources().getDimensionPixelSize(R.dimen.dialog_corners));
            }
        }

        mAdd = (Button) findViewById(R.id.add);

        mDescription = (AutoCompleteTextView) findViewById(R.id.description);
        mDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mAdd.setEnabled(isDescriptionValid());
            }
        });
    }

    public void dismiss(View view) {
        setResult(Activity.RESULT_CANCELED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
            overridePendingTransition(0, 0);
        }
    }

    public void addWord(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(mContainer);
        }
        Toast.makeText(this, mDescription.getText().toString(), Toast.LENGTH_LONG).show();
        dismiss(null);
    }

    private boolean isDescriptionValid() {
        return mDescription.length() > 0;
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }
}
