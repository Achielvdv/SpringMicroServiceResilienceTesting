package domain;

public class MessageChannel {
    private String type;
    private String joinpoint;
    private String name;

    public MessageChannel() {
    }

    public MessageChannel(String type, String joinpoint, String name) {
        this.type = type;
        this.joinpoint = joinpoint;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    @Override
    public String toString() {
        return "MessageChannel{" +
                "type='" + type + '\'' +
                ", joinpoint='" + joinpoint + '\'' +
                '}';
    }
}
