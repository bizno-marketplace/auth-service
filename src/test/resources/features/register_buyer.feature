Feature: Register Buyer
  As a person interested in buying on Bizno Marketplace
  I want to register as a buyer
  So that I can browse and purchase products from local merchants

  Scenario: Successfully register a new buyer
    Given no buyer exists with email "ana.machava@gmail.com"
    When i send a POST request to "/buyers" with:
      | firstName     | Ana                   |
      | lastName      | Machava               |
      | email         | ana.machava@gmail.com |
      | phoneNumber   | +258841234567         |
      | password      | Segura@123             |
      | latitude      | -25.9692              |
      | longitude     | 32.5732               |
      | street        | Av. Eduardo Mondlane  |
      | neighbourhood | Sommerschield         |
      | city          | Maputo                |
      | province      | Maputo                |
      | country       | Mozambique            |
    Then the response status should be 200
    And the buyer account is created with status "PENDING"
    And the account expires in 2 days
    And the confirmation email should have a link that expires after 15 minutes
    And the response body should contain message "We've sent an activation link to provided email: ana.machava@gmail.com"

  Scenario: Attempt to register with an already registered email
    Given a buyer already exists with email "ana.machava@gmail.com"
    When I submit a registration request with:
      | firstName     | Ana                   |
      | lastName      | Machava               |
      | email         | ana.machava@gmail.com |
      | phoneNumber   | +258841234567         |
      | password      | segura123             |
      | latitude      | -25.9692              |
      | longitude     | 32.5732               |
      | street        |                       |
      | neighbourhood |                       |
      | city          |                       |
      | province      |                       |
      | country       |                       |
    Then the response status should be 409
    And the response body should contain error "Email already in use"
    And no event is published to NATS

  Scenario Outline: Attempt to register with invalid or missing fields
    When I submit a registration request with:
      | firstName     | <firstName>     |
      | lastName      | <lastName>      |
      | email         | <email>         |
      | phone         | <phone>         |
      | password      | <password>      |
      | latitude      | <latitude>      |
      | longitude     | <longitude>     |
      | street        | <street>        |
      | neighbourhood | <neighbourhood> |
      | city          | <city>          |
      | province      | <province>      |
      | country       | <country>       |
    Then the response status should be <statusCode>
    And the registration is rejected with error "<error>"

    Examples:
      | firstName | lastName | email                 | phone         | password  | latitude | longitude | street               | neighbourhood | city   | province | country    | statusCode | error                                  |
      |           | Machava  | ana.machava@gmail.com | +258841234567 | segura123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | First name is required                 |
      | Ana       |          | ana.machava@gmail.com | +258841234567 | segura123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Last name is required                  |
      | Ana       | Machava  |                       | +258841234567 | segura123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Email is required                      |
      | Ana       | Machava  | not-an-email          | +258841234567 | segura123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Invalid email format                   |
      | Ana       | Machava  | ana.machava@gmail.com |               | segura123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Phone is required                      |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 |           | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Password is required                   |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | abc123    | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Password must be at least 8 characters |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | segura123 |          |           | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Latitude is required                   |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | segura123 | -25.9692 |           | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Longitude is required                  |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | segura123 | -999     | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 422        | Invalid latitude value                 |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | segura123 | -25.9692 | 999       | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 422        | Invalid longitude value                |