# API Authentication Documentation

This section contains documentation and flow diagrams for the authentication flow.

## Contents

- Fuzzy Authentication Login.png - describes the authentication login flow

# Login API


Login API endpoint will authenticate the user before getting an access to the Fuzzy Search API. Login API accepts the user credentials. The user credentials are then passed to Authentication service, which then sends the information to Cognito service. Cognito service calls the AWS Cognito, which will responds back with ACCESS_TOKEN and REFRESH_TOKEN, which will be validated against the user details and accepts or rejects the authentication to access Fuzzy Search API.

Please find the Login API endpoint documentation in confluence:
https://nhsd-confluence.digital.nhs.uk/display/SFDEV/Login+Endpoint

Please find the Login API flow diagram in confluence:
https://nhsd-confluence.digital.nhs.uk/pages/viewpage.action?spaceKey=SFDEV&title=Authentication+Login+Flow

Please find the Login API c4 model diagrams in following link:
https://nhsd-confluence.digital.nhs.uk/pages/viewpage.action?spaceKey=SFDEV&title=Authentication+Login+C4+Models


# API Authorization

Fuzzy Search API is authorized endpoint and you need to be fully authenticated to access this resource.
When Fuzzy search API endpoint is called, the spring filter handles the authorization process by validating
the ACCESS_TOKEN generated from Cognito during the login process.
Authentication failed message is thrown back to the user, if the ACCESS_TOKEN is expired or if its invalid.

Please find the Authorization process flow diagram in confluence:
https://nhsd-confluence.digital.nhs.uk/display/SFDEV/Authentication+Flow

Please find the Authorization c4 model diagrams in following link:
https://nhsd-confluence.digital.nhs.uk/display/SFDEV/Authentication+C4+model
