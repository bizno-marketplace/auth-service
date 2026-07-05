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

  Scenario: Successfully resubmit without changing email
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH request to resubmit seller using endpoint "/sellers/sellerId/resubmit"
      | storeName | Loja Nova |
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"

  Scenario: Successfully resubmit with new BI document
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH request to resubmit seller using endpoint "/sellers/sellerId/resubmit" with new BI document
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"

  Scenario: Successfully resubmit changing email
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as the seller
    When i send a PATCH request to resubmit seller using endpoint "/sellers/sellerId/resubmit"
      | email | newseller@email.com |
    Then the response status should be 200
    And the seller status should be "PENDING_CONFIRMATION"

  Scenario: Successfully resubmit on last attempt
    Given a existing seller with status "REJECTED" and rejection count 2
    And i am authenticated as the seller
    When i send a PATCH request to resubmit seller using endpoint "/sellers/sellerId/resubmit"
      | storeName | Loja Nova |
    Then the response status should be 200
    And the seller status should be "AWAITING_APPROVAL"

  Scenario: Resubmit blocked after 3 rejections
    Given a existing seller with status "BLOCKED" and rejection count 3
    And i am authenticated as the seller
    When i send a PATCH request to resubmit seller using endpoint "/sellers/sellerId/resubmit"
      | storeName | Loja Nova |
    Then the response status should be 403
    And the response body should contain error "Account is blocked. Please contact the administrator"

  Scenario: Active seller tries to resubmit
    Given a existing seller with status "ACTIVE" and rejection count 0
    And i am authenticated as the seller
    When i send a PATCH request to resubmit seller using endpoint "/sellers/sellerId/resubmit"
      | storeName | Loja Nova |
    Then the response status should be 409
    And the response body should contain error "Can only perform this action to Sellers with status REJECTED"

  Scenario: Awaiting approval seller tries to resubmit
    Given a existing seller with status "AWAITING_APPROVAL" and rejection count 0
    And i am authenticated as the seller
    When i send a PATCH request to resubmit seller using endpoint "/sellers/sellerId/resubmit"
      | storeName | Loja Nova |
    Then the response status should be 409
    And the response body should contain error "Can only perform this action to Sellers with status REJECTED"

  Scenario: Seller tries to resubmit another seller account
    Given a existing seller with status "REJECTED" and rejection count 1
    And i am authenticated as a different seller
    When i send a PATCH request to resubmit seller using endpoint "/sellers/otherSellerId/resubmit"
      | storeName | Loja Nova |
    Then the response status should be 403
    And the response body should contain error "Access denied"