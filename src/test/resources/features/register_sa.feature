Feature: Register Super Admin
  As a non registered user
  I want to register as a super admin if not exists
  So that I can access all features

  Acceptance Criteria:
  * The system can only have one Super Admin
  * The system must not accept creating a Super Admin with invalid credentials
  * The Super Admin must register with a Bizno institutional email (@bizno.co.mz)
  * The system must send a confirmation email to the provided email address
  * The confirmation email link expires after 15 minutes
  * After 15 minutes the user must request a new confirmation email

  Scenario: Successfully register super admin when none exists
    Given no super admin exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super             |
      | lastName  | Admin             |
      | email     | admin@bizno.co.mz |
      | password  | Password@1234     |
    Then the response status should be 200
    And a confirmation email should be sent to "admin@bizno.co.mz"
    And the confirmation email should have a link that expires after 15 minutes
    And the response body should contain message "We've sent an activation link to provided email: admin@bizno.co.mz"

  Scenario: Reject registration when super admin already exists
    Given a super admin already exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super             |
      | lastName  | Admin             |
      | email     | admin@bizno.co.mz |
      | password  | Password@1234     |
    Then the response status should be 409
    And the response body should contain error "Super admin already exists"

  Scenario: Reject registration if email is already in use
    Given a user with email "admin@bizno.co.mz" exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super             |
      | lastName  | Admin             |
      | email     | admin@bizno.co.mz |
      | password  | Password@1234     |
    Then the response status should be 409
    And the response body should contain error "Email already in use"

  Scenario Outline: Attempt to register with invalid or missing fields
    Given no super admin exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | <firstName> |
      | lastName  | <lastName>  |
      | email     | <email>     |
      | password  | <password>  |
    Then the response status should be <statusCode>
    And the response body should contain error "<error>"

    Examples:
      | firstName | lastName | email             | password      | statusCode | error                                                                                                                                              |
      |           | Admin    | admin@bizno.co.mz | Password@1234 | 400        | First name is required                                                                                                                             |
      | Su        | Admin    | admin@bizno.co.mz | Password@1234 | 422        | First name must be at least 3 characters long                                                                                                      |
      | Super     |          | admin@bizno.co.mz | Password@1234 | 400        | Last name is required                                                                                                                              |
      | Super     | Ad       | admin@bizno.co.mz | Password@1234 | 422        | Last name must be at least 3 characters long                                                                                                       |
      | Super     | Admin    |                   | Password@1234 | 400        | E-mail is required                                                                                                                                 |
      | Super     | Admin    | admin@gmail.com   | Password@1234 | 422        | E-mail must be a bizno institutional email                                                                                                         |
      | Super     | Admin    | admin@bizno.co.mz |               | 400        | Password is required                                                                                                                               |
      | Super     | Admin    | admin@bizno.co.mz | password      | 422        | Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character |