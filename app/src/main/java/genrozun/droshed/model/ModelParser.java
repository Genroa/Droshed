package genrozun.droshed.model;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by axelheine on 04/05/2017.
 */

public class ModelParser {
    //private final HashMap<String, Function<HashMap<String,String>, Column>> columnTypes;

    /*public ModelParser(/*HashMap<String, Function<HashMap<String,String>, Column>> columnTypes) {
      //  this.columnTypes = Objects.requireNonNull(columnTypes);
    //}

   /* public void addType(String name, Function<HashMap<String,String>, Column> process) {
        columnTypes.put(name, process);
    }*/

    /**
     * Method used to parse XML Model file as defined by the server
     * @param parser
     *          Example of use :
     *              XmlPullParser parser = pullParserFactory.newPullParser();
                    InputStream in_s = getApplicationContext().getAssets().open("sample.xml");
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(in_s, null);
                    parse(parser);
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    /*public List parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Column> columns = null;
        int eventType = parser.getEventType();
        Column column = null;
        HashMap<String,String> attributes = new HashMap<>();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    columns = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    Log.e(ModelParser.class.getName(), name + " " + parser.getAttributeCount());
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        attributes.put(parser.getAttributeName(i), parser.getAttributeValue(i));
                    }
                    break;
                case XmlPullParser.TEXT:
                    attributes.put("name", parser.getText());
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    Log.e(ModelParser.class.getName(), name);
                    //if (name.equalsIgnoreCase("column") && column != null) {
                        Log.e(ModelParser.class.getName(), attributes.toString());
                        //TODO getOrDefault
                        column = columnTypes.get(attributes.get("type")).apply(attributes);
                        columns.add(column);
                        attributes.clear();
                    //}
            }
            eventType = parser.next();
        }

        return columns;

    }*/

    public ArrayList<Column> parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Column> columns = null;
        int eventType = parser.getEventType();
        Column column = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch(eventType) {
                case XmlPullParser.START_DOCUMENT:
                    columns = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    Log.e(ModelParser.class.getName(), name + " " + parser.getAttributeCount());
                    if(name.equalsIgnoreCase("column")) {
                        HashMap<String, String> parameters = new HashMap<>();
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attributeName = parser.getAttributeName(i);
                            if(!attributeName.equals("id") && !attributeName.equals("name") && !attributeName.equals("type")) {
                                parameters.put(attributeName, parser.getAttributeValue(i));
                            }
                        }
                        String type = parser.getAttributeValue(null, "type");

                        switch(type) {
                            case "text":
                                column = new TextColumn(parser.getAttributeValue(null, "id"), parser.getAttributeValue(null, "name"), parameters);
                                break;
                            case "decimal":
                                column = new DecimalColumn(parser.getAttributeValue(null, "id"), parser.getAttributeValue(null, "name"), parameters);
                                break;
                            case "integer":
                                column = new IntegerColumn(parser.getAttributeValue(null, "id"), parser.getAttributeValue(null, "name"), parameters);
                                break;
                        }
                    }
                    break;
                case XmlPullParser.TEXT:
                    break;
                case XmlPullParser.END_TAG:
                    if(parser.getName().equalsIgnoreCase("column"))
                        columns.add(column);
                    break;
                default:
                    Log.e(ModelParser.class.getName(), "default");
            }
            eventType = parser.next();
        }
        return columns;
    }
}
