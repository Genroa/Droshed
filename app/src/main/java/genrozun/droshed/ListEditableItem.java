package genrozun.droshed;

import android.text.InputType;
import android.widget.EditText;

/**
 * Created by axelheine on 28/05/2017.
 */

public class ListEditableItem<T> {
    private T value;
    private int type;
    private String label;

    public ListEditableItem(T value, int type, String label) {
        this.value = value;
        this.type = type;
        this.label = label;
    }

    public T getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }
}
