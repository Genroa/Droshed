package genrozun.droshed.model;

import android.text.InputType;
import java.util.Map;


/**
 * Created by genro on 25/05/2017.
 */

abstract class ValueColumn<T> extends AbstractColumn<T> {
    private T minValue = getAbsoluteMin();
    private T maxValue = getAbsoluteMax();

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


    public T getMinValue() {
        return minValue;
    }

    public T getMaxValue() {
        return maxValue;
    }

    public void setMinValue(T minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(T maxValue) {
        this.maxValue = maxValue;
    }

    public abstract T getAbsoluteMin();
    public abstract T getAbsoluteMax();
}
