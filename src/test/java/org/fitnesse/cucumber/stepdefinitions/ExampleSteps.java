package org.fitnesse.cucumber.stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ExampleSteps {
    int x;

    @Before("@withBefore")
    public void beforeScenario() {
        System.out.println("Hook running before scenario");
    }

    @After("@withAfter")
    public void afterScenario() {
        System.out.println("After scenario");
    }

    @Before("@withFailingBefore")
    public void beforeScenarioFails() {
        System.out.println("Before scenario");
        throw new RuntimeException("Something went wrong at runtime");
    }

    @After("@withFailingAfter")
    public void afterScenarioFails() {
        System.out.println("After scenario");
        throw new RuntimeException("Something went wrong at runtime");
    }

    @Given("^a variable x with value (\\d+)$")
    public void givenXValue(int value) {
        x = value;
    }

    @When("^I multiply x by (\\d+)$")
    public void whenImultiplyXBy(int value) {
        x = x * value;
    }

    @Then("^x should equal (\\d+)$")
    public void thenXshouldBe(int value) {
        if (value != x)
            throw new AssertionError("x is " + value + ", but should be " + x);
    }

    @Then("throw exception")
    public void throwException() {
        throw new RuntimeException("some application exception hehe");
    }
}