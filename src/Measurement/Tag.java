package Measurement;

/**
 * The class models an InfluxDB tag.
 */

public class Tag {

    public String key ;
    public String value ;

    Tag(String key, String value) {
        this.key = key ;
        this.value = value ;
    }
}
