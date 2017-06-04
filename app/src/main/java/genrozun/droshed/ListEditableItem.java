package genrozun.droshed;

import android.text.InputType;
import android.widget.EditText;

import genrozun.droshed.model.Model;

/**
 * Created by axelheine on 28/05/2017.
 */

public class ListEditableItem<T> {
    private String label;
    private String targetID;
    private String colID;
    private String lineID;
    private Model model;

    public ListEditableItem(Model model, String label, String col, String line, String targetID) {
        this.model = model;
        this.label = label;
        this.targetID = targetID;
        this.colID = col;
        this.lineID = line;
    }

    public T getValue() {
        return (T) model.getColumn(colID).getValue(lineID);
    }

    public void setValue(String newValue) {
        model.getColumn(colID).setValueFromString(lineID, newValue);
    }

    public int getType() {
        return model.getColumn(colID).getInputType();
    }

    public String getLabel() {
        return label;
    }

    public String getTargetID() { return targetID; }

    public String getColumnID() {
        return colID;
    }

    public String getLineID() {
        return lineID;
    }
}
