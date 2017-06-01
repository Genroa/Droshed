package genrozun.droshed.model;

import android.text.InputType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by genro on 25/05/2017.
 */

public abstract class AbstractColumn<T> implements Column<T>, Serializable {
    private final Map<String, T> values;
    private final String id;
    private String name;

    public AbstractColumn(String id, String name) {
        this.id = id;
        this.name = name;
        values = new HashMap<String, T>();
    }

    Map<String, T> getValues() {
        return values;
    }

    @Override
    public int getInputType() {
        return InputType.TYPE_CLASS_TEXT;
    }

    @Override
    public String buildCellContent(String lineID) {
        T value = getValue(lineID);
        return value != null ? value.toString() : "";
    }

    @Override
    public T getValue(String lineID) {
        T value = values.get(lineID);
        return value != null ? value : getDefaultValue();
    }


    @Override
    public String getValueAsString(String lineID) {
        T value = getValue(lineID);
        if(value == null)
            return null;

        return value.toString();
    }

    @Override
    public void setValue(String lineID, T newValue) {
        values.put(lineID, newValue);
    }

    @Override
    public void setValueFromString(String lineID, String newValue) {
        setValue(lineID, stringToValue(newValue));
    }

    T stringToValue(String representation) {
        return (T) representation;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String newName) {
        name = newName;
    }

    @Override
    public String getID() {
        return id;
    }
}
