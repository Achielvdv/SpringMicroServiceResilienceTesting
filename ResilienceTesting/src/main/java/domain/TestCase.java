package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCase {
    private String name;
    private String execTimeInSeconds;
    private Boolean success;

    public TestCase() {
    }

    public TestCase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecTimeInSeconds() {
        return execTimeInSeconds;
    }

    public void setExecTimeInSeconds(String execTimeInSeconds) {
        this.execTimeInSeconds = execTimeInSeconds;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
