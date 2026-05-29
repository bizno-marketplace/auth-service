Feature: Register Seller
  As a merchant in the informal market of Maputo
  I want to register my store on Bizno Marketplace
  So that I can list products and receive orders with payment protection

  Scenario: Successfully register a new seller
    Given no seller exists with email "joao.tembe@gmail.com"
    When i send a multipart POST request to "/sellers" with data:
      | firstName        | João                              |
      | lastName         | Tembe                             |
      | email            | joao.tembe@gmail.com              |
      | phoneNumber      | +258841234567                     |
      | password         | Segura@123                        |
      | storeName        | Tembe Electronics                 |
      | storeDescription | Venda de electrónica e acessórios |
      | latitude         | -25.9692                          |
      | longitude        | 32.5732                           |
      | street           | Av. 24 de Julho                   |
      | neighbourhood    | Sommerschield                     |
      | city             | Maputo                            |
      | province         | Maputo                            |
      | country          | Mozambique                        |
      | nuit             | 400123456                         |
    And with files:
      | field        | filename      | contentType |
      | biFrontPhoto | bi_frente.png | image/png   |
      | biBackPhoto  | bi_verso.png  | image/png   |
    Then the response status should be 200
    And the response body should contain message "We've sent an activation link to provided email: joao.tembe@gmail.com"

  Scenario: Attempt to register with an already registered email
    Given a user with email "joao.tembe@gmail.com" exists in the system
    When i send a multipart POST request to "/sellers" with data:
      | firstName        | João                              |
      | lastName         | Tembe                             |
      | email            | joao.tembe@gmail.com              |
      | phoneNumber      | +258841234567                     |
      | password         | Segura@123                        |
      | storeName        | Tembe Electronics                 |
      | storeDescription | Venda de electrónica e acessórios |
      | latitude         | -25.9692                          |
      | longitude        | 32.5732                           |
      | street           | Av. 24 de Julho                   |
      | neighbourhood    | Sommerschield                     |
      | city             | Maputo                            |
      | province         | Maputo                            |
      | country          | Mozambique                        |
      | nuit             | 400123456                         |
    And with files:
      | field        | filename      | contentType |
      | biFrontPhoto | bi_frente.png | image/png   |
      | biBackPhoto  | bi_verso.png  | image/png   |
    Then the response status should be 409
    And the response body should contain error "E-mail already in use"

  Scenario: Attempt to register with an already registered NUIT
    Given a seller already exists with nuit "400123456"
    When i send a multipart POST request to "/sellers" with data:
      | firstName        | Carlos                   |
      | lastName         | Sitoe                    |
      | email            | carlos.sitoe@gmail.com   |
      | phoneNumber      | +258842345678            |
      | password         | Segura@123               |
      | storeName        | Sitoe Store              |
      | storeDescription | Venda de roupa e calçado |
      | latitude         | -25.9692                 |
      | longitude        | 32.5732                  |
      | street           | Av. Karl Marx            |
      | neighbourhood    | Polana                   |
      | city             | Maputo                   |
      | province         | Maputo                   |
      | country          | Mozambique               |
      | nuit             | 400123456                |
    And with files:
      | field        | filename      | contentType |
      | biFrontPhoto | bi_frente.png | image/png   |
      | biBackPhoto  | bi_verso.png  | image/png   |
    Then the response status should be 409
    And the response body should contain error "Nuit already in use"

  Scenario: Reject registration if BI front photo is missing
    When i send a multipart POST request to "/sellers" with data:
      | firstName        | João                              |
      | lastName         | Tembe                             |
      | email            | joao.tembe@gmail.com              |
      | phoneNumber      | +258841234567                     |
      | password         | Segura@123                        |
      | storeName        | Tembe Electronics                 |
      | storeDescription | Venda de electrónica e acessórios |
      | latitude         | -25.9692                          |
      | longitude        | 32.5732                           |
      | street           | Av. 24 de Julho                   |
      | neighbourhood    | Sommerschield                     |
      | city             | Maputo                            |
      | province         | Maputo                            |
      | country          | Mozambique                        |
      | nuit             | 400123456                         |
    And with files:
      | field       | filename     | contentType |
      | biBackPhoto | bi_verso.png | image/png   |
    Then the response status should be 400
    And the response body should contain error "BI front photo is required"

  Scenario: Reject registration if BI back photo is missing
    When i send a multipart POST request to "/sellers" with data:
      | firstName        | João                              |
      | lastName         | Tembe                             |
      | email            | joao.tembe@gmail.com              |
      | phoneNumber      | +258841234567                     |
      | password         | Segura@123                        |
      | storeName        | Tembe Electronics                 |
      | storeDescription | Venda de electrónica e acessórios |
      | latitude         | -25.9692                          |
      | longitude        | 32.5732                           |
      | street           | Av. 24 de Julho                   |
      | neighbourhood    | Sommerschield                     |
      | city             | Maputo                            |
      | province         | Maputo                            |
      | country          | Mozambique                        |
      | nuit             | 400123456                         |
    And with files:
      | field        | filename      | contentType |
      | biFrontPhoto | bi_frente.png | image/png   |
    Then the response status should be 400
    And the response body should contain error "BI back photo is required"

  Scenario Outline: Attempt to register with invalid or missing fields
    When i send a multipart POST request to "/sellers" with data:
      | firstName        | <firstName>        |
      | lastName         | <lastName>         |
      | email            | <email>            |
      | phoneNumber      | <phone>            |
      | password         | <password>         |
      | storeName        | <storeName>        |
      | storeDescription | <storeDescription> |
      | latitude         | <latitude>         |
      | longitude        | <longitude>        |
      | street           | <street>           |
      | neighbourhood    | <neighbourhood>    |
      | city             | <city>             |
      | province         | <province>         |
      | country          | <country>          |
      | nuit             | <nuit>             |
    And with files:
      | field        | filename      | contentType |
      | biFrontPhoto | bi_frente.png | image/png   |
      | biBackPhoto  | bi_verso.png  | image/png   |
    Then the response status should be <statusCode>
    And the response body should contain error "<error>"

    Examples:
      | firstName | lastName | email                | phone         | password   | storeName         | storeDescription                  | latitude | longitude | street          | neighbourhood | city   | province | country    | nuit      | statusCode | error                                                                                                                                              |
      |           | Tembe    | joao.tembe@gmail.com | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 400        | First name is required                                                                                                                             |
      | João      |          | joao.tembe@gmail.com | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 400        | Last name is required                                                                                                                              |
      | João      | Tembe    |                      | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 400        | E-mail is required                                                                                                                                 |
      | João      | Tembe    | not-an-email         | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 400        | Invalid E-mail                                                                                                                                     |
      | João      | Tembe    | joao.tembe@gmail.com |               | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 400        | Phone number is required                                                                                                                           |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 |            | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 400        | Password is required                                                                                                                               |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 | abc123     | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 422        | Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 | Segura@123 |                   | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 400        | Store name is required                                                                                                                             |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 | Segura@123 | Tembe Electronics |                                   | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 400        | Store description is required                                                                                                                      |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios |          | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 400        | Latitude is required                                                                                                                               |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 |           | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 400        | Longitude is required                                                                                                                              |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -999     | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 422        | Invalid Latitude on Address                                                                                                                        |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 999       | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 400123456 | 422        | Invalid Longitude on Address                                                                                                                       |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique |           | 400        | NUIT is required                                                                                                                                   |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | 12345     | 422        | NUIT must contain only digits and must be exactly 9 digits                                                                                         |
      | João      | Tembe    | joao.tembe@gmail.com | +258841234567 | Segura@123 | Tembe Electronics | Venda de electrónica e acessórios | -25.9692 | 32.5732   | Av. 24 de Julho | Sommerschield | Maputo | Maputo   | Mozambique | ABCDEFGHI | 422        | NUIT must contain only digits and must be exactly 9 digits                                                                                         |