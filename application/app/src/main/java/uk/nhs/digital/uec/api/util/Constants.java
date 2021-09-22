package uk.nhs.digital.uec.api.util;

public class Constants {

  public static final String IDENTITY_PROVIDER_ID = "IDENTITY_PROVIDER_ID";

  public static final String USER_HASH = "USER_HASH";

  public static final String ROLE_PREFIX = "ROLE_";

  public static final String COGNITO_GROUPS = "cognito:groups";

  public static final String CLAIM_NAME = "sub";

  public static final String HEALTH_CHECK_READINESS_URL = "/actuator/health/readiness";

  public static final String HEALTH_CHECK_LIVENESS_URL = "/actuator/health/liveness";

  public static final String WELCOME_URL = "/dosapi/dosservices/v0.0.1/home";

  public static final String FUZZY_SEARCH_URL = "/dosapi/dosservices/v0.0.1/services/byfuzzysearch";
}
