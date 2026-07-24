Feature: Approve Seller
  As a Supper Admin
  I want to approve a pending seller registration
  So that the seller can access their dashboard and start listing products

  Acceptance Criteria:
  * Only a Supper Admin can approve a seller
  * Only sellers with status AWAITING_APPROVAL can be approved
  * After approval the seller status becomes ACTIVE
  * The seller receives an email notifying them of the approval

  Scenario: Successfully approve a pending seller
    Given an existing seller and with status "AWAITING_APPROVAL"
    And i am authenticated as a Supper Admin
    When i send a PATCH request to approve seller using endpoint "/sellers/sellerId/approve"
    Then the response status should be 200
    And the seller status should be "ACTIVE"

  Scenario: Reject approval if seller is already active
    Given an existing seller and with status "ACTIVE"
    And i am authenticated as a Supper Admin
    When i send a PATCH request to approve seller using endpoint "/sellers/sellerId/approve"
    Then the response status should be 400
    And the response body should contain error "Can only perform this action to Sellers with status AWAITING_APPROVAL"

  Scenario: Reject approval if seller was previously rejected
    Given an existing seller and with status "REJECTED"
    And i am authenticated as a Supper Admin
    When i send a PATCH request to approve seller using endpoint "/sellers/sellerId/approve"
    Then the response status should be 400
    And the response body should contain error "Can only perform this action to Sellers with status AWAITING_APPROVAL"

  Scenario: Reject approval if seller does not exist
    Given no seller exists with id "f47ac10b-58cc-4372-a567-0e02b2c3d479"
    And i am authenticated as a Supper Admin
    When i send a PATCH request to approve seller using endpoint "/sellers/sellerId/approve"
    Then the response status should be 404
    And the response body should contain error "Seller not found"

  Scenario: Reject approval if requester is not a Supper Admin
    Given an existing seller and with status "AWAITING_APPROVAL"
    And i'm authenticated as a Buyer
    When i send a PATCH request to approve seller using endpoint "/sellers/sellerId/approve"
    Then the response status should be 403
    And the response body should contain error "Access denied"