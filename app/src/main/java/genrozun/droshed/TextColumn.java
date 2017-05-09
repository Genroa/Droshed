package genrozun.droshed;

import java.util.Objects;

/**
 * Created by axelheine on 08/05/2017.
 */

class TextColumn implements Column {
    private final String id;
    private String name;
    private String value;

    public TextColumn(String id) {
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

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = Objects.requireNonNull(value);
    }
}
