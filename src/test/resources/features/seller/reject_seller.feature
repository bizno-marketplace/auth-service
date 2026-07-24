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
    Given a existing seller with status "AWAITING_APPROVAL" and rejection count 0
    And i am authenticated as a Supper Admin
    When i send a PATCH request to reject seller using endpoint "/sellers/sellerId/reject"
      | reason | NUIT inválido — verifique o número e resubmeta |
    Then the response status should be 200
    And the seller status should be "REJECTED"
    And the seller rejection count should be 1

  Scenario: Block seller after 3 rejections
    Given a existing seller with status "AWAITING_APPROVAL" and rejection count 3
    And i am authenticated as a Supper Admin
    When i send a PATCH request to reject seller using endpoint "/sellers/sellerId/reject"
      | reason | Documentação inválida pela terceira vez |
    Then the response status should be 200
    And the seller status should be "BLOCKED"
    And the seller rejection count should be 3

  Scenario: Reject rejection if seller is not in AWAITING_APPROVAL
    Given a existing seller with status "ACTIVE" and rejection count 0
    And i am authenticated as a Supper Admin
    When i send a PATCH request to reject seller using endpoint "/sellers/sellerId/reject"
      | reason | Qualquer motivo |
    Then the response status should be 400
    And the response body should contain error "Can only perform this action to Sellers with status AWAITING_APPROVAL"

  Scenario: Reject rejection if reason is missing
    Given a existing seller with status "AWAITING_APPROVAL" and rejection count 0
    And i am authenticated as a Supper Admin
    When i send a PATCH request to reject seller using endpoint "/sellers/sellerId/reject"
      | reason |  |
    Then the response status should be 400
    And the response body should contain error "Reason for rejection is required"

  Scenario: Reject rejection if seller does not exist
    Given non existing seller and rejection count 0
    And i am authenticated as a Supper Admin
    When i send a PATCH request to reject seller using endpoint "/sellers/sellerId/reject"
      | reason | Qualquer motivo |
    Then the response status should be 404
    And the response body should contain error "Seller not found"

  Scenario: Reject rejection if requester is not a Supper Admin
    Given a existing seller with status "AWAITING_APPROVAL" and rejection count 0
    And i'm authenticated as a Buyer
    When i send a PATCH request to reject seller using endpoint "/sellers/sellerId/reject"
      | reason | Qualquer motivo |
    Then the response status should be 403
    And the response body should contain error "Access denied"