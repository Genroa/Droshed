package genrozun.droshed.model;

import java.util.Map;

/**
 * Created by genro on 25/05/2017.
 */

public class IntegerColumn extends ValueColumn<Integer> {

    public IntegerColumn(String id, String name, Map<String, String> parameters) {
        super(id, name, parameters);
        setMinValue(Math.max(Integer.MIN_VALUE, getMinValue()));
        setMaxValue(Math.min(Integer.MAX_VALUE, getMaxValue()));
    }

    @Override
    public void setValue(int line, Integer newValue) {
        super.setValue(line, Math.max(getMinValue(), Math.min(getMaxValue(), newValue)));
    }

    @Override
    Integer stringToValue(String representation) {
        return Integer.valueOf(representation);
    }
}
