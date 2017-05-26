package genrozun.droshed.model;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import genrozun.droshed.sync.DataManager;

/**
 * Created by axelheine on 25/05/2017.
 */

public class DataParser {
    private Model model;

    public DataParser(Model model) {
        this.model = model;
    }

    public void parseDataFromFile(Context context) {
        XmlPullParser parser = Xml.newPullParser();
        try {
            File dataFile = DataManager.getLastVersionData(context, model.getModelName());
            InputStream in_s = new FileInputStream(dataFile);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            parse(parser);
        } catch (IOException e) {
            //TODO gestion des exceptions
            Log.e(ModelParser.class.getName(), e.toString());
        } catch (XmlPullParserException e) {
            Log.e(ModelParser.class.getName(), e.toString());
        }
    }

    private void parse(XmlPullParser parser) throws XmlPullParserException {
        String tagName = "";
        String lineId = "";
        Column currentColumn = null;
        int currentColumnIndex= 0;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch(eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    switch (tagName) {
                        case "line":
                            lineId = parser.getAttributeValue(null, "id");
                            currentColumnIndex = 0;
                            break;
                        case "cell":
                            currentColumn = model.getColumn(currentColumnIndex);
                            break;
                    }

                    break;

                case XmlPullParser.TEXT:
                    switch(tagName) {
                        case "cell":
                            String text = parser.getText();
                            currentColumn.setValueFromString(lineId, text);
                    }
                    break;
                case XmlPullParser.END_TAG:
                    switch(parser.getName()) {
                        case "cell":
                            currentColumnIndex ++;
                            break;
                        case "line":
                            currentColumnIndex = 0;
                            break;
                    }
                    break;
            }
        }
    }
}
