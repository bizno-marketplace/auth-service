Feature: Reject Seller
  As a Seller Admin
  I want to reject a pending seller registration with a reason
  So that the seller knows what to correct before resubmitting

  Acceptance Criteria:
  * Only a Seller Admin can reject a seller
  * Only sellers with status PENDING_APPROVAL can be rejected
  * The Admin must provide a rejection reason
  * After rejection the seller status becomes REJECTED
  * The rejection is recorded in the approval history
  * The rejection counter is incremented
  * If the seller reaches 3 rejections the account becomes BLOCKED
  * The seller receives an email with the rejection reason

  Scenario: Successfully reject a pending seller
    Given a seller exists with id "seller-uuid-123" and status "PENDING_APPROVAL" and rejection count 0
    And I am authenticated as a Seller Admin
    When I send a PATCH request to "/sellers/seller-uuid-123/reject" with:
      | reason | NUIT inválido — verifique o número e resubmeta |
    Then the response status should be 200
    And the seller status should be "REJECTED"
    And the seller rejection count should be 1
    And the rejection is recorded in the approval history
    And the seller receives a rejection email with the reason

  Scenario: Block seller after 3 rejections
    Given a seller exists with id "seller-uuid-123" and status "PENDING_APPROVAL" and rejection count 2
    And I am authenticated as a Seller Admin
    When I send a PATCH request to "/sellers/seller-uuid-123/reject" with:
      | reason | Documentação inválida pela terceira vez |
    Then the response status should be 200
    And the seller status should be "BLOCKED"
    And the seller rejection count should be 3
    And the seller receives an email notifying the account is permanently blocked

  Scenario: Reject rejection if seller is not in PENDING_APPROVAL
    Given a seller exists with id "seller-uuid-123" and status "ACTIVE"
    And I am authenticated as a Seller Admin
    When I send a PATCH request to "/sellers/seller-uuid-123/reject" with:
      | reason | Qualquer motivo |
    Then the response status should be 409
    And the response body should contain message "Only pending sellers can be rejected"

  Scenario: Reject rejection if reason is missing
    Given a seller exists with id "seller-uuid-123" and status "PENDING_APPROVAL" and rejection count 0
    And I am authenticated as a Seller Admin
    When I send a PATCH request to "/sellers/seller-uuid-123/reject" with:
      | reason |  |
    Then the response status should be 400
    And the response body should contain message "Rejection reason is required"

  Scenario: Reject rejection if seller does not exist
    Given no seller exists with id "non-existent-uuid"
    And I am authenticated as a Seller Admin
    When I send a PATCH request to "/sellers/non-existent-uuid/reject" with:
      | reason | Qualquer motivo |
    Then the response status should be 404
    And the response body should contain message "Seller not found"

  Scenario: Reject rejection if requester is not a Seller Admin
    Given a seller exists with id "seller-uuid-123" and status "PENDING_APPROVAL" and rejection count 0
    And I am authenticated as a Buyer
    When I send a PATCH request to "/sellers/seller-uuid-123/reject" with:
      | reason | Qualquer motivo |
    Then the response status should be 403
    And the response body should contain message "Access denied"