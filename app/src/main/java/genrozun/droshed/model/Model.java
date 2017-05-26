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

    public Model(ArrayList<Column> columns, ArrayList<Line> lines) {
        this.columns = columns;
        this.lines = lines;
    }

    public Model createModelFromModelFile(String model, Context context) {
        ModelParser mp = new ModelParser();
        return mp.parseModel(model, context);
    }

    private void parseDataFile(XmlPullParser parser) throws XmlPullParserException {
        int eventType = parser.getEventType();
        String currentParent = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch(eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    String tagName = parser.getName();
                    switch (tagName) {
                        case "line":
                            currentParent = "line";

                            break;
                    }
                    break;
            }
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
        return lines.size();
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
