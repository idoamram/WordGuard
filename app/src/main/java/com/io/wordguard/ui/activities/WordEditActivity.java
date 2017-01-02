package com.io.wordguard.ui.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.io.wordguard.Constants;
import com.io.wordguard.R;

import java.util.Calendar;

public class WordEditActivity extends AppCompatActivity {

    public static final String EXTRA_WORD = "extra_word";
    private EditText mEditTitle, mEditDescription, mEditLocation,
            mEditContactName, mEditContactNumber, mEditContactMail;
    private Spinner mEditWordTypeSpinner;
    private TextView mEditDeadline;
    private Calendar mCalendar;
    private long mDeadlineLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Edit word");
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
        mEditTitle = (EditText) findViewById(R.id.word_edit_title_title);
        mEditDescription = (EditText) findViewById(R.id.word_edit_description_title);
        mEditLocation = (EditText) findViewById(R.id.word_edit_location_title);
        mEditContactName = (EditText) findViewById(R.id.word_edit_contact_name_title);
        mEditContactNumber = (EditText) findViewById(R.id.word_edit_contact_number_title);
        mEditContactMail = (EditText) findViewById(R.id.word_edit_contact_email_title);
        mEditDeadline = (TextView) findViewById(R.id.word_edit_deadline);

        ArrayAdapter<CharSequence> wordTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.word_types, R.layout.spinner_item);
        wordTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEditWordTypeSpinner.setAdapter(wordTypeAdapter);

        mCalendar = Calendar.getInstance();

        mEditDeadline.setOnClickListener(new View.OnClickListener() {
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
    }

    private boolean isFieldsFull() {
        return mEditTitle.length() > 0 || mEditDescription.length() > 0 || mEditLocation.length() > 0
                || mEditContactName.length() > 0 || mEditContactNumber.length() > 0 || mEditContactMail.length() > 0
                || mEditWordTypeSpinner.getSelectedItemPosition() != 0 || !mEditDeadline.getText().toString().isEmpty();
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
                finish();
                return true;
            }
        });

        return true;
    }
}
