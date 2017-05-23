package genrozun.droshed;

import java.util.Objects;

/**
 * Created by axelheine on 09/05/2017.
 */

class ValueColumn implements Column {
    private final String id;
    private String name;
    private Double value;
    private Double min;
    private Double max;

    public ValueColumn(String id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    @Override
    public String toString() {
        return "Column " + id + " current value " + value;
    }
}
