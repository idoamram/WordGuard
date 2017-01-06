package com.io.wordguard.ui.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.io.wordguard.Constants;
import com.io.wordguard.R;
import com.io.wordguard.db.ContentProvider;
import com.io.wordguard.word.Word;
import com.io.wordguard.db.simplite.interfaces.CRUDCallback;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WordEditActivity extends AppCompatActivity {

    public static final String EXTRA_WORD = "extra_word";
    public static final String EXTRA_EDIT_MODE = "extra_edit_mode";

    public static final int EDIT_MODE_CREATE = 1;
    public static final int EDIT_MODE_UPDATE = 2;

    private static final int REQUEST_PICK_CONTACT = 123;
    private static final int REQUEST_READ_CONTACTS_PERMISSION = 111;

    private ProgressBar mLocationProgressBar;
    private ImageView mLocationSearchBtn, mLocationMapPreview;
    private TextInputEditText mEditTitle, mEditDescription, mEditLocation,
            mEditContactName, mEditContactPhoneNumber, mEditContactMail;
    private Spinner mEditWordTypeSpinner;
    private TextView mEditDeadline;
    private Calendar mCalendar;
    private long mDeadlineLong;
    private double mLocationLatitude, mLocationLongitude;

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
        mEditContactPhoneNumber = (TextInputEditText) findViewById(R.id.word_edit_contact_number_title);
        mEditContactMail = (TextInputEditText) findViewById(R.id.word_edit_contact_email_title);
        mEditDeadline = (TextView) findViewById(R.id.word_edit_deadline);
        mLocationProgressBar = (ProgressBar) findViewById(R.id.word_edit_location_progressBar);
        mLocationSearchBtn = (ImageView) findViewById(R.id.word_edit_location_searchIcon);
        mLocationMapPreview = (ImageView) findViewById(R.id.word_edit_map_image_preview);
        mLocationSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchForAddressLocation(mEditLocation.getText().toString());
            }
        });

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
            if (!TextUtils.isEmpty(mWord.getTitle())) mEditTitle.setText(mWord.getTitle());
            mEditWordTypeSpinner.setSelection(mWord.getType());
        }
    }

    private void searchForAddressLocation(final String address) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                switchViewVisibility(View.VISIBLE, View.INVISIBLE);
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> results = geocoder.getFromLocationName(address, 1);
                    if (results != null && results.size() > 0) {
                        results.get(0).getLongitude();

                        if (mWord == null) mWord = new Word(getApplicationContext());
                        mLocationLatitude = results.get(0).getLatitude();
                        mLocationLongitude = results.get(0).getLongitude();
                        return true;
                    } else return false;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean isSucceeded) {
                super.onPostExecute(isSucceeded);
                switchViewVisibility(View.INVISIBLE, View.VISIBLE);

                if (isSucceeded) {
                    Glide.with(WordEditActivity.this).load(getMapImageFromCoordinates()).into(mLocationMapPreview);
                    mLocationMapPreview.setVisibility(View.VISIBLE);
                }

                if (!isSucceeded) {
                    new AlertDialog.Builder(WordEditActivity.this)
                            .setMessage("Location not found")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                    mLocationMapPreview.setVisibility(View.GONE);
                }
            }

            private void switchViewVisibility(int progressBarVisibility, int searchBtnVisibility) {
                mLocationSearchBtn.setVisibility(searchBtnVisibility);
                mLocationProgressBar.setVisibility(progressBarVisibility);
            }
        }.execute();
    }

    private boolean isFieldsFull() {
        return mEditTitle.length() > 0 || mEditDescription.length() > 0 || mEditLocation.length() > 0
                || mEditContactName.length() > 0 || mEditContactPhoneNumber.length() > 0 || mEditContactMail.length() > 0
                || mEditWordTypeSpinner.getSelectedItemPosition() != 0 || !mEditDeadline.getText().toString().isEmpty();
    }

    public void importContact(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheck;
            permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // User may have declined earlier, ask Android if we should show him a reason
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                    // show an explanation to the user
                    // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
                } else {
                    // request the permission
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS_PERMISSION);
                }
            } else {
                // got permission use it
                startPickFromContacts();
            }
        } else {
            startPickFromContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, do your work....
                    startPickFromContacts();
                } else {
                    // permission denied
                    // Disable the functionality that depends on this permission.
                }
            }
        }
    }

    private String getMapImageFromCoordinates() {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" +
                mLocationLatitude + "," + mLocationLongitude + "&markers=color:red%7C" +
                mLocationLatitude + "," + mLocationLongitude + "&zoom=15&size=600x300&key=" +
                 getString(R.string.google_maps_key);
    }

    private void startPickFromContacts() {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI), REQUEST_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case REQUEST_PICK_CONTACT:
                    getDataFromPickedContact(data);
                    break;
            }
        } else {
            Log.e(Constants.LOG_TAG, "Failed to pick contact");
        }
    }

    private void getDataFromPickedContact(Intent data) {
        // *** Get contact name ***
        String contactID = null;
        String contactName;
        String contactPhoneNumber;
        String contactEmail;

        // querying contact data store
        Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // DISPLAY_NAME = The display name for the contact.
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            cursor.close();
            if (!TextUtils.isEmpty(contactName)) {
                mEditContactName.setText(contactName);
            }
        }

        // Getting contacts ID
        Cursor cursorID = getContentResolver().query(data.getData(),
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID != null && cursorID.moveToFirst()) {
            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            cursorID.close();
        }

        // *** Get contact Phone number ***
        // Get contact phone number using the contact ID
        if (!TextUtils.isEmpty(contactID)) {
            Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                    new String[]{contactID},
                    null);

            if (cursorPhone != null && cursorPhone.moveToFirst()) {
                contactPhoneNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                cursorPhone.close();
                if (!TextUtils.isEmpty(contactPhoneNumber)) {
                    mEditContactPhoneNumber.setText(contactPhoneNumber);
                }
            }
        }

        // *** Get contact Email***
        // Get contact email using the contact ID
        if (!TextUtils.isEmpty(contactID)) {
            Cursor cursorEmail = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS},
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{contactID},
                    null);

            if (cursorEmail != null && cursorEmail.moveToFirst()) {
                contactEmail = cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                cursorEmail.close();
                if (!TextUtils.isEmpty(contactEmail)) {
                    mEditContactMail.setText(contactEmail);
                }
            }
        }
    }

    public void addAttachment(View view) {

    }

    public void saveWord() {
        getDataFromFields();
        if (mEditMode == EDIT_MODE_CREATE) {
            mWord.createInBackground(this, true, new CRUDCallback() {
                @Override
                public void onFinish(Object extra, Exception e) {
                    if (e == null) {
                        Toast.makeText(WordEditActivity.this, "Success ID: " + extra, Toast.LENGTH_LONG).show();
                        ContentProvider.getInstance().releaseAll();
                    } else Toast.makeText(WordEditActivity.this, "Error", Toast.LENGTH_LONG).show();
                }
            });
        } else if (mEditMode == EDIT_MODE_UPDATE) {
            mWord.saveInBackground(this, true, new CRUDCallback() {
                @Override
                public void onFinish(Object extra, Exception e) {
                    if (e == null) {
                        Toast.makeText(WordEditActivity.this, "Success", Toast.LENGTH_LONG).show();
                        ContentProvider.getInstance().releaseAll();
                    } else Toast.makeText(WordEditActivity.this, "Error", Toast.LENGTH_LONG).show();
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
        if (!TextUtils.isEmpty(mEditLocation.getText()))
            mWord.setLocationAddress(mEditLocation.getText().toString());
        if (mLocationLongitude != 0)
            mWord.setLocationLongitude(mLocationLongitude);
        if (mLocationLatitude != 0)
            mWord.setLocationLatitude(mLocationLatitude);
        if (!TextUtils.isEmpty(mEditContactName.getText()))
            mWord.setContactName(mEditContactName.getText().toString());
        if (!TextUtils.isEmpty(mEditContactPhoneNumber.getText()))
            mWord.setContactPhoneNumber(mEditContactPhoneNumber.getText().toString());
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
