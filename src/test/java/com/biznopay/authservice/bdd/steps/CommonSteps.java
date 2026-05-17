package com.biznopay.authservice.bdd.steps;

import com.biznopay.authservice.bdd.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonSteps {

    @Autowired
    private ScenarioContext scenarioContext;

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