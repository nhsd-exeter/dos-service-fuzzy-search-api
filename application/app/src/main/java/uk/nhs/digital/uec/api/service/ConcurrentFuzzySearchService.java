package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.DosService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface ConcurrentFuzzySearchService {
  CompletableFuture<List<DosService>> fuzzySearch(String searchLatitude,
                                                  String searchLongitude,
                                                  Double distanceRange,
                                                  List<String> searchTerms,
                                                  String searchPostcode,
                                                  Integer maxNumServicesToReturn) throws InterruptedException, ExecutionException, NotFoundException, InvalidParameterException;
}
