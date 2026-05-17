Feature: Register Buyer
  As a person interested in buying on Bizno Marketplace
  I want to register as a buyer
  So that I can browse and purchase products from local merchants

  Background:
    Given the system is available

  Scenario: Successfully register a new buyer
    Given no buyer exists with email "ana.machava@gmail.com"
    When I submit a registration request with:
      | firstName     | Ana                   |
      | lastName      | Machava               |
      | email         | ana.machava@gmail.com |
      | phone         | +258841234567         |
      | password      | segura123             |
      | latitude      | -25.9692              |
      | longitude     | 32.5732               |
      | street        | Av. Eduardo Mondlane  |
      | neighbourhood | Sommerschield         |
      | city          | Maputo                |
      | province      | Maputo                |
      | country       | Mozambique            |
    Then the buyer account is created with status "PENDING"
    And the account expires in 2 days
    And a "auth.buyer.registered" event is published to NATS

  Scenario: Attempt to register with an already registered email
    Given a buyer already exists with email "ana.machava@gmail.com"
    When I submit a registration request with:
      | firstName     | Ana                   |
      | lastName      | Machava               |
      | email         | ana.machava@gmail.com |
      | phone         | +258841234567         |
      | password      | segura123             |
      | latitude      | -25.9692              |
      | longitude     | 32.5732               |
      | street        |                       |
      | neighbourhood |                       |
      | city          |                       |
      | province      |                       |
      | country       |                       |
    Then the registration is rejected with error "Buyer already registered with email: ana.machava@gmail.com"
    And no event is published to NATS

  Scenario Outline: Expiration job handles buyer accounts correctly
    Given a buyer was registered 2 days ago with status "<status>"
    When the expiration job runs
    Then <outcome>

    Examples:
      | status  | outcome                                      |
      | PENDING | the buyer account is removed from the system |
      | ACTIVE  | the buyer account remains in the system      |

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
    Then the registration is rejected with error "<error>"

    Examples:
      | firstName | lastName | email                 | phone         | password  | latitude | longitude | street               | neighbourhood | city   | province | country    | error                                  |
      |           | Machava  | ana.machava@gmail.com | +258841234567 | segura123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | First name is required                 |
      | Ana       |          | ana.machava@gmail.com | +258841234567 | segura123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | Last name is required                  |
      | Ana       | Machava  |                       | +258841234567 | segura123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | Email is required                      |
      | Ana       | Machava  | not-an-email          | +258841234567 | segura123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | Invalid email format                   |
      | Ana       | Machava  | ana.machava@gmail.com |               | segura123 | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | Phone is required                      |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 |           | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | Password is required                   |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | abc123    | -25.9692 | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | Password must be at least 8 characters |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | segura123 |          |           | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | Latitude is required                   |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | segura123 | -25.9692 |           | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | Longitude is required                  |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | segura123 | -999     | 32.5732   | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | Invalid latitude value                 |
      | Ana       | Machava  | ana.machava@gmail.com | +258841234567 | segura123 | -25.9692 | 999       | Av. Eduardo Mondlane | Sommerschield | Maputo | Maputo   | Mozambique | Invalid longitude value                |