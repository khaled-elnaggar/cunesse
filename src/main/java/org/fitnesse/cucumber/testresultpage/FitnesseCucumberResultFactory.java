package org.fitnesse.cucumber.testresultpage;

import fitnesse.testsystems.ExecutionResult;
import io.cucumber.plugin.event.Status;

public class FitnesseCucumberResultFactory {
    public static FitnesseCucumberResult getFitnesseCucumberResult(Status status) {
        String statusStyle;
        ExecutionResult executionResult;

        //TODO: check other statuses in io.cucumber.plugin.event.Status.java
        switch (status) {
            case PASSED:
                statusStyle = "pass";
                executionResult = ExecutionResult.PASS;
                break;

            case FAILED:
                statusStyle = "fail";
                executionResult = ExecutionResult.FAIL;
                break;

            case SKIPPED:
            case PENDING:
            case AMBIGUOUS:
                statusStyle = "ignore";
                executionResult = ExecutionResult.IGNORE;
                break;

            default:
                statusStyle = "error";
                executionResult = ExecutionResult.ERROR;
        }

        return new FitnesseCucumberResult(executionResult, statusStyle);
    }
}
