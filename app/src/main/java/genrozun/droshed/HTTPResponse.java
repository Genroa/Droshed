package genrozun.droshed;

import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Created by genro on 23/05/2017.
 */

public class HTTPResponse {
    public final HttpURLConnection header;
    public final String body;

    public HTTPResponse(HttpURLConnection header, String body) {
        this.header = header;
        this.body = body;
    }
}
