package com.arushi.bakingapp.recipe;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.arushi.bakingapp.BApplication;
import com.arushi.bakingapp.R;
import com.arushi.bakingapp.data.local.entity.DessertEntity;
import com.arushi.bakingapp.data.local.entity.IngredientEntity;
import com.arushi.bakingapp.data.local.entity.StepEntity;
import com.arushi.bakingapp.utils.GlideApp;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class RecipeFragment extends Fragment {

    private int mId;
    private int mDefaultImgResource;
    private RecipeViewModel mViewModel = null;
    ImageView mIvDessert;
    TextView mTvName, mTvServings;
    CollapsingToolbarLayout mCollapsingToolbar;

    IngredientsAdapter mIngredientsAdapter;
    StepsAdapter mStepsAdapter;
    RecyclerView mRvIngredients, mRvSteps;
    RecipeListener mStepClickListener;
    private boolean mIsTwoPane = false;
    List<StepEntity> mStepEntities;

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    // Mandatory constructor
    public RecipeFragment() {}

    public interface RecipeListener {
        void showStepDetail(ArrayList<StepEntity> stepEntities, int position);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_recipe, container, false);

        setupViewModel();

        initViews(rootview);
        if(!mIsTwoPane) {
            // Only shown when not two pane
            setupDessertObserver();
        }

        bindIngredientViews(rootview);
        setupIngredientsObserver();

        bindStepViews(rootview);
        setupStepsObserver();
        return rootview;
    }

    private void setupViewModel(){
        if(getActivity()!=null) {
            ((BApplication) getActivity().getApplication()).getAppComponent().inject(this);
            mViewModel = ViewModelProviders.of(this, viewModelFactory).get(RecipeViewModel.class);
        }
    }

    private void initViews(View rootview){
        if(!mIsTwoPane){
            // Only shown when not two pane
            mIvDessert = rootview.findViewById(R.id.iv_dessert);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && mIvDessert!=null) {
                String id = String.valueOf(mId);
                mIvDessert.setTransitionName(getString(R.string.text_transition_img)+id);
            }
        }

        mTvName = rootview.findViewById(R.id.tv_name);
        mTvServings = rootview.findViewById(R.id.tv_servings);

        mCollapsingToolbar = (CollapsingToolbarLayout) rootview.findViewById(R.id.collapsing_toolbar);

    }

    public void setupDessertObserver(){

        if(mViewModel.getDessert(mId).hasObservers()) return;

        mViewModel.getDessert(mId).observe(this,
                new Observer<DessertEntity>() {
                    @Override
                    public void onChanged(@Nullable DessertEntity dessert) {
                        if ( dessert!= null ){
                            setTitle(dessert.getName());
                            setImage(dessert.getImage());
                            mTvServings.setText(String.valueOf(dessert.getServings()));

                        }
                    }
                });
    }

    public void bindIngredientViews(View rootview){
        mRvIngredients = rootview.findViewById(R.id.rv_ingredients);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        mRvIngredients.setLayoutManager(layoutManager);
        mRvIngredients.setHasFixedSize(true);
        mRvIngredients.setNestedScrollingEnabled(false);
        mIngredientsAdapter = new IngredientsAdapter(this.getContext());
        mRvIngredients.setAdapter(mIngredientsAdapter);
    }

    public void setupIngredientsObserver(){

        if(mViewModel.getIngredients(mId).hasObservers()) return;

        mViewModel.getIngredients(mId).observe(this,
                new Observer<List<IngredientEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<IngredientEntity> ingredientEntities) {
                        if(ingredientEntities != null
                                && ingredientEntities.size()>0 ){
                            // Set ingredient list
                            mIngredientsAdapter.setIngredientList(ingredientEntities);

                        }
                    }
                });
    }

    public void bindStepViews(View rootview){
        mRvSteps = rootview.findViewById(R.id.rv_steps);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext(),
                LinearLayoutManager.VERTICAL,
                false);
        mRvSteps.setLayoutManager(layoutManager);
        mRvSteps.setHasFixedSize(true);
        mRvSteps.setNestedScrollingEnabled(false);
        mStepsAdapter = new StepsAdapter(this.getContext(), mStepClickListener);
        mRvSteps.setAdapter(mStepsAdapter);
    }

    public void setupStepsObserver(){

        if(mViewModel.getRecipeSteps(mId).hasObservers()) return;

        mViewModel.getRecipeSteps(mId).observe(this,
                new Observer<List<StepEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<StepEntity> stepEntities) {
                        if(stepEntities != null
                                && stepEntities.size()>0 ){
                            // Set steps list
                            mStepsAdapter.setStepsList(stepEntities);
                            mStepEntities = stepEntities;
                            if(mIsTwoPane){
                                mStepClickListener.showStepDetail((ArrayList) stepEntities,
                                                                0);
                            }

                        }
                    }
                });
    }

    private void setImage(String imageUrl){

        if(getActivity()!=null) {
            Drawable defaultImg = getActivity().getResources().getDrawable(mDefaultImgResource);

            GlideApp.with(getActivity().getBaseContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(defaultImg)
                    .error(defaultImg)
                    .dontAnimate()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            scheduleStartPostponedTransition(mIvDessert);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            scheduleStartPostponedTransition(mIvDessert);
                            return false;
                        }
                    })
                    .into(mIvDessert);
        } else {
            Timber.e("getActivity() returned null");
        }
    }

    private void setTitle(String title){
        //Set toolbar title
        if(mCollapsingToolbar!=null) {
            mCollapsingToolbar.setTitle(title);
        }
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        if(getActivity()!=null) {
                            getActivity().supportStartPostponedEnterTransition();
                        }
                        return true;
                    }
                });
    }

    public void setRecipeData(int id, int defaultImgResource){
        mId = id;
        mDefaultImgResource = defaultImgResource;
    }

    public void setTwoPane(boolean isTwoPane){
        mIsTwoPane = isTwoPane;
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mStepClickListener = (RecipeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement RecipeListener");
        }
    }
}