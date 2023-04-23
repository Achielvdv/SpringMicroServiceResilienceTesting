package domain;

public class Listener {
    private String name;
    private String joinpoint;

    public Listener(String name, String joinpoint) {
        this.name = name;
        this.joinpoint = joinpoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJoinpoint() {
        return joinpoint;
    }

    public void setJoinpoint(String joinpoint) {
        this.joinpoint = joinpoint;
    }
}
