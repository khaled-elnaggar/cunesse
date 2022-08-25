package org.fitnesse.cucumber;

import fitnesse.testrunner.WikiPageIdentity;
import fitnesse.wiki.*;
import fitnesse.wiki.fs.WikiPageProperties;
import org.apache.commons.lang3.NotImplementedException;
import org.fitnesse.cucumber.testsystem.CucumberTestSystem;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;

public class CucumberFeaturePage implements WikiPage {
    private final File path;
    private final String name;
    private final WikiPage parent;
    private String content;

    public CucumberFeaturePage(File path, String name, WikiPage parent) {
        this.name = name;
        this.parent = parent;
        this.path = path;
    }

    @Override
    public WikiPage getParent() {
        return parent == null ? this : parent;
    }

    @Override
    public boolean isRoot() {
        return parent == null || parent == this;
    }

    @Override
    public WikiPage addChildPage(String name) {
        throw new NotImplementedException("Can not add child pages to Cucumber feature pages");
    }

    @Override
    public boolean hasChildPage(String name) {
        return false;
    }

    @Override
    public WikiPage getChildPage(String name) {
        return null;
    }

    @Override
    public void removeChildPage(String name) {
        // no-op
    }

    @Override
    public void remove() {
        // no-op
    }

    @Override
    public List<WikiPage> getChildren() {
        return emptyList();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PageData getData() {
        return new PageData(readContent(), wikiPageProperties());
    }

    private WikiPageProperties wikiPageProperties() {
        WikiPageProperties properties = new WikiPageProperties();
        properties.set(PageType.TEST.toString());
        properties.set(WikiPageProperty.EDIT);
        return properties;
    }

    @Override
    public Collection<VersionInfo> getVersions() {
        return emptyList();
    }

    @Override
    public WikiPage getVersion(String versionName) {
        return this;
    }

    @Override
    public String getHtml() {
        // TODO: this is the presentation for viewing the feature files ????, not the results -
        //  use gherkin-java (Parser) or io.cucumber.core.gherkin (FeatureParser)
//        final StringBuilder buffer = new StringBuilder();
//        try {
//
//            new gherkin.parser.Parser(new FitNessePageFormatter(new Printer() {
//                @Override
//                public void write(final String text) {
//                    buffer.append(text);
//                }
//            })).parse(readContent(), "", 0);
//        } catch (ParseError e) {
//            buffer.append("<span class=\"error\">Parse error in Cucumber page: " + e.getMessage() + "</span><br/><br/>");
//            buffer.append(readContent().replace("\n", "<br/>"));
//        }
//        return buffer.toString();
//        return readContent();
        return "ew3aaaaaaaaaaaaaaaaaaa";
    }

    private String readContent() {
        if (content == null) {
            try {
                content = FileUtil.getFileContent(path);
            } catch (IOException e) {
                content = String.format("<p class='error'>Unable to read feature file %s: %s</p>", path, e.getMessage());
            }
        }
        return content;
    }

    @Override
    public VersionInfo commit(PageData data) {
        return null;
    }

    @Override
    public PageCrawler getPageCrawler() {
        return new PageCrawler(this);
    }

    @Override
    public String getVariable(String name) {
        if (WikiPageIdentity.TEST_SYSTEM.equals(name)) {
            return CucumberTestSystem.TEST_SYSTEM_NAME;
        }
        return isRoot() ? null : parent.getVariable(name);
    }

    public File getFileSystemPath() {
        return path;
    }

    @Override
    public int compareTo(final WikiPage other) {
        try {
            WikiPagePath path1 = getPageCrawler().getFullPath();
            WikiPagePath path2 = other.getPageCrawler().getFullPath();
            return path1.compareTo(path2);
        } catch (Exception e) {
            return 0;
        }
    }
}
