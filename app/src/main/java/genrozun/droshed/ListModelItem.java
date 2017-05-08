package genrozun.droshed;

import java.util.Date;
import java.util.Objects;

/**
 * Created by axelheine on 04/05/2017.
 */

public class ListModelItem {
    private final String itemName;
    private Date lastModif;

    public ListModelItem(String itemName) {
        this.itemName = itemName;
    }

    public void setLastModif(Date lastModif) {
        this.lastModif = Objects.requireNonNull(lastModif);
    }

    public Date getLastModif() {
        return lastModif;
    }

    public String getItemName() {
        return itemName;
    }

}
