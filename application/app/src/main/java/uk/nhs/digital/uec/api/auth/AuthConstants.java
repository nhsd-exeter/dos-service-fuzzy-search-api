package uk.nhs.digital.uec.api.auth;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthConstants {

  public static final String ACCESS_TOKEN = "ACCESS_TOKEN";

  public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

  public static final String SUB = "sub";

  public static final String COGNITO_GROUPS = "cognito:groups";

  public static final String IDENTITY_PROVIDER_ID = "IDENTITY_PROVIDER_ID";

  public static final String USER_HASH = "USER_HASH";
}
