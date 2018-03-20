package Measurement;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * This class contains some useful Java methods to manipulate Modbus data.
 *
 * @author Michael Clausen
 * @author Cedric Crettaz
 */
public class Utility {

    /**
     * Calculates and returns the CRC using the data passed in parameters.
     *
     * @param data a byte array containing the data to send
     * @param offset the offset
     * @param len the data length
     * @return byte[] the CRC
     */
        /**
     * Utility method to convert a byte array in a string made up of hex (0,.. 9, a,..f)
     */
    public static String getHexString(byte[] b) throws Exception {
        return getHexString(b, 0, b.length);
    }

    public static String getHexString(byte[] b, int offset, int length) {
        String result = "";
        for (int i = offset ; i < offset+length ; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1) ;
        }
        return result;
    }

    public static String md5sum(String msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            return String.format("%032x", new BigInteger(1, md.digest(msg.getBytes("UTF-8"))));
        } catch (UnsupportedEncodingException e) {
            System.out.println("[MinecraftController] > UnsupportedEncodingException !!!");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("[MinecraftController] > NoSuchAlgorithmException !!!");
        }
        return null;
    }


    /**
     * To wait some times ...
     */
    public static void waitSomeTime(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            System.out.println("[Utility] waitSomeTime() > Exception : " + e.getMessage());
        }
    }

    // DEBUG System.out
    public static void DEBUG(String className, String method, String msg) {
        int millis = Calendar.getInstance().get(Calendar.MILLISECOND);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(Calendar.getInstance().getTime()) + "." + String.format("%3d", millis) +
                " " + className + " " + method + " > " + msg);
    }
}
