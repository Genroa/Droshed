package genrozun.droshed.model;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import genrozun.droshed.R;
import genrozun.droshed.sync.DataManager;

/**
 * Created by axelheine on 08/05/2017.
 */

public class Model {
    private ArrayList<Column> columns;
    private ArrayList<Line> lines;

    private Model() {
        this.columns = new ArrayList<>();
        this.lines = new ArrayList<>();
    }

    public static Model create(String modelName, Context c) {
        XmlPullParser parser = Xml.newPullParser();
        Model model = new Model();
        try {
            InputStream in_s = new FileInputStream(DataManager.getModel(c, modelName)); //TODO: load model file from path
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            model.parse(parser);
        } catch (IOException e) {
            //TODO gestion des exceptions
            Log.e(ModelParser.class.getName(), e.toString());
        } catch (XmlPullParserException e) {
            Log.e(ModelParser.class.getName(), e.toString());
        }

        return model;
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
     * @return Created Model
     * @throws XmlPullParserException
     * @throws IOException
     */
    public void parse(XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        String lastTagName = "";
        String columnType = "";
        HashMap<String, String> parameters = new HashMap<>();
        Column column = null;
        String lineId = null;
        String text = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name;
            switch(eventType) {
                case XmlPullParser.START_DOCUMENT:
                    columns = new ArrayList();
                    break;
                case XmlPullParser.START_TAG:
                    lastTagName = parser.getName();
                    Log.i(ModelParser.class.getName(), lastTagName + " " + parser.getAttributeCount());
                    if(lastTagName.equalsIgnoreCase("column")) {
                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            String attributeName = parser.getAttributeName(i);
                            if(!attributeName.equals("id") && !attributeName.equals("name") && !attributeName.equals("type")) {
                                parameters.put(attributeName, parser.getAttributeValue(i));
                            }
                        }
                        columnType = parser.getAttributeValue(null, "type");
                        Log.i(Model.class.getName(), "Text in switch : " + parser.getText());

                    }

                    if(lastTagName.equalsIgnoreCase("line")) {
                        lineId = parser.getAttributeValue(null, "id");
                        //this.addLine(, parser.getText());
                    }
                    break;
                case XmlPullParser.TEXT:
                    //Log.i(Model.class.getName(), "Text : " + parser.getText());
                    text = parser.getText();
                    break;
                case XmlPullParser.END_TAG:
                    if(parser.getName().equalsIgnoreCase("column")) {
                        switch(columnType) {
                            case "text":
                                column = new TextColumn(parser.getAttributeValue(null, "id"), text, parameters);
                                break;
                            case "decimal":
                                column = new DecimalColumn(parser.getAttributeValue(null, "id"), text, parameters);
                                break;
                            case "integer":
                                column = new IntegerColumn(parser.getAttributeValue(null, "id"), text, parameters);
                                break;
                        }
                        addColumn(column);
                        parameters.clear();
                    }
                    if(parser.getName().equalsIgnoreCase("line"))
                        addLine(lineId, text);
                    break;
                default:
                    Log.e(ModelParser.class.getName(), "default");
            }
            eventType = parser.next();
        }
    }

    public void addLine(String id, String name) {
        lines.add(new Line(id, name));
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public int getColumnNumber() {
        return columns.size();
    }

    public int getLineNumber() {
        return 15;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Columns : \n");
        for (Column c: columns) {
            sb.append(c.getInputType());
        }
        sb.append("\nLines : \n");
        for (Line l: lines) {
            sb.append(l.getName());
        }
        return sb.toString();
    }
}
