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
    Given a seller exists with id "seller-uuid-123" and status "AWAITING_APPROVAL"
    And I am authenticated as a Supper Admin
    When I send a PATCH request to "/sellers/seller-uuid-123/approve"
    Then the response status should be 200
    And the seller status should be "ACTIVE"
    And the seller receives an approval email

  Scenario: Reject approval if seller is already active
    Given a seller exists with id "seller-uuid-123" and status "ACTIVE"
    And I am authenticated as a Supper Admin
    When I send a PATCH request to "/sellers/seller-uuid-123/approve"
    Then the response status should be 409
    And the response body should contain message "Seller account is already active"

  Scenario: Reject approval if seller was previously rejected
    Given a seller exists with id "seller-uuid-123" and status "REJECTED"
    And I am authenticated as a Supper Admin
    When I send a PATCH request to "/sellers/seller-uuid-123/approve"
    Then the response status should be 409
    And the response body should contain message "Cannot approve a rejected seller account"

  Scenario: Reject approval if seller does not exist
    Given no seller exists with id "non-existent-uuid"
    And I am authenticated as a Supper Admin
    When I send a PATCH request to "/sellers/non-existent-uuid/approve"
    Then the response status should be 404
    And the response body should contain message "Seller not found"

  Scenario: Reject approval if requester is not a Supper Admin
    Given a seller exists with id "seller-uuid-123" and status "AWAITING_APPROVAL"
    And I am authenticated as a Buyer
    When I send a PATCH request to "/sellers/seller-uuid-123/approve"
    Then the response status should be 403
    And the response body should contain message "Access denied"