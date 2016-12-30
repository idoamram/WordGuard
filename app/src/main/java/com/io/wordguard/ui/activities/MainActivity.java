package com.io.wordguard.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.io.wordguard.BaseActivity;
import com.io.wordguard.R;
import com.io.wordguard.ui.fragment.ActiveFragment;
import com.io.wordguard.ui.fragment.DoneFragment;
import com.io.wordguard.ui.fragment.TrashFragment;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private CharSequence mTitle;
    private static final String PREV_TITLE = "title";
    public static boolean needRecreate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState != null) {
            // Restore previous title
            mTitle = savedInstanceState.getCharSequence(PREV_TITLE);
            setTitle(mTitle);
        } else {
            // No data saved, select the default drawer item (active)
            onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_active));
            mNavigationView.setCheckedItem(R.id.nav_active);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(PREV_TITLE, mTitle);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRecreate) {
            needRecreate = false;
            recreate();
        }
    }

    long lastPress;

    @Override
    public void onBackPressed() {
        // On back key pressed
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            // If drawer is opened, close it
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Drawer is closed, check if current fragment is active fragment, and change to it if it's not
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (currentFragment instanceof ActiveFragment) {
                // Current fragment is active fragment, safe check before exiting the app,
                // back key should be pressed twice in a range of 3 seconds
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastPress > 3000) {
                    Toast.makeText(this, R.string.confirm_exit, Toast.LENGTH_SHORT).show();
                    lastPress = currentTime;
                } else {
                    super.onBackPressed();
                }
            } else {
                // Current fragment is not active fragment, change back to it before exit
                mNavigationView.setNavigationItemSelectedListener(this);
                onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.nav_active));
                mNavigationView.setCheckedItem(R.id.nav_active);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.m_search);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.m_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (id == R.id.nav_active) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ActiveFragment()).commit();
        } else if (id == R.id.nav_done) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new DoneFragment()).commit();
        } else if (id == R.id.nav_trash) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new TrashFragment()).commit();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_describe_problem) {
            startActivity(new Intent(this, DescribeProblemActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        // Set title only to fragments
        if (id != R.id.nav_settings && id != R.id.nav_describe_problem && id != R.id.nav_about) {
            setTitle(item.getTitle());
            mTitle = item.getTitle();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}