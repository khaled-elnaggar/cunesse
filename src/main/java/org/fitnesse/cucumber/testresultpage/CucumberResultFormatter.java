package org.fitnesse.cucumber.testresultpage;

import fitnesse.testsystems.TestSummary;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.HookTestStep;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.SnippetsSuggestedEvent;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.Step;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestSourceRead;
import io.cucumber.plugin.event.TestStep;
import io.cucumber.plugin.event.TestStepFinished;
import org.fitnesse.cucumber.FitnessePrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CucumberResultFormatter implements EventListener {
    private final TestSummary testSummary;
    private final FitnessePrinter printer;
    private String currentScenario;

    private String currentFeature;

    private final List<SnippetsSuggestedEvent> undefinedSteps = new ArrayList<>();

    public CucumberResultFormatter(final TestSummary testSummary, FitnessePrinter printer) {
        this.testSummary = testSummary;
        this.printer = printer;
    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestSourceRead.class, this::testSourceRead);
        eventPublisher.registerHandlerFor(SnippetsSuggestedEvent.class, this::snippetsSuggested);
        eventPublisher.registerHandlerFor(TestCaseStarted.class, this::testCaseStarted);
        eventPublisher.registerHandlerFor(TestStepFinished.class, this::testStepFinished);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::testCaseFinished);
    }

    private void testSourceRead(TestSourceRead e) {
        final String wholeSource = e.getSource();
        final String[] split = wholeSource.split("\n");
        for (String line : split) {
            if (line.trim().startsWith("Feature")) {
                this.currentFeature = line;
                print("h2", line);
                printer.printBreakLine();
                return;
            }
            print("h3", line);
        }
    }

    private void snippetsSuggested(SnippetsSuggestedEvent snippetsSuggestedEvent) {
        undefinedSteps.add(snippetsSuggestedEvent);
    }

    public void testCaseStarted(TestCaseStarted e) {
        //TODO: Review why Data Table doesn't get printed!
        this.currentScenario = e.getTestCase().getKeyword() + ": " + e.getTestCase().getName();
        if (!undefinedSteps.isEmpty()) {
            printer.printBreakLine();
            print("h4", currentScenario + ", undefined steps: ", "style=bold;");
            undefinedSteps.forEach(this::printMissingSteps);
        } else {
            printer.printBreakLine();
        }

        print("h4", currentScenario, "style=bold;");
    }

    private void printMissingSteps(SnippetsSuggestedEvent snippetsSuggestedEvent) {
        SnippetsSuggestedEvent.Suggestion suggestion = snippetsSuggestedEvent.getSuggestion();
        String stepString = suggestion.getStep();
        int stepLine = snippetsSuggestedEvent.getStepLocation().getLine() - 1;

        print("span", "Undefined step @ line " + stepLine, "class=error");
        print("b", " " + stepString);
        print("h4", "Pending methods");

        List<String> snippets = suggestion.getSnippets();
        for (String snippet : snippets) {
            print("pre", snippet);
        }
        printer.printBreakLine();
    }

    private void testStepFinished(TestStepFinished e) {
        TestStep t = e.getTestStep();
        if (t instanceof HookTestStep) {
            HookTestStep step = (HookTestStep) t;
            printer.executionLogOut("Running hook " + step.getHookType());
        }

        if (t instanceof PickleStepTestStep) {
            PickleStepTestStep p = (PickleStepTestStep) t;

            final Result result = e.getResult();

            final Status status = result.getStatus();
            String statusStyle = FitnesseCucumberResultFactory.getFitnesseCucumberResult(status).getStatusStyle();

            String style = "class=" + statusStyle;
            print("span", p.getStep().getText(), style);
            handleExceptions(result, p.getStep());
            printer.outputChunk("<br>");

        }
    }

    private void handleExceptions(Result result, Step step) {
        Throwable throwable = result.getError();
        if (throwable == null) {
            return;
        }

        print("span", "Check execution log for error: " + throwable.getMessage(), "class=error");
        printer.executionLogErr(currentFeature);
        printer.executionLogErr(currentScenario);
        //TODO: review the -1 here
        printer.executionLogErr("Line " + (step.getLocation().getLine() - 1) + ", Step: [" + step.getText() + "]");
        printer.executionLogErr("Error message: [" + throwable.getMessage() + "]");
        printer.executionLogErr("Stacktrace: [");
        Arrays.stream(throwable.getStackTrace())
                .forEach(element -> printer.executionLogErr(String.valueOf(element)));
        printer.executionLogErr("]");

        printer.executionLogErr("=========================================================================================" +
                "================================================================================================================");
    }

    public void testCaseFinished(TestCaseFinished e) {
        final Status status = e.getResult().getStatus();
        testSummary.add(FitnesseCucumberResultFactory.getFitnesseCucumberResult(status).getExecutionResult());
        undefinedSteps.clear();
    }

    private void print(String tag, String message, String properties) {
        printer.outputChunk("<" + tag + " " + properties + ">" + message + "</" + tag + ">");
    }

    private void print(String tag, String message) {
        print(tag, message, "");
    }
}
