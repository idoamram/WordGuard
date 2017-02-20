package com.io.wordguard.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.io.wordguard.BaseActivity;
import com.io.wordguard.R;
import com.io.wordguard.ui.fragments.WordListFragment;
import com.io.wordguard.ui.util.RevealAnimationSetting;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private CharSequence mTitle;
    private static final String PREV_TITLE = "title";
    public static boolean needRecreate = false;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            //TODO No data saved, select the default drawer item (( ? ))
        }

        final View contentRootView = findViewById(R.id.contentRootView);
        final FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.add_fab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RevealAnimationSetting revealAnimationSetting = RevealAnimationSetting.with(
                        (int) (addFab.getX() + addFab.getWidth() / 2),
                        (int) (addFab.getY() + addFab.getHeight() / 2),
                        contentRootView.getWidth(),
                        contentRootView.getHeight());
                Intent startIntent = CreateWordActivity.getStartIntent(MainActivity.this, revealAnimationSetting);
                String transitionName = MainActivity.this.getString(R.string.transition_create_word_fab);

                // Define the view that the animation will start from
                ActivityOptionsCompat options =

                        ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                                addFab,   // Starting view
                                transitionName    // The String
                        );
                ActivityCompat.startActivity(MainActivity.this, startIntent, options.toBundle());
            }
        });
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new WordListFragment()).commit();
                return true;
            }
        });
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
            //TODO Drawer is closed, check if current fragment is (( ? )) fragment, and change to it if it's not
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPress > 3000) {
                Toast.makeText(this, R.string.confirm_exit, Toast.LENGTH_SHORT).show();
                lastPress = currentTime;
            } else {
                super.onBackPressed();
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

        if (id == R.id.nav_profile) {
            //TODO: Open Profile Activity
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_describe_problem) {
            startActivity(new Intent(this, DescribeProblemActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}