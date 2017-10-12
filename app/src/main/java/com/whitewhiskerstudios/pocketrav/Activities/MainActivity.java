package com.whitewhiskerstudios.pocketrav.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.ResultReceiver;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.whitewhiskerstudios.pocketrav.Fragments.CardView;
import com.whitewhiskerstudios.pocketrav.Interfaces.PocketRavPrefs_;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.API.AccessToken_;
import com.whitewhiskerstudios.pocketrav.Services.DownloadIntentService_;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.androidannotations.annotations.sharedpreferences.Pref;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    @Pref
    PocketRavPrefs_ prefs;

    IconDrawable icon_projects,         icon_handspun,          icon_stash,             icon_queue,
                icon_favorites,         icon_friends,           icon_groups,            icon_tools,
                icon_library,           icon_messages,          icon_contributions,     icon_purchases,
                icon_search_pattern,    icon_search_projects,   icon_search_yarn        = null;

    NavigationView navigationView;

    public static final int NAV_PROJECTS = 1;
    public static final int NAV_HANDSPUN = 2;
    public static final int NAV_STASH = 3;
    public static final int NAV_QUEUE = 4;
    public static final int NAV_FAVOURITES = 5;
    public static final int NAV_FRIENDS = 6;
    public static final int NAV_GROUPS = 7;
    public static final int NAV_TOOLS = 8;
    public static final int NAV_LIBRARY = 9;
    public static final int NAV_MESSAGES = 10;
    public static final int NAV_CONTRIBUTIONS = 11;
    public static final int NAV_PURCHASES = 12;
    public static final int NAV_SEARCH_PATTERNS = 13;
    public static final int NAV_SEARCH_YARNS = 14;
    public static final int NAV_SEARCH_PROJECTS = 15;

    private DownloadIntentResultReceiver downloadIntentResultReceiver;


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

        Iconify.with(new FontAwesomeModule());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        downloadIntentResultReceiver = new DownloadIntentResultReceiver(new Handler());

        getAuthToken();
        setupUser();
        initDrawerIcons();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_project) {

            CardView cardViewFragment = new CardView();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frame_layout, cardViewFragment );
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_handspun) {

        } else if (id == R.id.nav_stash) {

        } else if (id == R.id.nav_queue) {

        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_friends) {

        } else if (id == R.id.nav_groups_events) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_library) {

        } else if (id == R.id.nav_messages) {

        } else if (id == R.id.nav_contribute) {

        } else if (id == R.id.nav_purchases) {

        } else if (id == R.id.nav_search_pattern) {

        } else if (id == R.id.nav_search_yarn) {

        } else if (id == R.id.nav_search_project) {


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initDrawerIcons(){

        // Set an icon in the ActionBar
        Menu menu = navigationView.getMenu();

        menu.findItem(R.id.nav_project).setIcon(getIcons(NAV_PROJECTS));
        menu.findItem(R.id.nav_handspun).setIcon(getIcons(NAV_HANDSPUN));
        menu.findItem(R.id.nav_stash).setIcon(getIcons(NAV_STASH));
        menu.findItem(R.id.nav_queue).setIcon(getIcons(NAV_QUEUE));
        menu.findItem(R.id.nav_favorites).setIcon(getIcons(NAV_FAVOURITES));
        menu.findItem(R.id.nav_friends).setIcon(getIcons(NAV_FRIENDS));
        menu.findItem(R.id.nav_groups_events).setIcon(getIcons(NAV_GROUPS));
        menu.findItem(R.id.nav_tools).setIcon(getIcons(NAV_TOOLS));
        menu.findItem(R.id.nav_library).setIcon(getIcons(NAV_LIBRARY));
        menu.findItem(R.id.nav_messages).setIcon(getIcons(NAV_MESSAGES));
        menu.findItem(R.id.nav_contribute).setIcon(getIcons(NAV_CONTRIBUTIONS));
        menu.findItem(R.id.nav_purchases).setIcon(getIcons(NAV_PURCHASES));
        menu.findItem(R.id.nav_search_pattern).setIcon(getIcons(NAV_SEARCH_PATTERNS));
        menu.findItem(R.id.nav_search_yarn).setIcon(getIcons(NAV_SEARCH_YARNS));
        menu.findItem(R.id.nav_search_project).setIcon(getIcons(NAV_SEARCH_PROJECTS));

    }

    private void getAuthToken(){

        String json = prefs.accessToken().get();

        if (json == null){
            Intent intent = new Intent(this, AccessToken_.class);
            startActivity(intent);
        }
    }

    private void setupUser(){
        startDownloadIntentService(Constants.FETCH_USER);
    }

    private void startDownloadIntentService(int type){

        Intent intent = new Intent(this, DownloadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, downloadIntentResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE, type);
        this.startService(intent);
    }

    private class DownloadIntentResultReceiver extends ResultReceiver {
        private DownloadIntentResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT){
            }
        }
    }

    private IconDrawable getIcons(int icon) {

        switch (icon) {
            case NAV_PROJECTS:
                if (icon_projects == null) {
                    icon_projects = new IconDrawable(this, FontAwesomeIcons.fa_th)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_projects;

            case NAV_HANDSPUN:
                if (icon_handspun == null) {
                    icon_handspun = new IconDrawable(this, FontAwesomeIcons.fa_hand_spock_o)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_handspun;

            case NAV_STASH:
                if (icon_stash == null) {
                    icon_stash = new IconDrawable(this, FontAwesomeIcons.fa_shopping_basket)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_stash;

            case NAV_QUEUE:
                if (icon_queue == null) {
                    icon_queue = new IconDrawable(this, FontAwesomeIcons.fa_list_ol )
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_queue;

            case NAV_FAVOURITES:
                if (icon_favorites == null) {
                    icon_favorites = new IconDrawable(this, FontAwesomeIcons.fa_heart)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_favorites;

            case NAV_FRIENDS:
                if (icon_friends == null) {
                    icon_friends = new IconDrawable(this, FontAwesomeIcons.fa_users )
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_friends;

            case NAV_GROUPS:
                if (icon_groups == null) {
                    icon_groups = new IconDrawable(this, FontAwesomeIcons.fa_comment)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_groups;

            case NAV_TOOLS:
                if (icon_tools == null) {
                    icon_tools = new IconDrawable(this, FontAwesomeIcons.fa_cut)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_tools;

            case NAV_LIBRARY:
                if (icon_library == null) {
                    icon_library = new IconDrawable(this, FontAwesomeIcons.fa_archive)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_library;

            case NAV_MESSAGES:
                if (icon_messages == null) {
                    icon_messages = new IconDrawable(this, FontAwesomeIcons.fa_envelope)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_messages;

            case NAV_CONTRIBUTIONS:
                if (icon_contributions == null) {
                    icon_contributions = new IconDrawable(this, FontAwesomeIcons.fa_pencil)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_contributions;

            case NAV_PURCHASES:
                if (icon_purchases == null) {
                    icon_purchases = new IconDrawable(this, FontAwesomeIcons.fa_dollar)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_purchases;

            case NAV_SEARCH_PATTERNS:
                if (icon_search_pattern== null) {
                    icon_search_pattern = new IconDrawable(this, FontAwesomeIcons.fa_search)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_search_pattern;

            case NAV_SEARCH_YARNS:
                if (icon_search_yarn == null) {
                    icon_search_yarn = new IconDrawable(this, FontAwesomeIcons.fa_search)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_search_yarn;

            case NAV_SEARCH_PROJECTS:
                if (icon_search_projects == null) {
                    icon_search_projects = new IconDrawable(this, FontAwesomeIcons.fa_search)
                            .colorRes(R.color.nav_drawer_icons).actionBarSize();
                }
                return icon_search_projects;

        }
        return null;
    }
}

