package genrozun.droshed.model;

import java.util.Map;

/**
 * Created by genro on 25/05/2017.
 */

public class TextColumn extends AbstractColumn<String> {

    public TextColumn(String id, String name, Map<String, String> parameters) {
        super(id, name);
    }
}
