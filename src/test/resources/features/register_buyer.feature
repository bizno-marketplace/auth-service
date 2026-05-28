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
      | password      | Segura@123            |
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
    Given a user with email "ana.machava@gmail.com" exists in the system
    When i send a POST request to "/buyers" with:
      | firstName     | Ana                   |
      | lastName      | Machava               |
      | email         | ana.machava@gmail.com |
      | phoneNumber   | +258841234567         |
      | password      | Segura@123            |
      | latitude      | -25.9692              |
      | longitude     | 32.5732               |
      | street        |                       |
      | neighbourhood |                       |
      | city          |                       |
      | province      |                       |
      | country       |                       |
    Then the response status should be 409
    And the response body should contain error "E-mail already in use"

  Scenario Outline: Attempt to register with invalid or missing fields
    When i send a POST request to "/buyers" with:
      | firstName     | <firstName>     |
      | lastName      | <lastName>      |
      | email         | <email>         |
      | phoneNumber   | <phone>         |
      | password      | <password>      |
      | latitude      | <latitude>      |
      | longitude     | <longitude>     |
      | street        | <street>        |
      | neighbourhood | <neighbourhood> |
      | city          | <city>          |
      | province      | <province>      |
      | country       | <country>       |
    Then the response status should be <statusCode>
    And the response body should contain error "<error>"

    Examples:
      | firstName | lastName | email                 | phone         | password   | latitude | longitude | street               | neighbourhood | city   | province | country    | statusCode | error                                                                                                                                              |
      |           | Machava  | ana.machava@gmail.com | +258841234567 | Segura@123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | First name is required                                                                                                                             |
      | Ana       |          | ana.machava@gmail.com | +258841234567 | Segura@123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Last name is required                                                                                                                              |
      | Ana       | Machava  |                       | +258841234567 | Segura@123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | E-mail is required                                                                                                                                 |
      | Ana       | Machava  | not-an-email          | +258841234567 | Segura@123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Invalid E-mail                                                                                                                                     |
      | Ana       | Machava  | ana.machava@gmail.com |               | Segura@123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Phone number is required                                                                                                                           |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 |            | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Password is required                                                                                                                               |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | abc123     | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 422        | Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | Segura@123 |          |           | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Latitude is required                                                                                                                               |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | Segura@123 | -25.9692 |           | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 400        | Longitude is required                                                                                                                              |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | Segura@123 | -999     | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 422        | Invalid Latitude on Address                                                                                                                        |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | Segura@123 | -25.9692 | 999       | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | 422        | Invalid Longitude on Address                                                                                                                       |