package genrozun.droshed.model;

import android.graphics.Path;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by axelheine on 04/05/2017.
 */

public class ModelParser {
    /**
     * Method used to parse XML Model file as defined by the server
     * @param parser
     * @return Created Model
     * @throws XmlPullParserException
     * @throws IOException
     */
    public void parseModel(XmlPullParser parser, Model model) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        String lastTagName = "";
        String columnType = "";
        String columnId = "";
        HashMap<String, String> parameters = new HashMap<>();
        Column column = null;
        String lineId = "";
        String text = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch(eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    lastTagName = parser.getName();
                    Log.i(ModelParser.class.getName(), lastTagName + " " + parser.getAttributeCount());
                    if(lastTagName.equalsIgnoreCase("column")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attributeName = parser.getAttributeName(i);
                            if(!attributeName.equals("id") && !attributeName.equals("type")) {
                                parameters.put(attributeName, parser.getAttributeValue(i));
                            }
                        }
                        columnType = parser.getAttributeValue(null, "type");
                        columnId = parser.getAttributeValue(null, "id");
                        Log.i(Model.class.getName(), "Text in switch : " + parser.getText());

                    }

                    if(lastTagName.equalsIgnoreCase("line")) {
                        lineId = parser.getAttributeValue(null, "id");
                    }
                    break;
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if(parser.getName().equalsIgnoreCase("column")) {
                        switch(columnType) {
                            case "text":
                                column = new TextColumn(columnId, text, parameters);
                                break;
                            case "decimal":
                                column = new DecimalColumn(columnId, text, parameters);
                                break;
                            case "integer":
                                column = new IntegerColumn(columnId, text, parameters);
                                break;
                        }
                        model.addColumn(column);
                        parameters.clear();
                    }
                    if(parser.getName().equalsIgnoreCase("line"))
                        model.addLine(lineId, text);
                    break;
                default:
                    Log.e(ModelParser.class.getName(), "default");
            }
            eventType = parser.next();
        }
    }
}
