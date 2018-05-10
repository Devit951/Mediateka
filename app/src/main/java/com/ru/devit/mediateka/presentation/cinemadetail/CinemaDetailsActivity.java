package com.ru.devit.mediateka.presentation.cinemadetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.ru.devit.mediateka.MediatekaApp;
import com.ru.devit.mediateka.R;
import com.ru.devit.mediateka.di.cinema.CinemaDetailModule;
import com.ru.devit.mediateka.models.model.Cinema;
import com.ru.devit.mediateka.presentation.common.ViewPagerAdapter;
import com.ru.devit.mediateka.presentation.base.BaseActivity;
import com.ru.devit.mediateka.presentation.actorlist.ActorsFragment;
import com.ru.devit.mediateka.presentation.widget.CinemaHeaderView;
import com.ru.devit.mediateka.utils.AnimUtils;
import com.ru.devit.mediateka.utils.Constants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Locale;

import javax.inject.Inject;

public class CinemaDetailsActivity extends BaseActivity implements CinemaDetailPresenter.View{

    private static final String CINEMA_ID = "cinema_id";

    private ImageView mBackgroundPoster;
    private ImageView mSmallPosterImageView;
    private CinemaHeaderView mCinemaHeaderView;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ProgressBar mProgressBarBackgroundImage;
    private AppBarLayout mAppBarLayout;

    private final int BACKGROUND_POSTER_POSITION = 1;
    private final int SMALL_POSTER_POSITION = 0;

    @Inject CinemaDetailPresenter presenter;

    public static Intent makeIntent(Context context , int cinemaId){
        Intent intent = new Intent(context , CinemaDetailsActivity.class);
        intent.putExtra(CINEMA_ID , cinemaId);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cinema_detail;
    }

    @Override
    protected void initViews(){
        mBackgroundPoster = findViewById(R.id.iv_cinema_detail_background_poster);
        mSmallPosterImageView = findViewById(R.id.iv_cinema_small_poster);
        mCinemaHeaderView = findViewById(R.id.cinema_header_view);
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mProgressBarBackgroundImage = findViewById(R.id.progressBackgroundImage);
        mAppBarLayout = findViewById(R.id.app_bar_cinema);
    }

    @Override
    public void showLoading() {
        mCinemaHeaderView.startProgress();
    }

    @Override
    public void hideLoading() {
        mCinemaHeaderView.hideProgress();
    }

    @Override
    public void showCinemaDetail(Cinema cinema) {
        AnimUtils.startRevealAnimation(mBackgroundPoster);
        renderImage(cinema.getPosterPath() , mSmallPosterImageView , SMALL_POSTER_POSITION , Constants.IMG_PATH_W185);
        renderImage(cinema.getBackdropPath() , mBackgroundPoster , BACKGROUND_POSTER_POSITION , Constants.IMG_PATH_W500);
        mCinemaHeaderView.render(cinema);
        addOffsetChangeListener(mAppBarLayout , cinema.getTitle());
        setUpViewPager(mViewPager , mTabLayout , cinema);
    }

    @Override
    public void showNetworkError(String message) {
        Log.d("bbb" , message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_home : {
                navigateToMainActivity(this);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        mAppBarLayout.addOnOffsetChangedListener(null);
        super.onStop();
    }

    @Override
    protected void initToolbar(){
        super.initToolbar();
        if (isAboveLollipop()){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void initDagger(){
        MediatekaApp.getComponentsManager()
                .plusCinemaComponent()
                .plusCinemaDetailComponent(new CinemaDetailModule())
                .inject(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void initPresenter(){
        presenter.setView(this);
        presenter.setCinemaId(getIntent().getExtras().getInt(CINEMA_ID));
        presenter.initialize();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        MediatekaApp.getComponentsManager().clearCinemaComponent();
        super.onDestroy();
    }

    private void setUpViewPager(ViewPager viewPager , TabLayout tabLayout , Cinema cinema) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(CinemaDetailContentFragment.newInstance(cinema) , getString(R.string.message_info));
        adapter.addFragment(ActorsFragment.newInstance(cinema.getActors()) , String
                .format(Locale.getDefault() , "%s (%d)" , getString(R.string.actors) , cinema.getActors().size()));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void renderImage(String url , ImageView image , int posterPosition , String imgPath){
        Picasso.with(CinemaDetailsActivity.this)
                .load(imgPath + url)
                .placeholder(posterPosition == SMALL_POSTER_POSITION ? R.color.colorDarkBackground : R.color.colorWhite)
                .error(R.drawable.ic_cinema)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (posterPosition == SMALL_POSTER_POSITION){
                            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                            Palette.from(bitmap)
                                    .generate(palette -> {
                                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                        if (mutedSwatch != null) {
                                            mTabLayout.setBackgroundColor(mutedSwatch.getRgb());
                                        }
                                    });
                        } else if (posterPosition == BACKGROUND_POSTER_POSITION){
                            mProgressBarBackgroundImage.setVisibility(View.GONE);
                            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                            Palette.from(bitmap)
                                    .generate(palette -> {
                                        Palette.Swatch darkMutedSwatch = palette.getDarkMutedSwatch();
                                        if (darkMutedSwatch != null){
                                            getCollapsingToolbarLayout().setContentScrimColor(darkMutedSwatch.getRgb());
                                            getCollapsingToolbarLayout().setBackgroundColor(darkMutedSwatch.getRgb());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onError() {
                        mProgressBarBackgroundImage.setVisibility(View.GONE);
                    }
                });
    }
}