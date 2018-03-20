package Measurement;

import java.time.Instant;
import java.util.LinkedList;


public class Point {

    private LinkedList<FloatField> fields = new LinkedList<>();
    private LinkedList<Tag> tags = new LinkedList<>();

    public String query ;

    public Point(String measurement) {
        this.query = measurement;
    }

    public void addTag(String key, String value) {
        tags.add(new Tag(key, value));
    }

    public void addFloatField(String key, float value) {
        fields.add(new FloatField(key, value));
    }

    public boolean request() {

        query +=  ",";

        for (int i = 0; i < tags.size(); i++) {
            query += tags.get(i).key + '=' + tags.get(i).value ;
            if (i != tags.size() -1 ) query += ',' ;
        }
        query += ' ';

        for (int i = 0; i < fields.size(); i++) {
            query += fields.get(i).key + '=' + fields.get(i).value ;
            if (i != fields.size() -1 ) query += ',' ;
        }
        query += ' ';
        query += (long) (Instant.now().toEpochMilli()*1E6);

        return (DatabaseConnector.getInstance().write(query)) ;
    }

    public static void main(String[] args) {
        Point wo = new Point("test1") ;
        wo.addTag("Location", "a") ;
        wo.addTag("Scientist", "x" ) ;
        wo.addFloatField("Value", 5f) ;
        wo.request() ;

    }
}
