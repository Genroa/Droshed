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
    private String modelName;

    public Model(String modelName, ArrayList<Column> columns, ArrayList<Line> lines) {
        this.modelName = modelName;
        this.columns = columns;
        this.lines = lines;
    }

    public static Model createModelFromModelFile(String model, Context context) {
        ModelParser mp = new ModelParser();
        Model m =  mp.parseModel(model, context);
        DataParser dp = new DataParser(m);
        return m;
    }

    public void addLine(String id, String name) {
        lines.add(new Line(id, name));
    }

    public void addColumn(Column column) {
        columns.add(column);
    }

    public Column getColumn(int columnIndex) {
        return columns.get(columnIndex);
    }

    public int getColumnNumber() {
        return columns.size();
    }

    public String getModelName() {
        return modelName;
    }

    public int getLineNumber() {
        return lines.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Columns : \n");
        for (Column c: columns) {
            sb.append("getColumn  : ");
            sb.append(c.getInputType());
            sb.append("\n");
        }
        sb.append("\nLines").append(lines.size()).append(" : \n");
        for (Line l: lines) {
            sb.append("getLine  : ");
            sb.append(l.getName());
            sb.append("\n");
        }
        return sb.toString();
    }
}
