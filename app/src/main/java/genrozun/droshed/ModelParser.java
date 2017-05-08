package genrozun.droshed;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by axelheine on 04/05/2017.
 */

public class ModelParser {
    private final HashMap<String, Function<HashMap<String,String>, Column>> columnTypes;

    public ModelParser(HashMap<String, Function<HashMap<String,String>, Column>> columnTypes) {
        this.columnTypes = Objects.requireNonNull(columnTypes);
    }

    public void addType(String name, Function<HashMap<String,String>, Column> process) {
        columnTypes.put(name, process);
    }

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
    public List parse(XmlPullParser parser) throws XmlPullParserException, IOException {
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

                    if(!name.equals("model")) {
                        Log.e(ModelParser.class.getName(), attributes.toString());
                        //TODO getOrDefault
                        columnTypes.get(parser.getAttributeValue(null, "type")).apply(attributes);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("column") && column != null) {
                        columns.add(column);
                    }
            }
            eventType = parser.next();
        }

        return columns;

    }
}
