package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.DosService;
import uk.nhs.digital.uec.api.model.nhschoices.NHSChoicesV2DataModel;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface NHSChoicesSearchService {
  CompletableFuture<List<NHSChoicesV2DataModel>> retrieveParsedNhsChoicesV2Model(String searchLatitude, String searchLongitude, Double distanceRange, List<String> searchTerms, String searchPostcode) throws NotFoundException;
  DosService convertNHSChoicesToDosService (NHSChoicesV2DataModel nhsChoicesV2DataModel);
}
