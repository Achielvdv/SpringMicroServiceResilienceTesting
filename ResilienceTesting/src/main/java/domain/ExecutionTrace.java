package domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionTrace {
    private List<Channel> channels;
    private List<Handler> handlers;

    public ExecutionTrace(List<Channel> channels, List<Handler> handlers) {
        this.channels = channels;
        this.handlers = handlers;
    }

    public ExecutionTrace() {
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<Handler> getHandlers() {
        return handlers;
    }

    public void setHandlers(List<Handler> handlers) {
        this.handlers = handlers;
    }
}

