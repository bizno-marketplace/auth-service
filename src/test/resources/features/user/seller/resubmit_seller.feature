Feature: Resubmit Seller
  As a Seller
  I want to resubmit my registration after rejection
  So that i can correct my data and get approved

  Acceptance Criteria:
  * Only the seller who owns the account can resubmit
  * Only sellers with status REJECTED can resubmit
  * The seller can update their data and optionally submit a new BI document
  * After resubmission the seller status becomes AWAITING_APPROVAL
  * If the seller changes their email the status becomes PENDING_CONFIRMATION
  * The resubmission is recorded in the rejection history
  * If the seller reaches 3 rejections the account becomes BLOCKED
  * The admin receives a notification after resubmission

  Scenario: Successfully resubmit updating first name
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | firstName | NovoNome |
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"
    And the seller first name should be "NovoNome"

  Scenario: Successfully resubmit updating last name
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | lastName | NovoApelido |
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"
    And the seller last name should be "NovoApelido"

  Scenario: Successfully resubmit updating phone
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | phoneNumber | +258841234567 |
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"
    And the seller phone should be "+258841234567"

  Scenario: Successfully resubmit updating store name
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | storeName | Nova Loja XYZ |
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"
    And the seller store name should be "Nova Loja XYZ"

  Scenario: Successfully resubmit updating store description
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | storeDescription | Nova descricao da loja |
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"
    And the seller store description should be "Nova descricao da loja"

  Scenario: Successfully resubmit updating nuit
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | nuit | 987654321 |
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"
    And the seller nuit should be "987654321"

  Scenario: Successfully resubmit with new BI document
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | storeName | Loja Nova |
    And with files for resubmit:
      | field        | filename      | contentType |
      | biFrontPhoto | bi_frente.png | image/png   |
      | biBackPhoto  | bi_verso.png  | image/png   |
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"

  Scenario: Successfully resubmit changing email
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | email | newseller@email.com |
    Then the response status should be 200
    And the seller status should be "PENDING"

  Scenario: Successfully resubmit on last attempt
    Given a existing seller with status "REJECTED" and rejection count 2
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | storeName | Loja Nova |
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"

  Scenario: Resubmit blocked after 3 rejections
    Given a existing seller with status "BLOCKED" and rejection count 3
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | storeName | Loja Nova |
    Then the response status should be 403
    And the response body should contain error "Access denied"

  Scenario: Active seller tries to resubmit
    Given a existing seller with status "ACTIVE" and rejection count 0
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | storeName | Loja Nova |
    Then the response status should be 403
    And the response body should contain error "Access denied"

  Scenario: Awaiting approval seller tries to resubmit
    Given a existing seller with status "AWAITING_APPROVAL" and rejection count 0
    And i am authenticated as the seller
    When i send a PATCH multipart request to resubmit seller using endpoint "/sellers/resubmit"
      | storeName | Loja Nova |
    Then the response status should be 403
    And the response body should contain error "Access denied"