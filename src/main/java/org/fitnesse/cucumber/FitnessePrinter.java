package org.fitnesse.cucumber;

import fitnesse.testsystems.CompositeTestSystemListener;
import fitnesse.testsystems.ExecutionLogListener;
import fitnesse.testsystems.TestPage;

public class FitnessePrinter {
    TestPage testPage;
    CompositeTestSystemListener testSystemListener;
    private final ExecutionLogListener executionLogListener;

    public FitnessePrinter(TestPage testPage, CompositeTestSystemListener testSystemListener, ExecutionLogListener executionLogListener) {
        this.testPage = testPage;
        this.testSystemListener = testSystemListener;
        this.executionLogListener = executionLogListener;
    }

    public void outputChunk(String text) {
        testSystemListener.testOutputChunk(testPage, text);
    }

    public void executionLogOut(String text) {
        executionLogListener.stdOut(text);
    }

    public void executionLogErr(String text) {
        executionLogListener.stdErr(text);
    }

    public void printBreakLine() {
        testSystemListener.testOutputChunk(testPage, "<br/>");
    }

    public void printHorizontalRule(){
        testSystemListener.testOutputChunk(testPage, "<hr style=\"height:3px;border-width:0;color:gray;background-color:gray\">");
    }
}
