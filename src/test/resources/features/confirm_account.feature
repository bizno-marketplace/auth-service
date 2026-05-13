Feature: Confirm Account
  As a registered user
  I want to confirm my account via email link
  So that my account becomes active and i can access the platform

  Acceptance Criteria:
  * The confirmation token must be valid and not expired
  * A confirmed account must have its status changed to ACTIVE
  * An expired token must be rejected with HTTP 410
  * An already confirmed account must not be confirmed again
  * An invalid or tampered token must be rejected
  * A missing token must be rejected

  Scenario: Successfully confirm account with valid token
    Given a user regitration was made with email "user@example.com"
    And the confirmation token has not expired
    When i send a GET request to "/confirm-account?token={validToken}"
    Then the response code is 204
    And the user account status should be "ACTIVE"

  Scenario: Reject confirmation with expired token
    Given a user regitration was made with email "user@example.com"
    And the confirmation token has expired after 15 minutes
    When i send a GET request to "/confirm-account?token={expiredToken}"
    Then the response code is 410
    And the response body should contain error "Confirmation link expired"

  Scenario: Reject confirmation with invalid or tampered token
    Given a user registration was made with email "user@example.com"
    When i send a GET request to "/confirm-account?token={invalidToken}"
    Then the response code is 400
    And the response body should contain error "Invalid confirmation link"

  Scenario: Reject confirmation when account is already active
    Given a user with email "user@bizno.co.mz" has already confirmed the account
    When I send a GET request to "/confirm-account?token={validToken}"
    Then the response status should be 409
    And the response body should contain error "Account already confirmed"

  Scenario: Reject confirmation when token is missing
    When I send a GET request to "/confirm-account"
    Then the response status should be 400
    And the response body should contain error "Token is required"