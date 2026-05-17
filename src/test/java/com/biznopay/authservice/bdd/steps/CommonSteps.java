package com.biznopay.authservice.bdd.steps;

import com.biznopay.authservice.bdd.ScenarioContext;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.dto.RegisterSARequest;
import com.biznopay.authservice.infra.dto.ResendConfirmationRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class CommonSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @When("i send a POST request to {string} with:")
    public void iSendAPOSTRequestToWith(String path, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        Object request = switch (path) {
            case "/supper-admins" -> new RegisterSARequest(
                    data.get("firstName"),
                    data.get("lastName"),
                    data.get("email"),
                    data.get("password")
            );
            case "/accounts/resend-confirmation" -> new ResendConfirmationRequest(data.get("email"));
            default -> throw new IllegalArgumentException("Unknown path: " + path);
        };

        scenarioContext.setResponse(scenarioContext.getRestTemplate().postForEntity(scenarioContext.url(path), request, ApiResponse.class));
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int statusCode) {
        Assertions.assertEquals(statusCode, scenarioContext.getResponse().getStatusCode().value());
    }

    @And("the response body should contain error {string}")
    public void theResponseBodyShouldContainError(String error) {
        Assertions.assertEquals(error, scenarioContext.getResponse().getBody().error().message());
    }

    @And("the response body should contain message {string}")
    public void theResponseBodyShouldContainMessage(String message) {
        Assertions.assertEquals(message, scenarioContext.getResponse().getBody().data());
    }
}