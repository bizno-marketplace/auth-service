Feature: Validate Token (gRPC)
  As an internal service
  I want to validate a JWT token
  So that i can know if a token is valid or not

  Acceptance Criteria:
  * The token must not be null or empty
  * The token signature must be valid
  * The token must not be expired
  * The user must exist in the database

  Scenario: Successfully validate a valid token
    Given an active user exists in the database
    And a valid JWT token is generated for that user
    When the ValidateToken gRPC method is called with the token
    Then the gRPC response valid field should be "true"

  Scenario: Fail to validate an empty token
    When the ValidateToken gRPC method is called with an empty token
    Then the gRPC response valid field should be "INVALID_ARGUMENT: Token is required"

  Scenario: Fail to validate an invalid token
    When the ValidateToken gRPC method is called with an invalid token
    Then the gRPC response valid field should be "false"