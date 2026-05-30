package com.biznopay.authservice.bdd.steps;

import com.biznopay.authservice.bdd.ScenarioContext;
import com.biznopay.authservice.domain.entity.user.Address;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.presentation.dto.AddressRequest;
import com.biznopay.authservice.presentation.dto.RegisterBuyerRequest;
import com.biznopay.authservice.presentation.dto.RegisterSARequest;
import com.biznopay.authservice.presentation.dto.ResendConfirmationRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import tools.jackson.core.type.TypeReference;

import java.util.Map;

import static com.biznopay.authservice.testcases.BuyerTestCases.validBuyer;

public class CommonSteps {

    @Autowired
    private ScenarioContext scenarioContext;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @When("i send a POST request to {string} with:")
    public void iSendAPOSTRequestToWith(String path, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        Object request = switch (path) {
            case "/supper-admins/register" -> new RegisterSARequest(
                    data.get("firstName"),
                    data.get("lastName"),
                    data.get("email"),
                    data.get("password")
            );
            case "/accounts/resend-confirmation" -> new ResendConfirmationRequest(data.get("email"));
            case "/buyers/register" -> {
                AddressRequest address = new AddressRequest(
                        data.get("latitude") != null && !data.get("latitude").isBlank() ? Double.parseDouble(data.get("latitude")) : null,
                        data.get("longitude") != null && !data.get("longitude").isBlank() ? Double.parseDouble(data.get("longitude")) : null,
                        data.get("street"),
                        data.get("neighbourhood"),
                        data.get("city"),
                        data.get("province"),
                        data.get("country")
                );
                yield new RegisterBuyerRequest(
                        data.get("firstName"),
                        data.get("lastName"),
                        data.get("email"),
                        data.get("password"),
                        data.get("phoneNumber"),
                        address
                );
            }
            default -> throw new IllegalArgumentException("Unknown path: " + path);
        };

        scenarioContext.setResponse(scenarioContext.getRestTemplate().postForEntity(scenarioContext.url(path), request, ApiResponse.class));
    }

    @When("i send a GET request to {string}")
    public void iSendAGetRequestTo(String path) {
        scenarioContext.setResponse(scenarioContext.getRestTemplate().getForEntity(scenarioContext.url(path), ApiResponse.class));
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        System.out.println(scenarioContext.getRequestData());
        Assertions.assertEquals(statusCode, scenarioContext.getResponse().getStatusCode().value());
    }

    @And("the response body should contain error {string}")
    public void theResponseBodyShouldContainError(String error) {
        Assertions.assertEquals(error, scenarioContext.getResponse().getBody().error().message());
    }

    @And("the response body should contain message {string}")
    public void theResponseBodyShouldContainMessage(String message) {
        Object data = scenarioContext.getResponse().getBody().data();
        Map<String, Object> dataMap = scenarioContext.getObjectMapper().convertValue(data, new TypeReference<Map<String, Object>>() {
        });
        Assertions.assertEquals(message, dataMap.get("message"));
    }

    @And("the confirmation email should have a link that expires after 15 minutes")
    public void theConfirmationEmailShouldHaveALinkThatExpiresAfter15Minutes() {
        Map<String, Object> event = scenarioContext.getJdbcTemplate().queryForMap(
                "SELECT payload FROM t_outbox_events WHERE status = 'PENDING' ORDER BY created_at DESC LIMIT 1"
        );
        String payload = (String) event.get("payload");
        Assertions.assertTrue(payload.contains("activationTokenId"));
    }

    @Given("a user with email {string} exists in the system")
    public void aUserWithEmailExistsInTheSystem(String email) {
        User user = validBuyer(email);;
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);
    }
}