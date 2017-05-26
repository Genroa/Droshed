package genrozun.droshed.model;

import android.graphics.Path;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by axelheine on 04/05/2017.
 */

public class ModelParser {

    /*public static ArrayList<Column> parseFromModelFile(Path path) {
        XmlPullParser parser = Xml.newPullParser();
        ArrayList<Column> cols = null;
        try {
            InputStream in_s = null; //TODO: load model file from path
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            cols = ModelParser.parse(parser);
            Log.e(ModelParser.class.getName(), "cols" + cols.toString());
        } catch (IOException e) {
            //TODO gestion des exceptions
            Log.e(ModelParser.class.getName(), e.toString());
        } catch (XmlPullParserException e) {
            Log.e(ModelParser.class.getName(), e.toString());
        }

        return cols;
    }*/


}
