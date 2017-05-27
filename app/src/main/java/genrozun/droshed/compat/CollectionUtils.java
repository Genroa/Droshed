package genrozun.droshed.compat;

import java.util.HashMap;
import java.util.List;

/**
 * Created by genro on 27/05/2017.
 */

public class CollectionUtils {
    public static <T> HashMap<String, T> toMap(List<T> elements, Function<T, String> keyBuilder) {
        HashMap<String, T> hashMap = new HashMap<>();
        for (T e : elements) {
            hashMap.put(keyBuilder.apply(e), e);
        }
        return hashMap;
    }
}
