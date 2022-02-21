package uk.nhs.digital.uec.api.service;

import java.util.List;
import uk.nhs.digital.uec.api.exception.NotFoundException;
import uk.nhs.digital.uec.api.model.DosService;

/** Interface encapsulating validation processes used by the API. */
public interface ValidationServiceInterface {

  /**
   * Validates that the search criteria provided meets business requirements. Namely that the search
   * criteria must not be empty or null, and that the number of search terms does not exceed a
   * defined value.
   *
   * @param searchCriteria the searchCriteria list to validate.
   * @throws NotFoundException when business requirements are not met.
   */
  public void validateSearchCriteria(final List<String> searchCriteria) throws NotFoundException;

  /**
   * Validates if there are any dos services returned
   *
   * @param dosService the searchCriteria string to validate.
   * @throws NotFoundException when none of the dos search results are returned
   */
  public void validateDosService(List<DosService> dosServices) throws NotFoundException;
}
