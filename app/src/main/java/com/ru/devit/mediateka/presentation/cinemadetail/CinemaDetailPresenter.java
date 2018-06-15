package com.ru.devit.mediateka.presentation.cinemadetail;

import com.ru.devit.mediateka.domain.cinemausecases.GetCinemaById;
import com.ru.devit.mediateka.domain.UseCaseSubscriber;
import com.ru.devit.mediateka.models.model.Cinema;
import com.ru.devit.mediateka.presentation.base.BasePresenter;
import com.ru.devit.mediateka.presentation.base.BaseView;

import java.util.List;

public class CinemaDetailPresenter extends BasePresenter<CinemaDetailPresenter.View> {

    private final GetCinemaById getCinemaById;
    private int cinemaId;
    private boolean isFABMenuOpen;

    public CinemaDetailPresenter(GetCinemaById getCinemaById) {
        this.getCinemaById = getCinemaById;
    }

    @Override
    public void initialize() {
        getView().showLoading();
        getCinemaById.searchCinemaById(cinemaId);
        getCinemaById.subscribe(new CinemaDetailSubscriber());
    }

    public void setCinemaId(int cinemaId){
        this.cinemaId = cinemaId;
    }

    public void onSmallPosterClicked(List<String> posterUrls) {
        getView().showListPosters(posterUrls);
    }

    public void onFABCinemaMenuClicked() {
        if (!isFABMenuOpen){
            isFABMenuOpen = true;
            getView().showFABCinemaMenu();
        } else {
            isFABMenuOpen = false;
            getView().hideFABCinemaMenu();
        }
    }

    public void onForegroundViewClicked() {
        isFABMenuOpen = false;
        getView().hideFABCinemaMenu();
    }

    public void onDestroy(){
        getCinemaById.dispose();
        setView(null);
    }

    public interface View extends BaseView{
        void showCinemaDetail(Cinema cinemaDetail);
        void showListPosters(List<String> postersUrl);
        void showFABCinemaMenu();
        void hideFABCinemaMenu();
    }

    private final class CinemaDetailSubscriber extends UseCaseSubscriber<Cinema>{
        @Override
        public void onNext(Cinema cinema) {
            getView().showCinemaDetail(cinema);
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            getView().hideLoading();
        }

        @Override
        public void onComplete() {
            getView().hideLoading();
        }
    }
}
