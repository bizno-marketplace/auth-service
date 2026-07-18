Feature: Register Courier
  As a Platform Admin
  I want to register a new courier
  So that they can access the platform to accept and deliver orders

  Acceptance Criteria:
  * Only an authenticated Super Admin can register a courier
  * No documents (BI) are required at this stage
  * All fields are mandatory: firstName, lastName, email, phone, password,
  vehicleType, licenseNumber and zone
  * vehicleType must be one of: MOTORCYCLE, BIKE, CAR, FOOT
  * A new courier account is created with status PENDING
  * An activation email is sent to the courier to confirm the account
  * Email and phone number must be unique across all users
  * Phone number must follow the Mozambican format

  Scenario: Reject registration when no authentication token is provided
    When i send a POST request register courier using endpoint "/couriers/register" without an Authorization header
    Then the response status should be 403

  Scenario: Reject registration when logged user is not a Super Admin
    Given a seller is authenticated
    When i send a POST request register courier using endpoint "/couriers/register" with:
      | firstName     | Carlos             |
      | lastName      | Machava            |
      | email         | carlos@bizno.co.mz |
      | phone         | +258841234567      |
      | password      | Password@123       |
      | vehicleType   | MOTORCYCLE         |
      | licenseNumber | AB123456           |
      | zone          | Maputo Central     |
    Then the response status should be 403
    And the response body should contain error "Access denied"

  Scenario: Successfully register a courier
    And i am authenticated as a Supper Admin
    When i send a POST request register courier using endpoint "/couriers/register" with:
      | firstName     | Carlos             |
      | lastName      | Machava            |
      | email         | carlos@bizno.co.mz |
      | phone         | +258841234567      |
      | password      | Password@123       |
      | vehicleType   | MOTORCYCLE         |
      | licenseNumber | AB123456           |
      | zone          | Maputo Central     |
    Then the response status should be 200
    And a courier should exist with status "PENDING"
    And an activation token should be generated for the courier
    And a domain event should be published to notify the courier registration

  Scenario: Reject registration when email already exists
    Given a user already exists with email "carlos@bizno.co.mz"
    And i am authenticated as a Supper Admin
    When i send a POST request register courier using endpoint "/couriers/register" with:
      | firstName     | Carlos             |
      | lastName      | Machava            |
      | email         | carlos@bizno.co.mz |
      | phone         | +258851234567      |
      | password      | Password@123       |
      | vehicleType   | MOTORCYCLE         |
      | licenseNumber | AB123456           |
      | zone          | Maputo Central     |
    Then the response status should be 409
    And the response body should contain error "E-mail already in use"

  Scenario Outline: Reject registration when a required field is missing
    Given i am authenticated as a Supper Admin
    When i send a POST request register courier using endpoint "/couriers/register" with:
      | firstName     | <firstName>     |
      | lastName      | <lastName>      |
      | email         | <email>         |
      | phone         | <phone>         |
      | password      | <password>      |
      | vehicleType   | <vehicleType>   |
      | licenseNumber | <licenseNumber> |
      | zone          | <zone>          |
    Then the response status should be <statusCode>
    And the response body should contain error "<message>"

    Examples:
      | firstName | lastName | email              | phone         | password     | vehicleType | licenseNumber | zone           | statusCode | message                    |
      |           | Machava  | carlos@bizno.co.mz | +258841234567 | Password@123 | MOTORCYCLE  | AB123456      | Maputo Central | 400        | First name is required     |
      | Carlos    |          | carlos@bizno.co.mz | +258841234567 | Password@123 | MOTORCYCLE  | AB123456      | Maputo Central | 400        | Last name is required      |
      | Carlos    | Machava  |                    | +258841234567 | Password@123 | MOTORCYCLE  | AB123456      | Maputo Central | 400        | E-mail is required         |
      | Carlos    | Machava  | carlos@bizno.co.mz |               | Password@123 | MOTORCYCLE  | AB123456      | Maputo Central | 400        | Phone is required          |
      | Carlos    | Machava  | carlos@bizno.co.mz | +258841234567 |              | MOTORCYCLE  | AB123456      | Maputo Central | 400        | Password is required       |
      | Carlos    | Machava  | carlos@bizno.co.mz | +258841234567 | Password@123 | MOTORCYCLE  |               | Maputo Central | 400        | License number is required |
      | Carlos    | Machava  | carlos@bizno.co.mz | +258841234567 | Password@123 | MOTORCYCLE  | AB123456      |                | 400        | Zone is required           |

  Scenario Outline: Reject registration with invalid field format
    Given i am authenticated as a Supper Admin
    When i send a POST request register courier using endpoint "/couriers/register" with:
      | firstName     | <firstName>     |
      | lastName      | <lastName>      |
      | email         | <email>         |
      | phone         | <phone>         |
      | password      | <password>      |
      | vehicleType   | <vehicleType>   |
      | licenseNumber | <licenseNumber> |
      | zone          | <zone>          |
    Then the response status should be <statusCode>
    And the response body should contain error "<message>"

    Examples:
      | firstName | lastName | email         | phone         | password     | vehicleType | licenseNumber | zone           | statusCode | message        |
      | Carlos    | Machava  | invalid-email | +258841234567 | Password@123 | MOTORCYCLE  | AB123456      | Maputo Central | 422        | Invalid E-mail |