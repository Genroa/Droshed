package genrozun.droshed.model;

import java.util.Map;

/**
 * Created by genro on 25/05/2017.
 */

public class DecimalColumn extends ValueColumn<Double> {

    public DecimalColumn(String id, String name, Map<String, String> parameters) {
        super(id, name, parameters);
    }

    @Override
    public Double getAbsoluteMin() {
        return Double.MIN_VALUE;
    }

    @Override
    public void setValue(int line, Double newValue) {
        super.setValue(line, Math.max(getMinValue(), Math.min(getMaxValue(), newValue)));
    }

    @Override
    public Double getAbsoluteMax() {
        return Double.MAX_VALUE;
    }

    @Override
    Double stringToValue(String representation) {
        return Double.valueOf(representation);
    }
}
