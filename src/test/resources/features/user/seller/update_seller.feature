Feature: Update Seller
  As a Seller
  I want to update my store profile information
  So that my store details stay accurate and up to date

  Acceptance Criteria:
  * Only the authenticated seller can update their own profile
  * The update is a partial PATCH — only provided fields are changed
  * Unprovided fields remain unchanged
  * If the email is changed, the account status becomes PENDING and a new
  activation email is sent to confirm the new address
  * If the email is not changed, the account status becomes AWAITING_APPROVAL,
  requiring re-approval by a Platform Admin
  * A non-seller user (e.g. Buyer) cannot access this endpoint

  Scenario: Reject update when no authentication token is provided
    When i send a PATCH request to update seller using endpoint "/sellers/update" without an authorization header
    Then the response status should be 403

  Scenario: Reject update when logged user is not a Seller
    Given a Buyer is authenticated
    When i send a PATCH request to update seller using endpoint "/sellers/update" with:
      | storeName | Nova Loja |
    Then the response status should be 403
    And the response body should contain error "Access denied"

  Scenario: Successfully update store fields without changing email
    Given a seller exists with status "ACTIVE"
    And i am authenticated as that seller
    When i send a PATCH request to update seller using endpoint "/sellers/update" with:
      | storeName        | Nova Loja Dombo        |
      | storeDescription | Nova descrição da loja |
    Then the response status should be 200
    And the response body should contain message "Seller updated successfully"
    And the seller status should be "AWAITING_APPROVAL"
    And the seller store name should be "Nova Loja Dombo"
    And the seller store description should be "Nova descrição da loja"

  Scenario: Keep unprovided fields unchanged after partial update
    Given a seller exists with status "ACTIVE" and first name "João"
    And i am authenticated as that seller
    When i send a PATCH request to update seller using endpoint "/sellers/update" with:
      | storeName | Loja Actualizada |
    Then the response status should be 200
    And the seller first name should remain unchanged

  Scenario: Update email triggers PENDING status and sends activation email
    Given a seller exists with status "ACTIVE" and email "dombo@bizno.co.mz"
    And i am authenticated as that seller
    When i send a PATCH request to update seller using endpoint "/sellers/update" with:
      | email | dombo.novo@gmail.com |
    Then the response status should be 200
    And the response body should contain message "As you changed you email we've sent instruction to conform account in the provided email."
    And the seller status should be "PENDING"