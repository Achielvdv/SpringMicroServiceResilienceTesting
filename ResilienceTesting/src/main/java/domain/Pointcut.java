package domain;

public class Pointcut {
    private String joinpoint;
    private String name;

    public Pointcut(String joinpoint, String name) {
        this.joinpoint = joinpoint;
        this.name = name;
    }

    public String getJoinpoint() {
        return joinpoint;
    }

    public void setJoinpoint(String joinpoint) {
        this.joinpoint = joinpoint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
