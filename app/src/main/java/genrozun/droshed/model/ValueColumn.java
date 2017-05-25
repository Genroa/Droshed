package genrozun.droshed.model;

import android.text.InputType;
import java.util.Map;


/**
 * Created by genro on 25/05/2017.
 */

abstract class ValueColumn<V> extends AbstractColumn<V> {
    private V minValue;
    private V maxValue;

    public ValueColumn(String id, String name, Map<String, String> parameters) {
        super(id, name);

        if(parameters.containsKey("min")) {
            minValue = stringToValue(parameters.get("min"));
        }

        if(parameters.containsKey("max")) {
            maxValue = stringToValue(parameters.get("max"));
        }
    }

    @Override
    public int getInputType() {
        return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL;
    }


    public V getMinValue() {
        return minValue;
    }

    public V getMaxValue() {
        return maxValue;
    }

    public void setMinValue(V minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(V maxValue) {
        this.maxValue = maxValue;
    }

    abstract V stringToValue(String representation);
}
