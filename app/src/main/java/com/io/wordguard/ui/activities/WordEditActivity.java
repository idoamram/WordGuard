package com.io.wordguard.ui.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.io.wordguard.Constants;
import com.io.wordguard.R;
import com.io.wordguard.db.ContentProvider;
import com.io.wordguard.db.Word;
import com.simplite.orm.interfaces.BackgroundTaskCallBack;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WordEditActivity extends AppCompatActivity {

    public static final String EXTRA_WORD = "extra_word";
    public static final String EXTRA_EDIT_MODE = "extra_edit_mode";

    public static final int EDIT_MODE_CREATE = 1;
    public static final int EDIT_MODE_SAVE = 2;

    private TextInputEditText mEditTitle, mEditDescription, mEditLocation,
            mEditContactName, mEditContactNumber, mEditContactMail;
    private Spinner mEditWordTypeSpinner;
    private TextView mEditDeadline;
    private Calendar mCalendar;
    private long mDeadlineLong;

    private Word mWord;
    private int mEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mEditWordTypeSpinner = (Spinner) findViewById(R.id.word_edit_type_spinner);
        mEditTitle = (TextInputEditText) findViewById(R.id.word_edit_title_title);
        mEditDescription = (TextInputEditText) findViewById(R.id.word_edit_description_title);
        mEditLocation = (TextInputEditText) findViewById(R.id.word_edit_location_title);
        mEditContactName = (TextInputEditText) findViewById(R.id.word_edit_contact_name_title);
        mEditContactNumber = (TextInputEditText) findViewById(R.id.word_edit_contact_number_title);
        mEditContactMail = (TextInputEditText) findViewById(R.id.word_edit_contact_email_title);
        mEditDeadline = (TextView) findViewById(R.id.word_edit_deadline);

        ArrayAdapter<CharSequence> wordTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.word_types, R.layout.spinner_item);
        wordTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEditWordTypeSpinner.setAdapter(wordTypeAdapter);

        mCalendar = Calendar.getInstance();

        findViewById(R.id.word_edit_deadline_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open dialog picker with today's date
                DatePickerDialog datePickerDialog = new DatePickerDialog(WordEditActivity.this,
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
                                mEditDeadline.setText(DateFormat.getDateFormat(
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

        mEditMode = getIntent().getIntExtra(EXTRA_EDIT_MODE, -1);
        mWord = getIntent().getParcelableExtra(EXTRA_WORD);

        if (mWord != null) {
            if (TextUtils.isEmpty(mWord.getTitle())) mEditTitle.setText(mWord.getTitle());
            mEditWordTypeSpinner.setSelection(mWord.getType());
        }
    }

    private boolean isFieldsFull() {
        return mEditTitle.length() > 0 || mEditDescription.length() > 0 || mEditLocation.length() > 0
                || mEditContactName.length() > 0 || mEditContactNumber.length() > 0 || mEditContactMail.length() > 0
                || mEditWordTypeSpinner.getSelectedItemPosition() != 0 || !mEditDeadline.getText().toString().isEmpty();
    }

    public void importContact(View view) {

    }

    public void addAttachment(View view) {

    }

    public void saveWord() {
        getDataFromFields();
        if (mEditMode == EDIT_MODE_CREATE) {
            mWord.createInBackground(this, true, new BackgroundTaskCallBack() {
                @Override
                public void onSuccess(String result, List<Object> data) {
                    Toast.makeText(WordEditActivity.this, "Success", Toast.LENGTH_LONG).show();
                    ContentProvider.getInstance().releaseAll();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(WordEditActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            });
        } else if (mEditMode == EDIT_MODE_SAVE) {
            mWord.saveInBackground(this, true, new BackgroundTaskCallBack() {
                @Override
                public void onSuccess(String result, List<Object> data) {
                    Toast.makeText(WordEditActivity.this, "Success", Toast.LENGTH_LONG).show();
                    ContentProvider.getInstance().releaseAll();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(WordEditActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void getDataFromFields() {

        mWord = new Word(this);

        if (!TextUtils.isEmpty(mEditTitle.getText()))
            mWord.setTitle(mEditTitle.getText().toString());

        mWord.setType(mEditWordTypeSpinner.getSelectedItemPosition());

        if (!TextUtils.isEmpty(mEditDescription.getText()))
            mWord.setDescription(mEditDescription.getText().toString());
        if (mDeadlineLong > 0)
            mWord.setDeadLine(new Date(mDeadlineLong));
        if (!TextUtils.isEmpty(mEditContactName.getText()))
            mWord.setContactName(mEditContactName.getText().toString());
        if (!TextUtils.isEmpty(mEditContactNumber.getText()))
            mWord.setContactPhoneNumber(mEditContactNumber.getText().toString());
        if (!TextUtils.isEmpty(mEditContactMail.getText()))
            mWord.setContactEmail(mEditContactMail.getText().toString());
    }

    @Override
    public void onBackPressed() {
        if (isFieldsFull()) {
            new ConfirmDiscardDialog().show(getSupportFragmentManager(),
                    Constants.DIALOG_FRAGMENT_CONFIRM_WORD_EDIT_DISCARD);
        } else {
            super.onBackPressed();
        }
    }

    public static class ConfirmDiscardDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.discard_dialog_message)
                    .setPositiveButton(R.string.discard_dialog_keep_editing,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    getDialog().dismiss();
                                }
                            }
                    )
                    .setNegativeButton(R.string.discard_dialog_discard,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    getActivity().finish();
                                }
                            }
                    )
                    .create();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_edit_menu, menu);

        menu.findItem(R.id.word_edit_menu_save).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                saveWord();
                return true;
            }
        });

        return true;
    }
}
