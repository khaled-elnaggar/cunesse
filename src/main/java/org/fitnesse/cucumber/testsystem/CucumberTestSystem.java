package org.fitnesse.cucumber.testsystem;

import fitnesse.testsystems.CompositeTestSystemListener;
import fitnesse.testsystems.ExecutionLogListener;
import fitnesse.testsystems.ExecutionResult;
import fitnesse.testsystems.TestPage;
import fitnesse.testsystems.TestSummary;
import fitnesse.testsystems.TestSystem;
import fitnesse.testsystems.TestSystemListener;
import fitnesse.testsystems.UnableToStartException;
import io.cucumber.core.exception.CucumberException;
import io.cucumber.core.feature.FeatureParser;
import io.cucumber.core.feature.GluePath;
import io.cucumber.core.gherkin.Feature;
import io.cucumber.core.gherkin.FeatureParserException;
import io.cucumber.core.options.RuntimeOptions;
import io.cucumber.core.options.RuntimeOptionsBuilder;
import io.cucumber.core.runtime.FeatureSupplier;
import io.cucumber.core.runtime.Runtime;
import io.cucumber.core.snippets.SnippetType;
import org.fitnesse.cucumber.FitnesseFeatureResource;
import org.fitnesse.cucumber.FitnessePrinter;
import org.fitnesse.cucumber.testresultpage.CucumberResultFormatter;
import util.FileUtil;

import java.io.Closeable;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CucumberTestSystem implements TestSystem {
    public static final String TEST_SYSTEM_NAME = "cucumber";
    private final String name;
    private final ExecutionLogListener executionLogListener;
    private final CompositeTestSystemListener testSystemListener;
    private final ClassLoader classLoader;
    private boolean started = false;

    public CucumberTestSystem(String name, final ExecutionLogListener executionLogListener) {
        super();
        this.name = name;
        this.executionLogListener = executionLogListener;
        this.testSystemListener = new CompositeTestSystemListener();
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void start() throws UnableToStartException {
        started = true;
        testSystemListener.testSystemStarted(this);
    }

    @Override
    public void bye() {
        kill();
    }

    @Override
    public void kill() {
        testSystemListener.testSystemStopped(this, null);

        if (classLoader instanceof Closeable) {
            FileUtil.close((Closeable) classLoader);
        }
    }

    @Override
    public void runTests(TestPage testPage) {
        final TestSummary testSummary = new TestSummary();

        String gluePath = testPage.getVariable("cucumber.glue");
        URI glueURI = GluePath.parse(gluePath);
        final FitnessePrinter printer = new FitnessePrinter(testPage, testSystemListener, executionLogListener);
        printer.printHorizontalRule();

        testSystemListener.testStarted(testPage);

        FeatureSupplier featureSupp = null;

        try {
            featureSupp = createFeatureSupp(testPage);
            System.out.println("Parsed feature files successfully");
        } catch (FeatureParserException e) {
            printer.outputChunk("<span class='error'>Test execution failed: " + e.getMessage() + "</span>");
            printer.executionLogErr("Test execution failed: (FeatureParserException) " + e.getMessage());
            printer.executionLogErr("Caused by: " + e.getCause());
            printer.executionLogErr("=============================================" +
                    "=======================================================" +
                    "=========================================================" +
                    "============================================");

            testSummary.add(ExecutionResult.ERROR);
            log(testPage, 1);
            testSystemListener.testComplete(testPage, testSummary);
            return;
        }  catch (Exception e) {

            printer.outputChunk("<span class='error'>Test execution failed (General Exception): " + e.getMessage() + "</span>");
            printer.executionLogErr("Test execution failed: (General Exception)" + e.getMessage());
            printer.executionLogErr("Stacktrace: [");
            Arrays.stream(e.getStackTrace())
                    .forEach(element -> printer.executionLogErr(String.valueOf(element)));
            printer.executionLogErr("]");

            testSummary.add(ExecutionResult.ERROR);
            log(testPage, 1);
            testSystemListener.testComplete(testPage, testSummary);
            return;
        }

        ClassLoader originalLoader = Thread.currentThread().getContextClassLoader();

        RuntimeOptions runtimeOptions = new RuntimeOptionsBuilder().setSnippetType(SnippetType.CAMELCASE).addGlue(glueURI).build();

        final CucumberResultFormatter cucumberResultFormatter = new CucumberResultFormatter(testSummary, printer);
        final Runtime runtime = Runtime.builder()
                .withRuntimeOptions(runtimeOptions)
                .withFeatureSupplier(featureSupp)
                .withAdditionalPlugins(cucumberResultFormatter)
                .withClassLoader(() -> originalLoader)
                .build();


        try {
            runtime.run();
        } catch (CucumberException e) {

            printer.outputChunk("<span class='error'>Test execution failed (run): " + e.getMessage() + "</span>");
            printer.executionLogErr("Test execution failed: " + e.getMessage());
            printer.executionLogErr("Exception: " + e);
            printer.executionLogErr("Caused by: " + e.getCause());
            printer.executionLogErr("Stacktrace: [");
            Arrays.stream(e.getStackTrace())
                    .forEach(element -> printer.executionLogErr(String.valueOf(element)));
            printer.executionLogErr("]");

            testSummary.add(ExecutionResult.ERROR);
            log(testPage, 1);
            testSystemListener.testComplete(testPage, testSummary);
            return;
        }

        //TODO: log instead of printing
        final int exitStatus = runtime.exitStatus();
        log(testPage, exitStatus);
        testSystemListener.testComplete(testPage, testSummary);
    }

    private void log(TestPage testPage, int exitStatus) {
        String status = (exitStatus == 0) ? "SUCCESS" : "FAIL";
        System.out.println("Testing page: " + testPage.getFullPath() + ". [" + status + " - " + exitStatus + "]");
    }

    private FeatureSupplier createFeatureSupp(TestPage testPage) {
        Feature feature = new FeatureParser(UUID::randomUUID)
                .parseResource(new FitnesseFeatureResource(testPage)).orElse(null);

        return new FeatureSupplier() {
            @Override
            public List<Feature> get() {
                return Collections.singletonList(feature);
            }
        };
    }

    @Override
    public boolean isSuccessfullyStarted() {
        return started;
    }

    @Override
    public void addTestSystemListener(TestSystemListener listener) {
        testSystemListener.addTestSystemListener(listener);
    }
}
