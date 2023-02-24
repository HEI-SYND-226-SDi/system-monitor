import influx.Connection;
import monitor.SystemProperties;

import java.net.MalformedURLException;

public class Main {
    private static final String URL = "https://influx.sdi.hevs.ch";

    public static void main(String[] args) {
        if (args.length != 2) {
            usage();
        }

        try {
            Connection connection = new Connection(URL, args[0], args[1]);
            SystemProperties systemProperties = new SystemProperties(connection);
            systemProperties.run();
        } catch (MalformedURLException exception) {
            System.err.println("Invalid URL: " + exception.getMessage());
            System.exit(1);
        }
    }

    private static void usage() {
        System.out.println("java -jar monitor.jar <organisation> <token>");
        System.exit(1);
    }
}
