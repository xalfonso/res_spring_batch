package eas.com.batch;

import lombok.Getter;

import java.util.Date;
import java.util.Objects;

@Getter
public class SimpleMeasureTime {

    private Date start;

    private Date end;

    public SimpleMeasureTime start() {
        this.start = new Date();
        return this;

    }

    public SimpleMeasureTime end() {
        this.end = new Date();
        return this;
    }

    public long getElapsedTime() {
        Objects.requireNonNull(start, "The start time must be set");
        Objects.requireNonNull(end, "The end time must set");
        return end.getTime() - start.getTime();
    }
}
