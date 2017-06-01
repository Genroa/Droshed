package genrozun.droshed.model;

import android.text.InputType;

import java.util.Map;

/**
 * Created by genro on 25/05/2017.
 */

public class IntegerColumn extends ValueColumn<Integer> {

    public IntegerColumn(String id, String name, Map<String, String> parameters) {
        super(id, name, parameters);
    }

    @Override
    public int getInputType() {
        return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED;
    }

    @Override
    public Integer getAbsoluteMin() {
        return Integer.MIN_VALUE;
    }

    @Override
    public Integer getAbsoluteMax() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setValue(String lineID, Integer newValue) {
        super.setValue(lineID, Math.max(getMinValue(), Math.min(getMaxValue(), newValue)));
    }

    @Override
    Integer stringToValue(String representation) {
        return Integer.valueOf(representation);
    }

    @Override
    public Integer getDefaultValue() {
        return 0;
    }
}
