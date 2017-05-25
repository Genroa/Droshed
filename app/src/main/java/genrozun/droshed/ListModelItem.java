package genrozun.droshed;

import java.util.Date;
import java.util.Objects;

/**
 * Created by axelheine on 04/05/2017.
 */

public class ListModelItem {
    private final String itemName;
    private int version;

    public ListModelItem(String itemName, int version) {

        this.itemName = itemName;
        this.version = version;
    }

    public void setVersion(int version) {
        this.version = Objects.requireNonNull(version);
    }

    public int getVersion() {
        return version;
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public String toString() {
        return itemName+"/"+version;
    }
}
