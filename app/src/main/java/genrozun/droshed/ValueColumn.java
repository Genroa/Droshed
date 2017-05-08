package genrozun.droshed;

import java.util.Objects;

/**
 * Created by axelheine on 09/05/2017.
 */

class ValueColumn implements Column {
    private final String id;

    public ValueColumn(String id) {
        this.id = Objects.requireNonNull(id);
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }
}
