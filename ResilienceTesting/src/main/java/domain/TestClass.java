package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestClass {
    private String name;
    private String path;
    private List<TestCase> testCases;


    public TestClass() {
    }

    public TestClass(String name, String path, List<TestCase> testCases) {
        this.name = name;
        this.path = path;
        this.testCases = testCases;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
