package com.ru.devit.mediateka.domain.cinemausecases;

import android.util.Log;

import com.ru.devit.mediateka.data.repository.cinema.CinemaRepository;
import com.ru.devit.mediateka.domain.UseCase;
import com.ru.devit.mediateka.models.model.Cinema;
import com.ru.devit.mediateka.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;

public class GetUpComingCinemas extends UseCase<List<Cinema>> {

    private final CinemaRepository repository;

    public GetUpComingCinemas(Scheduler executorThread ,
                              Scheduler uiThread ,
                              CinemaRepository repository) {
        super(executorThread, uiThread);
        this.repository = repository;
    }

    @Override
    public Flowable<List<Cinema>> createUseCase() {
        return repository.getUpComingCinemas(pageIndex)
                .toFlowable()
                .flatMap(cinemas -> Flowable.fromIterable(cinemas)
                        .filter(cinema -> !cinema.getDescription().isEmpty())
                        .filter(cinema -> !cinema.getReleaseDate().equals(Constants.DEFAULT_VALUE) && cinema.getVoteAverage() == 0)
                        .toList()
                        .toFlowable());

    }
}