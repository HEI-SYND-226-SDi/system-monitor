package influx;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public final class Point implements Writeable {
    private final String measurement;
    private final Map<String,String> tags = new HashMap<>();
    private final Map<String,Float> fields = new HashMap<>();

    public Point(String measurement) {
        this.measurement = measurement;
    }

    public Point tag(String key, String value) {
        tags.put(key, value);
        return this;
    }

    public Point field(String key, float value) {
        fields.put(key, value);
        return this;
    }

    public Point field(String key, double value) {
        return field(key, (float)value);
    }

    public void write(Connection connection) throws IOException {
        connection.write(this);
    }

    @Override
    public String toWriteQuery() {
        StringBuilder builder = new StringBuilder();
        builder.append(measurement);
        builder.append(",");

        tags.forEach((key, value) -> {
            builder.append(key);
            builder.append("=");
            builder.append(value);
            builder.append(",");
        });
        builder.deleteCharAt(builder.length() - 1);
        builder.append(" ");

        fields.forEach((key, value) -> {
            builder.append(key);
            builder.append("=");
            builder.append(value);
            builder.append(",");
        });
        builder.deleteCharAt(builder.length() - 1);
        builder.append(" ");

        builder.append(Instant.now().toEpochMilli() * 1000000);

        return builder.toString();
    }
}
