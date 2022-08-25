package org.fitnesse.cucumber.testresultpage;

import fitnesse.testsystems.ExecutionResult;

public class FitnesseCucumberResult {
    private final ExecutionResult executionResult;
    private final String statusStyle;
    public FitnesseCucumberResult(ExecutionResult executionResult, String statusStyle) {
        this.executionResult = executionResult;
        this.statusStyle = statusStyle;
    }

    public ExecutionResult getExecutionResult() {
        return executionResult;
    }

    public String getStatusStyle() {
        return statusStyle;
    }
}
