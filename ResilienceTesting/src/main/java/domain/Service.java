package domain;

import java.util.ArrayList;
import java.util.List;

public class Service {
    private String name;
    private String path;
    private List<MessageChannel> messageChannels;
    private List<TestClass> testClasses;
    private List<Listener> listeners;


    public Service() {
        messageChannels = new ArrayList<>();
    }

    public Service(String name, String path, List<TestClass> testClasses) {
        this.name = name;
        this.path = path;
        this.testClasses = testClasses;
        messageChannels = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<TestClass> getTestClasses() {
        return testClasses;
    }

    public void setTestClasses(List<TestClass> testClasses) {
        this.testClasses = testClasses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MessageChannel> getMessageChannels() {
        return messageChannels;
    }

    public void setMessageChannels(List<MessageChannel> messageChannels) {
        this.messageChannels = messageChannels;
    }

    public List<Listener> getListeners() {
        return listeners;
    }

    public void setListeners(List<Listener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public String toString() {
        return "Service{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", outputChannels=" + messageChannels.toString() +
                ", testClasses=" + testClasses +
                '}';
    }
}
