package genrozun.droshed.model;

import java.util.Objects;

/**
 * Created by axelheine on 08/05/2017.
 */

public interface Column<T> {
    int getInputType();
    String buildCellContent(int line);
    T getValue(int line);
    void setValue(int line, T newValue);
    void setValueFromString(int line, String newValue);
}