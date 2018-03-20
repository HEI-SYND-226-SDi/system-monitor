package Measurement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * The singleton class establish an HTTP connection to an InfluxDB server
 * and allows to push points to it.
 */

public class DatabaseConnector {

    //Singleton
    private static DatabaseConnector pInstance = null;

    // Local variables
    private int 				_portNbr = 8086;
    private String 				_server = "vlesdi.hevs.ch";
    private String 				_db = "<DB>";
    private String 				_username = "<USER>";
    private String 				_password = "<PASS>";
    private HttpURLConnection httpURLConnection;

    private DatabaseConnector() {
    }

    public static DatabaseConnector getInstance() {
        if (pInstance == null) {
            pInstance = new DatabaseConnector();
        }
        return pInstance;
    }

    /**
     * POST a query to the /write endpoint
     * @param query: the query to push
     * @return true if everything went OK
     */

    public boolean write(String query) {
        try {
            URL obj           = new URL("http", _server, _portNbr, "/write?db=" + _db);
            String userpass   = _username + ":" + _password;
            httpURLConnection = (HttpURLConnection) obj.openConnection();
            httpURLConnection.setRequestProperty ("Authorization", "Basic " + new sun.misc.BASE64Encoder().encode(userpass.getBytes()));
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "binary/octet-stream");
            httpURLConnection.setDoOutput(true);

            DEBUG_INFO("pushValue", "input: " + query);

            try (OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream())) {
                writer.write(query);
                writer.flush();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String decodedStr;
            while ((decodedStr = in.readLine()) != null) {
                DEBUG_INFO("pushValue", decodedStr);
            }
            in.close();
            return true;
        } catch (IOException error) {
            DEBUG_INFO("pushValue", "IOException: " + error.getMessage());
            System.out.println(error);
        } finally {
            // Make sure the connection is not null.
            if(httpURLConnection != null) httpURLConnection.disconnect();
        }
        return true; // ?
    }

    // DEBUG System.out
    private void DEBUG_INFO(String method, String msg) {
        Utility.DEBUG("[db::DataBaseConnector]", method, msg);
    }
}
