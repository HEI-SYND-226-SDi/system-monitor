package influx;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public final class Connection {
    private final URL url;
    private final String token;

    public Connection(String url, String organisation, String token) throws MalformedURLException {
        this.url = new URL(url + "/api/v2/write?bucket=" + organisation + "&org=" + organisation);
        this.token = token;
    }

    public void write(Writeable writeable) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Token " + token);
        connection.setRequestProperty("Content-Type", "binary/octet-stream");
        connection.setDoOutput(true);

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(writeable.toWriteQuery());
        writer.flush();

        int status = connection.getResponseCode();
        if (status != 204) {
            throw new IOException("Request failed with status " + status + ": " + connection.getResponseMessage());
        }

        connection.disconnect();
    }
}
