package com.whitewhiskerstudios.pocketrav.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.whitewhiskerstudios.pocketrav.API.Models.Stash;
import com.whitewhiskerstudios.pocketrav.API.Models.Photo;
import com.whitewhiskerstudios.pocketrav.Adapters.ViewPagerAdapter;
import com.whitewhiskerstudios.pocketrav.Fragments.StashInfo;
import com.whitewhiskerstudios.pocketrav.Fragments.StashNotes;
import com.whitewhiskerstudios.pocketrav.Interfaces.PocketRavPrefs_;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Services.DownloadIntentService_;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rachael on 10/31/17.
 */

@EActivity
public class StashActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    @Pref
    PocketRavPrefs_ prefs;

    private DownloadIntentResultReceiver downloadIntentResultReceiver;
    private static final String TAG = "Stash Activity";
    private SliderLayout slider;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Stash stash;
    private int stashType = -1;


    private StashInfo stashInfoFragment = new StashInfo();
    private StashNotes stashNotesFragment = new StashNotes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_stash);

        slider = (SliderLayout)findViewById(R.id.slider);

        downloadIntentResultReceiver = new DownloadIntentResultReceiver(new Handler());

        setupTabs();

        Intent intent = getIntent();
        int stashType = intent.getIntExtra(Constants.STASH_TYPE, -1);
        int stashId = intent.getIntExtra(Constants.STASH_ID, -1);

        if (stashType != -1 && stashId !=-1)
        {
            if (stashType == Constants.STASH_TYPE_YARN)
                startDownloadIntentService(Constants.FETCH_STASH_YARN, stashId);
            else
                startDownloadIntentService(Constants.FETCH_STASH_FIBER, stashId);
        }
    }

    private void setupTabs(){

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(stashInfoFragment, "Stash Details");
        adapter.addFragment(stashNotesFragment, "Stash Notes");
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void startDownloadIntentService(int type, int id){

        Intent intent = new Intent(this, DownloadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, downloadIntentResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE, type);
        intent.putExtra(Constants.STASH_ID, id);
        this.startService(intent);
    }

    private class DownloadIntentResultReceiver extends ResultReceiver {
        private DownloadIntentResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT) {

                //TODO: Set this up for stashes, not projects
                int type = resultData.getInt(Constants.FETCH_TYPE);
                String resultDataString = resultData.getString(Constants.RESULT_DATA_KEY);
                ObjectMapper mapper = new ObjectMapper();

                switch (type){
                    case Constants.FETCH_STASH_YARN:

                        try {
                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_project = jObject.get("stash").toString();
                            stash = mapper.readValue(s_project, Stash.class);

                            if (stash != null) {

                                final ActionBar actionBar = getSupportActionBar();
                                if (actionBar != null)
                                    actionBar.setTitle(stash.getName());

                                stashType = Constants.STASH_TYPE_YARN;
                                setupSlideshow(stash);
                                setupStash(stash);
                                setupNotes(stash);
                                setupTabs();
                            }

                        }catch (Exception e) {

                            Log.e(TAG, e.toString());
                        }

                        break;

                    case Constants.FETCH_STASH_FIBER:

                        try {
                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_project = jObject.get("fiber_stash").toString();
                            stash = mapper.readValue(s_project, Stash.class);

                            if (stash != null) {

                                final ActionBar actionBar = getSupportActionBar();
                                if (actionBar != null)
                                    actionBar.setTitle(stash.getName());

                                stashType = Constants.STASH_TYPE_FIBER;
                                setupSlideshow(stash);
                                setupStash(stash);
                                setupNotes(stash);
                                setupTabs();
                            }

                        }catch (Exception e) {

                            Log.e(TAG, e.toString());
                        }

                        break;
                }
            }
        }
    }

    private void setupNotes(Stash stash){

        Bundle bundle = new Bundle();

        if (stash.hasNotes())
            bundle.putString(Constants.NOTES_BUNDLE, stash.getNotes());
        else
            bundle.putString(Constants.NOTES_BUNDLE, "");
        bundle.putInt(Constants.STASH_TYPE, stashType);
        bundle.putInt(Constants.STASH_ID, stash.getId());
        stashNotesFragment.setArguments(bundle);
    }

    private void setupStash(Stash stash){

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.STASH_BUNDLE, stash);
        bundle.putInt(Constants.STASH_TYPE, stashType);
        stashInfoFragment.setArguments(bundle);
    }

    public void setupSlideshow(Stash stash){

        slider.removeAllSliders();

        if (stash.getPhotos().size() == 1) {

            setupSlide(stash, 0);
            slider.stopAutoCycle();
        }
        else{
            for (int i = 0; i < stash.getPhotos().size(); i++){

                setupSlide(stash, i);
                slider.stopAutoCycle();
                //slider.setDuration(4000);
                //slider.setPresetTransformer(SliderLayout.Transformer.Fade);
                slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                //slider.setCustomAnimation(new DescriptionAnimation());
                slider.addOnPageChangeListener(this);
            }
        }
    }



    private void setupSlide(Stash stash, int i){

        TextSliderView textSliderView = new TextSliderView(this);
        Photo tempPhoto = stash.getPhotos().get(i);


        textSliderView
                .description(tempPhoto.getCaption())
                .image(tempPhoto.getMedium2Url())
                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                .setOnSliderClickListener(this);

        textSliderView.bundle(new Bundle());
        textSliderView.getBundle().putString(Constants.PHOTO_BUNDLE, tempPhoto.getMedium2Url());

        slider.addSlider(textSliderView);

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        slider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) { Log.d("Image Slider", "Page Changed: " + position);   }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onBackPressed(){
        finish();
    }
}
