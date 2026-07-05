Feature: Get User Profile (gRPC)
  As an internal service
  I want to get a user profile by id
  So that i can retrieve user information for other services

  Acceptance Criteria:
  * The userId must not be null or empty
  * The userId must be a valid UUID
  * The user must exist in the database

  Scenario: Successfully get an active user profile
    Given an active user exists in the database
    When the GetUserProfile gRPC method is called with the user id
    Then the response should contain the user id
    And the response should contain the user email
    And the response should contain the user first name
    And the response should contain the user last name
    And the response should contain the user role
    And the response should contain the user status "ACTIVE"

  Scenario: Fail to get profile with empty user id
    When the GetUserProfile gRPC method is called with an empty user id
    Then the gRPC status should be INVALID_ARGUMENT
    And the gRPC error message should contain "User id is required"

  Scenario: Fail to get profile with invalid uuid
    When the GetUserProfile gRPC method is called with an invalid uuid "not-a-uuid"
    Then the gRPC status should be INVALID_ARGUMENT
    And the gRPC error message should contain "Invalid user id format"

  Scenario: Fail to get profile for non existing user
    When the GetUserProfile gRPC method is called with a non existing user id
    Then the gRPC status should be NOT_FOUND
    And the gRPC error message should contain "User not found"