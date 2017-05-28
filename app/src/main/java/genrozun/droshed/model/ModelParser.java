package genrozun.droshed.model;

import android.content.Context;
import android.graphics.Path;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import genrozun.droshed.sync.DataManager;

/**
 * Created by axelheine on 04/05/2017.
 */

public class ModelParser {
    ArrayList<Column> columns;
    ArrayList<Line> lines;

    public ModelParser() {
        this.columns = new ArrayList<>();
        this.lines = new ArrayList<>();
    }

    public Model parseModel(String modelName, Context context) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            InputStream in_s = new FileInputStream(DataManager.getModel(context, modelName)); //TODO: load model file from path
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            parse(parser);
        } catch (IOException e) {
            //TODO gestion des exceptions
            Log.e(ModelParser.class.getName(), e.toString());
        } catch (XmlPullParserException e) {
            Log.e(ModelParser.class.getName(), e.toString());
        }
        Log.e(ModelParser.class.getName(), "Lines : " + lines.size());
        return new Model(modelName, columns, lines);
    }

    /**
     * Method used to parse XML Model file as defined by the server
     * @param parser
     * @return Created Model
     * @throws XmlPullParserException
     * @throws IOException
     */
    public void parse(XmlPullParser parser) throws XmlPullParserException, IOException {
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
                    if(lastTagName.equalsIgnoreCase("column")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attributeName = parser.getAttributeName(i);
                            if(!attributeName.equals("id") && !attributeName.equals("type")) {
                                parameters.put(attributeName, parser.getAttributeValue(i));
                            }
                        }
                        columnType = parser.getAttributeValue(null, "type");
                        columnId = parser.getAttributeValue(null, "id");
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
                        //Log.e(ModelParser.class.getName(), "new column : ");
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
                        columns.add(column);
                        parameters.clear();
                    }
                    if(parser.getName().equalsIgnoreCase("line")) {
                        //Log.e(ModelParser.class.getName(), "new Line : " + lineId + " " + text);
                        lines.add(new Line(lineId, text));
                    }
                    break;
                default:
                    Log.e(ModelParser.class.getName(), "default");
            }
            eventType = parser.next();
        }
    }
}
