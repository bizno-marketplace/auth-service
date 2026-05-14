Feature: Resend Confirmation Email
  As a registered user with an unconfirmed account
  I want to request a new confirmation email
  So that I can activate my account when the previous token expired or was not received

  Acceptance Criteria:
  * Only accounts in PENDING status can request a resend
  * The previous token must be invalidated before issuing a new one
  * The new token expires in 15 minutes
  * Already active accounts must not receive new confirmation emails
  * Non-existent emails must not reveal whether an account exists (security)
  * A cooldown period must be enforced to prevent email flooding

  Scenario: Successfully resend confirmation email for a pending account
    Given a user registered with email "user@example.com" with status "PENDING"
    And the previous confirmation token has expired
    When I send a POST request to "/accounts/resend-confirmation" with body:
      | email | user@example.com |
    Then the response status should be 200
    And the previous token should be invalidated
    And a new confirmation email should be sent to "user@example.com"
    And the new token should expire in 15 minutes

  Scenario: Reject resend when account is already active
    Given a user with email "user@example.com" has status "ACTIVE"
    When I send a POST request to "/accounts/resend-confirmation" with body:
      | email | user@example.com |
    Then the response status should be 409
    And the response body should contain error "Account already confirmed"

  Scenario: Return 200 for non-existent email (security — no enumeration)
    Given no user exists with email "ghost@example.com"
    When I send a POST request to "/accounts/resend-confirmation" with body:
      | email | ghost@example.com |
    Then the response status should be 200

  Scenario: Reject resend during cooldown period
    Given a user registered with email "user@example.com" with status "PENDING"
    And a confirmation email was already sent less than 2 minutes ago
    When I send a POST request to "/accounts/resend-confirmation" with body:
      | email | user@example.com |
    Then the response status should be 429
    And the response body should contain error "Please wait before requesting a new confirmation email"

  Scenario: Reject resend when email is missing in request body
    When I send a POST request to "/accounts/resend-confirmation" with empty body
    Then the response status should be 400
    And the response body should contain error "Email is required"

  Scenario: Reject resend when email format is invalid
    When I send a POST request to "/accounts/resend-confirmation" with body:
      | email | not-an-email |
    Then the response status should be 400
    And the response body should contain error "Invalid email format"