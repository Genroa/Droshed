package genrozun.droshed;

import java.util.Objects;

/**
 * Created by axelheine on 08/05/2017.
 */

class TextColumn implements Column {
    private final String id;

    public TextColumn(String id) {
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
