package com.io.wordguard.ui.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.io.wordguard.R;
import com.io.wordguard.db.Word;
import com.io.wordguard.ui.transitions.FabTransform;
import com.io.wordguard.ui.transitions.MorphTransform;
import java.util.Calendar;
import java.util.Date;

public class WordQuickAddActivity extends AppCompatActivity {
    private ViewGroup mContainer;
    private AutoCompleteTextView mTitle;
    private Button mAddBtn;
    private Calendar mCalendar;
    private TextView mDeadline;
    private Spinner mTypeSpinner;
    private long mDeadlineLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_quick_add);

        mContainer = (ViewGroup) findViewById(R.id.container);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!FabTransform.setup(this, mContainer)) {
                MorphTransform.setup(this, mContainer,
                        ContextCompat.getColor(this, R.color.cards_and_dialogs_color),
                        getResources().getDimensionPixelSize(R.dimen.dialog_corners));
            }
        }

        mTypeSpinner = (Spinner) findViewById(R.id.add_word_type_spinner);
        ArrayAdapter<CharSequence> wordTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.word_types, R.layout.spinner_item);
        wordTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(wordTypeAdapter);

        mAddBtn = (Button) findViewById(R.id.add_word_btn_add);
        Button btnMoreFields = (Button) findViewById(R.id.add_word_btn_more_fields);
        btnMoreFields.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Word word = new Word(WordQuickAddActivity.this);

                if (TextUtils.isEmpty(mTitle.getText())) word.setTitle(mTitle.getText().toString());
                if (mDeadlineLong > 0) word.setDeadLine(new Date(mDeadlineLong));
                word.setType(mTypeSpinner.getSelectedItemPosition());

                Intent intent = new Intent(WordQuickAddActivity.this, WordEditActivity.class);
                intent.putExtra(WordEditActivity.EXTRA_WORD, word);
                startActivity(intent);
                finish();
            }
        });

        mTitle = (AutoCompleteTextView) findViewById(R.id.word_quick_add_title);
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mAddBtn.setEnabled(isDescriptionValid());
            }
        });

        mCalendar = Calendar.getInstance();

        mDeadline = (TextView) findViewById(R.id.deadline);
        mDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open dialog picker with today's date
                DatePickerDialog datePickerDialog = new DatePickerDialog(WordQuickAddActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                // Get dates from dialog picker
                                mCalendar.set(Calendar.YEAR, year);
                                mCalendar.set(Calendar.MONTH, monthOfYear);
                                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                // Ignore the time
                                mCalendar.set(Calendar.HOUR_OF_DAY, 0);
                                mCalendar.set(Calendar.MINUTE, 0);
                                mCalendar.set(Calendar.SECOND, 0);
                                mCalendar.set(Calendar.MILLISECOND, 0);
                                // Update text
                                mDeadline.setText(DateFormat.getDateFormat(
                                        getApplicationContext()).format(mCalendar.getTime()));
                                // Save start date long
                                mDeadlineLong = mCalendar.getTimeInMillis();
                            }
                        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                // Set minimum date to now
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
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
        Toast.makeText(this, mTitle.getText().toString()
                + "\n" + mDeadlineLong, Toast.LENGTH_LONG).show();
        dismiss(null);
    }

    private boolean isDescriptionValid() {
        return mTitle.length() > 0;
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }
}
