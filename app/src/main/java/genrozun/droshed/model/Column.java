package genrozun.droshed.model;


/**
 * Created by axelheine on 08/05/2017.
 */

public interface Column<T> {
    int getInputType();
    String buildCellContent(String lineID);
    T getValue(String lineID);
    String getValueAsString(String lineID);
    void setValue(String lineID, T newValue);
    void setValueFromString(String lineID, String newValue);
    String getID();
    String getName();
    void setName(String newName);
}