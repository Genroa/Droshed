package genrozun.droshed.model;

import java.util.Objects;

/**
 * Created by axelheine on 26/05/2017.
 */

public class Line {
    private final String id;
    private String name;

    public Line(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getID() { return id; }

    public String getName() { return name; }

    public void setName(String newName) { name = Objects.requireNonNull(newName); }
}
