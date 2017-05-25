package genrozun.droshed.model;

import java.util.Map;

/**
 * Created by genro on 25/05/2017.
 */

public class DecimalColumn extends ValueColumn<Double> {

    public DecimalColumn(String id, String name, Map<String, String> parameters) {
        super(id, name, parameters);
        setMinValue(Math.max(Double.MIN_VALUE, getMinValue()));
        setMaxValue(Math.min(Double.MAX_VALUE, getMaxValue()));
    }



    @Override
    Double stringToValue(String representation) {
        return Double.valueOf(representation);
    }
}
