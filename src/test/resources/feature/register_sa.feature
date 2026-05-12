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
    Then the response should be 200
    And a confirmation email should be sent to "admin@bizno.co.mz"
    And the confirmation email should have a link that expires after 15 minutes

  Scenario: Reject registration when user already exists
    Given a super admin already exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super             |
      | lastName  | Admin             |
      | email     | admin@bizno.co.mz |
      | password  |  Password@1234    |
    Then the response should be 409
    And the response should have a message "Super admin already exists"

  Scenario: Reject registration if email is already in use
    Given a super admin already exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super             |
      | lastName  | Admin             |
      | email     | admin@bizno.co.mz |
      | password  |  Password@1234    |
    Then the response should be 409
    And the response should have a message "Email already in use"

  Scenario: Reject registration if firstname is missing
    Given no super admin exists in the system
    When i send a POST request to "/supper-admins" with:
      | lastName | Admin             |
      | email    | admin@bizno.co.mz |
      | password  |  Password@1234    |
    Then the response should be 400
    And the response should have a message "First name is required"

  Scenario: Reject registration if firstname as less than 3 characters
    Given no super admin exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Su                |
      | lastName  | Admin             |
      | email     | admin@bizno.co.mz |
      | password  |  Password@1234    |
    Then the response should be 422
    And the response should have a message "First name must be at least 3 characters long"

  Scenario: Reject registration if lastname is missing
    Given no super admin exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super             |
      | email     | admin@bizno.co.mz |
      | password  |  Password@1234    |
    Then the response should be 400
    And the response should have a message "Last name is required"

  Scenario: Reject registration if lastname as less than 3 characters
    Given no super admin exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super             |
      | lastName  | Ad                |
      | email     | admin@bizno.co.mz |
      | password  |  Password@1234    |
    Then the response should be 422
    And the response should have a message "Last name must be at least 3 characters long"

  Scenario: Reject registration if email is missing
    Given no super admin exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super    |
      | lastName  | Admin    |
      | password  |  Password@1234    |
    Then the response should be 400
    And the response should have a message "Email is required"

  Scenario: Reject registration with non bizno institutional email
    Given no super admin exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super             |
      | lastName  | Admin             |
      | email     | admin@bizno.co.mz |
      | password  |  Password@1234    |
    Then the response should be 422
    And the response should have a message "Email must be a bizno institutional email"

  Scenario: Reject registration if password is missing
    Given no super admin exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super             |
      | lastName  | Admin             |
      | email     | admin@bizno.co.mz |
    Then the response should be 400
    And the response should have a message "Password is required"

  Scenario: Reject registration if weak password
    Given no super admin exists in the system
    When i send a POST request to "/supper-admins" with:
      | firstName | Super             |
      | lastName  | Admin             |
      | email     | admin@bizno.co.mz |
      | password  | password          |
    Then the response should be 422
    And the response should have a message"Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character"

