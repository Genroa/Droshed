package genrozun.droshed.model;

import android.text.InputType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by genro on 25/05/2017.
 */

public abstract class AbstractColumn<T> implements Column<T> {
    private final Map<String, T> values;

    public AbstractColumn(String id, String name) {
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
        return values.get(lineID);
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
}
