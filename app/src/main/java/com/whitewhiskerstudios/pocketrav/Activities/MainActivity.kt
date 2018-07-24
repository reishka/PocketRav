package com.whitewhiskerstudios.pocketrav.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.os.ResultReceiver
import android.util.Log
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeIcons
import com.joanzapata.iconify.fonts.FontAwesomeModule
import com.squareup.picasso.Picasso
import com.whitewhiskerstudios.pocketrav.API.Models.User
import com.whitewhiskerstudios.pocketrav.Fragments.CardViewProject
import com.whitewhiskerstudios.pocketrav.Fragments.CardViewStash
import com.whitewhiskerstudios.pocketrav.Interfaces.PocketRavPrefs_
import com.whitewhiskerstudios.pocketrav.R
import com.whitewhiskerstudios.pocketrav.API.AccessTokenActivity_
import com.whitewhiskerstudios.pocketrav.Services.DownloadIntentService_
import com.whitewhiskerstudios.pocketrav.Utils.Constants

import org.androidannotations.annotations.EActivity
import org.androidannotations.annotations.sharedpreferences.Pref
import org.json.JSONObject

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

@EActivity
open class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    @Pref
    open lateinit var prefs: PocketRavPrefs_

    // Icons for the sidebar
    private var icon_projects: IconDrawable? = null;        private var icon_handspun: IconDrawable? = null
    private var icon_stash: IconDrawable? = null;           private var icon_queue: IconDrawable? = null
    private var icon_favorites: IconDrawable? = null;       private var icon_friends: IconDrawable? = null
    private var icon_groups: IconDrawable? = null;          private var icon_tools: IconDrawable? = null
    private var icon_library: IconDrawable? = null;         private var icon_messages: IconDrawable? = null
    private var icon_contributions: IconDrawable? = null;   private var icon_purchases: IconDrawable? = null
    private var icon_search_pattern: IconDrawable? = null;  private var icon_search_projects: IconDrawable? = null
    private var icon_search_yarn: IconDrawable? = null

    //internal var navigationView: NavigationView? = null

    private var downloadIntentResultReceiver: DownloadIntentResultReceiver? = null
    private var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        Iconify.with(FontAwesomeModule())


        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        downloadIntentResultReceiver = DownloadIntentResultReceiver(Handler())

        // Check to see if we already have an access token. If not, get one.
        val json:String? = prefs.accessToken().get()
        if ( json == null || json.isEmpty() )
                getAuthToken()
        else {
            setupUser()
        }
        initDrawerIcons()
    }


    override fun onBackPressed() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        val id = item.itemId
        when (id) {
            R.id.nav_project -> {
                val bundle = Bundle()
                bundle.putInt(Constants.FETCH_TYPE, Constants.FETCH_PROJECT_LIST)

                val cardViewProjectFragment = CardViewProject()
                cardViewProjectFragment.arguments = bundle

                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.frame_layout, cardViewProjectFragment)
                //fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit()
            }
            R.id.nav_handspun -> {}
            R.id.nav_stash -> {
                val bundle = Bundle()
                bundle.putInt(Constants.FETCH_TYPE, Constants.FETCH_STASH_LIST)

                val cardViewStashFragment = CardViewStash()
                cardViewStashFragment.arguments = bundle

                val fragmentManager = supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.frame_layout, cardViewStashFragment)
                //fragmentTransaction.addToBackStack(null);

                fragmentTransaction.commit()
            }
            R.id.nav_queue -> {}
            R.id.nav_favorites -> {}
            R.id.nav_friends -> {}
            R.id.nav_groups_events -> {}
            R.id.nav_tools -> {}
            R.id.nav_library -> {}
            R.id.nav_messages -> {}
            R.id.nav_contribute -> {}
            R.id.nav_purchases -> {}
            R.id.nav_search_pattern -> {}
            R.id.nav_search_yarn -> {}
            R.id.nav_search_project -> {}
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun initDrawerIcons() {

        // Set an icon in the ActionBar
        nav_view.menu.findItem(R.id.nav_project).icon = getIcons(NAV_PROJECTS)
        nav_view.menu.findItem(R.id.nav_handspun).icon = getIcons(NAV_HANDSPUN)
        nav_view.menu.findItem(R.id.nav_stash).icon = getIcons(NAV_STASH)
        nav_view.menu.findItem(R.id.nav_queue).icon = getIcons(NAV_QUEUE)
        nav_view.menu.findItem(R.id.nav_favorites).icon = getIcons(NAV_FAVOURITES)
        nav_view.menu.findItem(R.id.nav_friends).icon = getIcons(NAV_FRIENDS)
        nav_view.menu.findItem(R.id.nav_groups_events).icon = getIcons(NAV_GROUPS)
        nav_view.menu.findItem(R.id.nav_tools).icon = getIcons(NAV_TOOLS)
        nav_view.menu.findItem(R.id.nav_library).icon = getIcons(NAV_LIBRARY)
        nav_view.menu.findItem(R.id.nav_messages).icon = getIcons(NAV_MESSAGES)
        nav_view.menu.findItem(R.id.nav_contribute).icon = getIcons(NAV_CONTRIBUTIONS)
        nav_view.menu.findItem(R.id.nav_purchases).icon = getIcons(NAV_PURCHASES)
        nav_view.menu.findItem(R.id.nav_search_pattern).icon = getIcons(NAV_SEARCH_PATTERNS)
        nav_view.menu.findItem(R.id.nav_search_yarn).icon = getIcons(NAV_SEARCH_YARNS)
        nav_view.menu.findItem(R.id.nav_search_project).icon = getIcons(NAV_SEARCH_PROJECTS)

    }

    private fun getAuthToken() {

            val intent = Intent(this, AccessTokenActivity_::class.java)
            startActivityForResult(intent, Constants.AUTH_INTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == Constants.AUTH_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                setupUser()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "You did not authorize this app to use Ravelry. You are not logged in.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUser() {

        if (user != null)
        // We already have a user - downloaded from rav
            loadUserData()
        else {                  // try to load from prefs

            val json:String? = prefs.accessToken().get()
            if ( json == null || json.isEmpty() )
                getAuthToken()
            else {
                setupUser()
            }

            val userJson:String? = prefs.user().get()

            if (userJson == null || userJson.isEmpty()) {            // Don't have user & don't have a user in preferences -> download user & try again
                startDownloadIntentService(Constants.FETCH_USER)
            } else {
                try {
                    val mapper = ObjectMapper()
                    user = mapper.readValue<User>(userJson, User::class.java)
                    loadUserData()

                } catch (e: Exception) {
                    Log.d(TAG, "Could not load user data")  // Couldn't load data from prefs -> download user & try again
                    startDownloadIntentService(Constants.FETCH_USER)
                }

            }
        }
    }

    private fun loadUserData() {

        // Set up the user info in the menu header
        Picasso.with(this).load(user?.smallPhotoURL).into(nav_header_image)
        nav_header_main_username.text = user?.username
        nav_header_main_name.text = user?.firstName
    }

    private fun startDownloadIntentService(type: Int) {

        val intent = Intent(this, DownloadIntentService_::class.java)
        intent.putExtra(Constants.RECEIVER, downloadIntentResultReceiver)
        intent.putExtra(Constants.FETCH_TYPE, type)
        this.startService(intent)
    }

    private inner class DownloadIntentResultReceiver constructor(handler: Handler) : ResultReceiver(handler) {

        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {

            if (resultCode == Constants.SUCCESS_RESULT) {

                val type = resultData.getInt(Constants.FETCH_TYPE)
                val resultDataString = resultData.getString(Constants.RESULT_DATA_KEY)
                val mapper = ObjectMapper()

                when (type) {
                    Constants.FETCH_USER ->

                        try {
                            val jObject = JSONObject(resultDataString)
                            val s_user = jObject.get("user").toString()
                            user = mapper.readValue<User>(s_user, User::class.java)

                            if (user != null) {

                                val gson = Gson()
                                val jsonUser = gson.toJson(user)
                                prefs.user().put(jsonUser)

                                setupUser()
                            }

                        } catch (e: Exception) {

                            user = null
                            Log.e(TAG, "Could not get user info from data returned from Ravelry")
                        }

                    else -> Log.e(TAG, "How did we get here....?")
                }
            }
        }
    }

    private fun getIcons(icon: Int): IconDrawable? {

        when (icon) {
            NAV_PROJECTS -> {
                return icon_projects?:IconDrawable(this, FontAwesomeIcons.fa_th)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_HANDSPUN -> {
                return icon_handspun?:IconDrawable(this, FontAwesomeIcons.fa_hand_spock_o)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_STASH -> {
                return icon_stash?:IconDrawable(this, FontAwesomeIcons.fa_shopping_basket)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_QUEUE -> {
                return icon_queue?:IconDrawable(this, FontAwesomeIcons.fa_list_ol)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_FAVOURITES -> {
                return icon_favorites?:IconDrawable(this, FontAwesomeIcons.fa_heart)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_FRIENDS -> {
                return icon_friends?:IconDrawable(this, FontAwesomeIcons.fa_users)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_GROUPS -> {
                return icon_groups?:IconDrawable(this, FontAwesomeIcons.fa_comment)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_TOOLS -> {
                return icon_tools?:IconDrawable(this, FontAwesomeIcons.fa_cut)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_LIBRARY -> {
                return icon_library?:IconDrawable(this, FontAwesomeIcons.fa_archive)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_MESSAGES -> {
                return icon_messages?:IconDrawable(this, FontAwesomeIcons.fa_envelope)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_CONTRIBUTIONS -> {
                return icon_contributions?:IconDrawable(this, FontAwesomeIcons.fa_pencil)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_PURCHASES -> {
                return icon_purchases?:IconDrawable(this, FontAwesomeIcons.fa_dollar)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_SEARCH_PATTERNS -> {
                return icon_search_pattern?:IconDrawable(this, FontAwesomeIcons.fa_search)
                .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_SEARCH_YARNS -> {
                return icon_search_yarn?:IconDrawable(this, FontAwesomeIcons.fa_search)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }

            NAV_SEARCH_PROJECTS -> {
                return icon_search_projects?:IconDrawable(this, FontAwesomeIcons.fa_search)
                        .colorRes(R.color.nav_drawer_icons).actionBarSize()
            }
        }
        return null
    }

    companion object {

        const val NAV_PROJECTS = 1
        const val NAV_HANDSPUN = 2
        const val NAV_STASH = 3
        const val NAV_QUEUE = 4
        const val NAV_FAVOURITES = 5
        const val NAV_FRIENDS = 6
        const val NAV_GROUPS = 7
        const val NAV_TOOLS = 8
        const val NAV_LIBRARY = 9
        const val NAV_MESSAGES = 10
        const val NAV_CONTRIBUTIONS = 11
        const val NAV_PURCHASES = 12
        const val NAV_SEARCH_PATTERNS = 13
        const val NAV_SEARCH_YARNS = 14
        const val NAV_SEARCH_PROJECTS = 15

        private const val TAG = "MainActivity"
    }
}
