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
import com.whitewhiskerstudios.pocketrav.API.Models.Photo;
import com.whitewhiskerstudios.pocketrav.API.Models.Project;
import com.whitewhiskerstudios.pocketrav.Adapters.ViewPagerAdapter;
import com.whitewhiskerstudios.pocketrav.Fragments.ProjectInfo;
import com.whitewhiskerstudios.pocketrav.Fragments.ProjectInfo_;
import com.whitewhiskerstudios.pocketrav.Fragments.ProjectNotes;
import com.whitewhiskerstudios.pocketrav.Fragments.ProjectPattern;
import com.whitewhiskerstudios.pocketrav.Interfaces.PocketRavPrefs_;
import com.whitewhiskerstudios.pocketrav.R;
import com.whitewhiskerstudios.pocketrav.Services.DownloadIntentService_;
import com.whitewhiskerstudios.pocketrav.Utils.Constants;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONObject;


/**
 * Created by rachael on 9/17/17.
 */
@EActivity
public class ProjectActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener{

    @Pref
    PocketRavPrefs_ prefs;

    private DownloadIntentResultReceiver downloadIntentResultReceiver;
    private static final String TAG = "ProjectActivity";
    public Project project;
    private SliderLayout slider;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ProjectInfo_ projectInfoFragment = new ProjectInfo_();
    private ProjectNotes projectNotesFragment = new ProjectNotes();
    private ProjectPattern projectPatternFragment = new ProjectPattern();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_stash);

        slider = (SliderLayout)findViewById(R.id.slider);

        downloadIntentResultReceiver = new DownloadIntentResultReceiver(new Handler());

        Intent intent = getIntent();
        int projectId = intent.getIntExtra(Constants.PROJECT_ID, -1);

        if (projectId != -1){
            startDownloadIntentService(Constants.FETCH_PROJECT, projectId);
        }
    }

    private void setupTabs(){

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(projectInfoFragment, "Project Details");
        adapter.addFragment(projectNotesFragment, "Project Notes");
        adapter.addFragment(projectPatternFragment, "Pattern Details");
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    
    private void startDownloadIntentService(int type, int id){

        Intent intent = new Intent(this, DownloadIntentService_.class);
        intent.putExtra(Constants.RECEIVER, downloadIntentResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE, type);
        intent.putExtra(Constants.PROJECT_ID, id);
        this.startService(intent);
    }

    private class DownloadIntentResultReceiver extends ResultReceiver {
        private DownloadIntentResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == Constants.SUCCESS_RESULT){

                int type = resultData.getInt(Constants.FETCH_TYPE);
                String resultDataString = resultData.getString(Constants.RESULT_DATA_KEY);
                ObjectMapper mapper = new ObjectMapper();

                switch(type){
                    case Constants.FETCH_PROJECT:

                        try {
                            JSONObject jObject = new JSONObject(resultDataString);
                            String s_project = jObject.get("project").toString();
                            project = mapper.readValue(s_project, Project.class);

                            if (project != null) {

                                final ActionBar actionBar = getSupportActionBar();
                                if (actionBar != null)
                                    actionBar.setTitle(project.getName());

                                setupSlideshow(project);
                                setupProject();
                                setupPattern();
                                setupNotes();
                                setupTabs();
                            }

                        }catch (Exception e) {

                            project = null;
                            Log.e(TAG, e.toString());
                        }

                        break;

                    case Constants.FETCH_PATTERN:



                        break;

                    case Constants.FETCH_PHOTOS:
                        break;


                    default:
                        Log.e(TAG, "How did we get here....?");
                }
            }
        }
    }
    // Can be called from child fragment in the event that the images are updated
    // from the ProjectInfo fragment
    public void setupSlideshow(Project project){


        slider.removeAllSliders();

        if (project.getPhotos().size() == 1) {

            setupSlide(project, 0);
            slider.stopAutoCycle();


        }
        else{
            for (int i = 0; i < project.getPhotos().size(); i++){

                setupSlide(project, i);
                slider.stopAutoCycle();
                //slider.setDuration(4000);
                //slider.setPresetTransformer(SliderLayout.Transformer.Fade);
                slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                //slider.setCustomAnimation(new DescriptionAnimation());
                slider.addOnPageChangeListener(this);

            }
        }
    }

    private void setupSlide(Project project, int i){

        TextSliderView textSliderView = new TextSliderView(this);
        Photo tempPhoto = project.getPhotos().get(i);


        textSliderView
                .description(tempPhoto.getCaption())
                .image(tempPhoto.getMedium2Url())
                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                .setOnSliderClickListener(this);

        textSliderView.bundle(new Bundle());
        textSliderView.getBundle().putString(Constants.PHOTO_BUNDLE, tempPhoto.getMedium2Url());

        slider.addSlider(textSliderView);

    }

    private void setupProject(){

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.PROJECT_BUNDLE, project);
        projectInfoFragment.setArguments(bundle);
    }

    private void setupPattern(){

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.PATTERN_ID, project.getPatternId());
        projectPatternFragment.setArguments(bundle);
    }

    private void setupNotes(){

        Bundle bundle = new Bundle();
        bundle.putString(Constants.NOTES_BUNDLE, project.getNotes());
        bundle.putInt(Constants.PROJECT_ID, project.getId());
        projectNotesFragment.setArguments(bundle);
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
    public void onPageSelected(int position) {
        Log.d("Image Slider", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onBackPressed(){


            int count = getFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                super.onBackPressed();
                //additional code
            } else {
                getFragmentManager().popBackStack();
            }

            projectInfoFragment.setRecyclerViewAdapter(null);

        finish();
    }
}


