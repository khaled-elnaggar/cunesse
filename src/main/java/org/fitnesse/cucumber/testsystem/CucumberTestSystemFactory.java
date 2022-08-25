package org.fitnesse.cucumber.testsystem;

import fitnesse.testsystems.Descriptor;
import fitnesse.testsystems.TestSystem;
import fitnesse.testsystems.TestSystemFactory;

public class CucumberTestSystemFactory implements TestSystemFactory {

    @Override
    public TestSystem create(Descriptor descriptor) {
        return new CucumberTestSystem(descriptor.getTestSystem(), descriptor.getExecutionLogListener());
    }
}
