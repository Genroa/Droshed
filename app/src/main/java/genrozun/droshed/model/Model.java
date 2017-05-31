package genrozun.droshed.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import genrozun.droshed.activities.SheetEditActivity;
import genrozun.droshed.compat.CollectionUtils;
import genrozun.droshed.sync.DataManager;
import genrozun.droshed.sync.SheetUpdateService;

/**
 * Created by axelheine on 08/05/2017.
 */

public class Model implements Serializable {
    private final ArrayList<Column> columns;
    private final HashMap<String, Column> columnsById;

    private final ArrayList<Line> lines;
    private String modelName;

    public Model(String modelName, ArrayList<Column> columns, ArrayList<Line> lines) {
        this.modelName = modelName;
        this.columns = columns;
        this.columnsById = CollectionUtils.toMap(columns, Column::getID);
        this.lines = lines;
    }

    public static Model createModelFromModelFile(String model, Context context) {
        ModelParser mp = new ModelParser();
        Model m =  mp.parseModel(model, context);

        m.populateModelWithData(context, model);

        return m;
    }

    public void populateModelWithData(Context context, String model) {
        int version = DataManager.getLastVersionNumberForModel(context, model);
        Log.d(Model.class.getName(), "CurrentVersion : " + version);
        if(version > 0) {
            DataParser dp = new DataParser(this);
            dp.parseDataFromFile(context);
        }
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

    public Column getColumn(String columnID) {
        return columnsById.get(columnID);
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

    public List<Column> getColumns() {
        return columns;
    }

    public List<Line> getLines() {
        return lines;
    }

    public Line getLine(int index) { return lines.get(index); }

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
