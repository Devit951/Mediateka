package com.ru.devit.mediateka.di.cinema.cinemadetail;

import com.ru.devit.mediateka.data.CinemaNotificationReceiver;
import com.ru.devit.mediateka.domain.CinemaRepository;
import com.ru.devit.mediateka.di.ActivityScope;
import com.ru.devit.mediateka.domain.SystemTimeCalculator;
import com.ru.devit.mediateka.domain.cinemausecases.GetCinemaById;
import com.ru.devit.mediateka.domain.cinemausecases.GetCinemaByQuery;
import com.ru.devit.mediateka.domain.cinemausecases.GetFavouriteListCinema;
import com.ru.devit.mediateka.presentation.cinemadetail.CinemaDetailContentPresenter;
import com.ru.devit.mediateka.presentation.cinemadetail.CinemaDetailPresenter;
import com.ru.devit.mediateka.presentation.smallcinemalist.SmallCinemasPresenter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;

@Module
public class CinemaDetailModule {

    @ActivityScope
    @Provides
    CinemaDetailPresenter provideCinemaDetailPresenter(GetCinemaById getCinemaById ,
                                                       GetFavouriteListCinema getFavouriteListCinema){
        return new CinemaDetailPresenter(getCinemaById , getFavouriteListCinema);
    }

    @ActivityScope
    @Provides
    SmallCinemasPresenter provideSmallCinemasPresenter(GetCinemaByQuery getCinemaByQuery){
        return new SmallCinemasPresenter(getCinemaByQuery);
    }

    @ActivityScope
    @Provides
    CinemaDetailContentPresenter provideCinemaDetailContentPresenter(){
        return new CinemaDetailContentPresenter();
    }

    @ActivityScope
    @Provides
    SystemTimeCalculator provideSystemTimeCalculator(){
        return new CinemaNotificationReceiver();
    }

    @ActivityScope
    @Provides
    GetCinemaById providesGetCinemaById(@Named("executor_thread") Scheduler executorThread ,
                                        @Named("ui_thread")Scheduler uiThread ,
                                        CinemaRepository repository ,
                                        SystemTimeCalculator systemTimeCalculator){
        return new GetCinemaById(executorThread , uiThread , repository, systemTimeCalculator);
    }

    @ActivityScope
    @Provides
    GetCinemaByQuery providesGetCinemaByQuery(@Named("executor_thread") Scheduler executorThread ,
                                        @Named("ui_thread")Scheduler uiThread ,
                                        CinemaRepository repository){
        return new GetCinemaByQuery(executorThread , uiThread , repository);
    }
}
