package org.fitnesse.cucumber;

import fitnesse.testsystems.TestPage;
import io.cucumber.core.feature.FeatureIdentifier;
import io.cucumber.core.resource.Resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FitnesseFeatureResource implements Resource {
    private final TestPage testPage;

    public FitnesseFeatureResource(TestPage testPage) {
        this.testPage = testPage;
    }

    @Override
    public URI getUri() {
        return FeatureIdentifier.parse(testPage.getFullPath() + ".feature");
    }

    @Override
    public InputStream getInputStream() {
        final String featureString = testPage.getContent();
        return new ByteArrayInputStream(featureString.getBytes(UTF_8));
    }
}
