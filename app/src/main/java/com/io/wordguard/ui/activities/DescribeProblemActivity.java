package com.io.wordguard.ui.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.io.wordguard.BaseActivity;
import com.io.wordguard.BuildConfig;
import com.io.wordguard.R;
import com.io.wordguard.WordGuardApplication;
import com.io.wordguard.ui.util.GetFilePathFromUri;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DescribeProblemActivity extends BaseActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private static final int REQUEST_STORAGE = 0;
    private ImageView[] screenshots = new ImageView[3];
    private Uri[] uris = new Uri[3];
    private int PICK_IMAGE_REQUEST;
    private ArrayList<Uri> attachments = new ArrayList<>();
    private EditText mDescription;
    private CheckBox mLogsCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_describe_problem);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.mail_subject);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDescription = (EditText) findViewById(R.id.describe_problem_description);
        mLogsCheckBox = (CheckBox) findViewById(R.id.describe_problem_include_logs);

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.screenshots);
        linearLayout.removeAllViews();
        for (int i = 0; i < 3; i++) {
            final ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(R.drawable.ic_add_48dp);
            imageView.setBackgroundResource(R.drawable.inline_screenshot_placeholder);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                imageView.setForeground(getSelectedItemDrawable());
            }
            imageView.setOnClickListener(new screenshotClickListener(i));
            imageView.setOnLongClickListener(new screenshotClickListener(i));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            int margin = getResources().getDimensionPixelSize(R.dimen.medium_thumbnail_padding);
            layoutParams.bottomMargin = margin;
            layoutParams.rightMargin = margin;
            layoutParams.topMargin = margin;
            layoutParams.leftMargin = margin;
            linearLayout.addView(imageView, layoutParams);
            // change image view height after width is set
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    imageView.getLayoutParams().height = (imageView.getWidth() * 4) / 3;
                    imageView.requestLayout();
                }
            });
            // save image view for later use
            screenshots[i] = imageView;
        }

        if (savedInstanceState != null) {
            Parcelable[] savedScreenshots = savedInstanceState.getParcelableArray("screenshots");
            if (savedScreenshots == null) {
                throw new AssertionError();
            }
            int i = 0;
            while (i < savedScreenshots.length) {
                if (savedScreenshots[i] != null) {
                    attachScreenshot(i, (Uri) savedScreenshots[i]);
                }
                i += 1;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_us, menu);
        final MenuItem next = menu.findItem(R.id.next);
        next.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(next);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next:
                sendEmail();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendEmail() {
        int length = mDescription.getText().toString().trim().getBytes().length;
        if (length > 10) {
            if (mLogsCheckBox.isChecked()) {
                // Create debug info text file and set file name to current date and time
                Calendar now = Calendar.getInstance();
                String filename = String.format(Locale.getDefault(),
                        "wordguard_%02d%02d%04d_%02d%02d%02d.log",
                        now.get(Calendar.DAY_OF_MONTH), now.get(Calendar.MONTH) + 1,
                        now.get(Calendar.YEAR), now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
                File tempFile = new File(getBaseContext().getExternalCacheDir()
                        + File.separator + filename + ".txt");
                try {
                    FileWriter writer = new FileWriter(tempFile);
                    writer.write(debugInfo());
                    writer.close();
                    attachments.add(Uri.fromFile(tempFile));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            List<Intent> intentShareList = new ArrayList<>();
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setType("text/plain");
            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(shareIntent, 0);

            for (ResolveInfo resInfo : resolveInfoList) {
                String packageName = resInfo.activityInfo.packageName;

                if (packageName.contains("gm") ||
                        packageName.contains("email") ||
                        packageName.contains("fsck.k9") ||
                        packageName.contains("maildroid") ||
                        packageName.contains("hotmail") ||
                        packageName.contains("android.mail") ||
                        packageName.contains("com.baydin.boomerang") ||
                        packageName.contains("yandex.mail") ||
                        packageName.contains("com.google.android.apps.inbox") ||
                        packageName.contains("com.microsoft.office.outlook") ||
                        packageName.contains("com.asus.email")) {

                    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
                    emailIntent.setType("plain/text");
                    emailIntent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ido.movieditor@gmail.com"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject));
                    emailIntent.putExtra(Intent.EXTRA_TEXT, mDescription.getText());
                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachments);
                    intentShareList.add(emailIntent);
                }
            }
            if (intentShareList.isEmpty()) {
                Toast.makeText(DescribeProblemActivity.this, R.string.no_supported_mail_apps, Toast.LENGTH_LONG).show();
//                        finish();
            } else {
                Intent chooserIntent = Intent.createChooser(intentShareList.remove(0), getResources().getText(R.string.contact_support_via));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentShareList.toArray(new Parcelable[intentShareList.size()]));
                startActivity(chooserIntent);
                finish();
            }
        } else if (length == 0) {
            Toast makeText = Toast.makeText(DescribeProblemActivity.this,
                    R.string.describe_problem_description, Toast.LENGTH_LONG);
            makeText.setGravity(Gravity.CENTER, 0, 0);
            makeText.show();
        } else {
            Toast makeText = Toast.makeText(DescribeProblemActivity.this,
                    R.string.describe_problem_description_further, Toast.LENGTH_LONG);
            makeText.setGravity(Gravity.CENTER, 0, 0);
            makeText.show();
        }
    }


    private class screenshotClickListener implements View.OnClickListener, View.OnLongClickListener {

        int position;

        screenshotClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            PICK_IMAGE_REQUEST = position;
            // Check for storage permission
            if (ActivityCompat.checkSelfPermission(DescribeProblemActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission();
            } else {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (uris[position] != null && attachments.contains(uris[position])) {
                attachments.remove(uris[position]);
                uris[position] = null;
                screenshots[position].setImageResource(R.drawable.ic_add_48dp);
                screenshots[position].setScaleType(ImageView.ScaleType.CENTER);
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.screenshot_removed, Snackbar.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        }
    }

    private void attachScreenshot(int position, Uri uri) {
        String filePath = GetFilePathFromUri.getPath(this, uri);
        if (filePath != null) {
            if (uris[position] != null && attachments.contains(uris[position]))
                attachments.remove(uris[position]);
            uris[position] = Uri.fromFile(new File(filePath));
            attachments.add(uris[position]);
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            screenshots[position].setScaleType(ImageView.ScaleType.CENTER_CROP);
            screenshots[position].setImageBitmap(bitmap);
        } else {
            Toast.makeText(DescribeProblemActivity.this, getString(R.string.error_load_image), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (requestCode == 0) {
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    attachScreenshot(requestCode, data.getData());
                } else if (requestCode == 1) {
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    attachScreenshot(requestCode, data.getData());
                } else {
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    attachScreenshot(requestCode, data.getData());
                }
            }
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("screenshots", uris);
    }

    public Drawable getSelectedItemDrawable() {
        int[] attrs = new int[]{android.R.attr.selectableItemBackground};
        TypedArray typedArray = obtainStyledAttributes(attrs);
        Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();
        return drawable;
    }

    public String debugInfo() {
        // Get debug info from the device for error report email and save it as string
        String debugInfo = "--Support Info--";
        debugInfo += "\n Version: " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")";
        debugInfo += "\n Manufacturer: " + Build.MANUFACTURER;
        debugInfo += "\n Model: " + Build.MODEL + " (" + Build.DEVICE + ")";
        debugInfo += "\n Locale: " + Locale.getDefault().toString();
        debugInfo += "\n OS: " + Build.VERSION.RELEASE + " (" + android.os.Build.VERSION.SDK_INT + ")";
        debugInfo += "\n Rooted: " + (WordGuardApplication.isRooted() ? "yes" : "no");
        File[] filesDirs = ContextCompat.getExternalFilesDirs(this, null);
        if (filesDirs[0] != null) {
            long freeBytesInternal = new StatFs(filesDirs[0].getPath()).getAvailableBytes();
            debugInfo += "\n Free Space Built-In: " + freeBytesInternal + " (" + Formatter.formatFileSize(this, freeBytesInternal) + ")";
        } else {
            debugInfo += "\n Free Space Built-In: Unavailable";
        }
        if (filesDirs.length > 1) {
            long freeBytesExternal = new StatFs(filesDirs[1].getPath()).getAvailableBytes();
            debugInfo += "\n Free Space Removable: " + freeBytesExternal + " (" + Formatter.formatFileSize(this, freeBytesExternal) + ")";
        } else {
            debugInfo += "\n Free Space Removable: Unavailable";
        }
        float density = getResources().getDisplayMetrics().density;
        String densityName = "unknown";
        if (density == 4.0) densityName = "xxxhdpi";
        if (density == 3.0) densityName = "xxhdpi";
        if (density == 2.0) densityName = "xhdpi";
        if (density == 1.5) densityName = "hdpi";
        if (density == 1.0) densityName = "mdpi";
        if (density == 0.75) densityName = "ldpi";
        debugInfo += "\n Screen Resolution: " + getResources().getDisplayMetrics().heightPixels + "x" +
                getResources().getDisplayMetrics().widthPixels + " (" + densityName + ")";
        debugInfo += "\n Target: " + BuildConfig.BUILD_TYPE;
        debugInfo += "\n Distribution: " + (verifyInstallerId() ? "play" : "apk");
        return debugInfo;
    }

    boolean verifyInstallerId() {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        // The package name of the app that has installed your app
        final String installer = getPackageManager().getInstallerPackageName(getPackageName());
        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(DescribeProblemActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_STORAGE);
                        }
                    })
                    .show();
        } else {
            // Storage permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}