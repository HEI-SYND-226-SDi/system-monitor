package Measurement;

/**
 * The class models an InfluxDB field of type float.
 */

public class FloatField {

    public String key;
    public float value;

    FloatField(String key, float value) {
        this.key = key;
        this.value = value;
    }
}
