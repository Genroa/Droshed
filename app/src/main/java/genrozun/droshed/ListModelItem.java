package genrozun.droshed;

import android.content.Context;

import java.util.Date;
import java.util.Objects;

import genrozun.droshed.sync.DataManager;

/**
 * Created by axelheine on 04/05/2017.
 */

public class ListModelItem {
    private final String itemName;
    private Context context;

    public ListModelItem(Context context, String itemName) {
        this.itemName = itemName;
        this.context = context;
    }

    public int getVersion() {
        return DataManager.getLastVersionNumberForModel(context, itemName);
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public String toString() {
        return itemName+"/"+getVersion();
    }
}
