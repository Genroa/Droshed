package genrozun.droshed.model;

import android.text.InputType;

import java.util.ArrayList;

/**
 * Created by genro on 25/05/2017.
 */

public abstract class AbstractColumn<T> implements Column<T> {
    private final ArrayList<T> values;

    public AbstractColumn(String id, String name) {
        values = new ArrayList<T>();
    }

    ArrayList<T> getValues() {
        return values;
    }

    @Override
    public int getInputType() {
        return InputType.TYPE_CLASS_TEXT;
    }

    @Override
    public String buildCellContent(int line) {
        if(line < 0 || line > values.size()) {
            return "";
        }
        return values.get(line).toString();
    }

    @Override
    public T getValue(int line) {
        if(line < 0 || line > values.size()) {
            throw new IllegalArgumentException();
        }
        return values.get(line);
    }

    @Override
    public void setValue(int line, T newValue) {
        values.add(line, newValue);
    }

    @Override
    public void setValueFromString(int line, String newValue) {
        setValue(line, stringToValue(newValue));
    }

    T stringToValue(String representation) {
        return (T) representation;
    }
}
