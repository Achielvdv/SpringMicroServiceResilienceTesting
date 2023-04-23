package domain;

public class TestResult {
    private String time;
    private Boolean success;

    public TestResult(String time, Boolean success) {
        this.time = time;
        this.success = success;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "time='" + time + '\'' +
                ", success=" + success +
                '}';
    }

    public String showOutcome() {
        if (this.success) {
            return "SUCCESS";
        } else {
            return "FAILURE";
        }
    }
}
