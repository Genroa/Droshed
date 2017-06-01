package genrozun.droshed.model;


import java.io.Serializable;

/**
 * Created by axelheine on 08/05/2017.
 */

public interface Column<T> {
    int getInputType();
    String buildCellContent(String lineID);
    T getValue(String lineID);
    T getDefaultValue();
    String getValueAsString(String lineID);
    void setValue(String lineID, T newValue);
    void setValueFromString(String lineID, String newValue);
    String getID();
    String getName();
    void setName(String newName);
}