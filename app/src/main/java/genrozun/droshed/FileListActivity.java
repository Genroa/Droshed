package genrozun.droshed;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.logging.XMLFormatter;

public class FileListActivity extends AppCompatActivity {

    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_file_list);

        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.addFab);

        addFab.setOnClickListener((v) -> {
            LayoutInflater li = LayoutInflater.from(getApplicationContext());
            View promptView = li.inflate(R.layout.prompt_dialog_layout, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FileListActivity.this);

            alertDialogBuilder.setView(promptView);

            final EditText userInput = (EditText) promptView.findViewById(R.id.editTextDialogUserInput);

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    result = userInput.getText().toString();

                                    if (result.length() == 0) {
                                        Snackbar.make(layout, "Merci d'entrer le nom d'un modèle", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        /*
                                         * TODO:
                                         *  - Call le service pour récupérer le modèle.
                                         *  - Ajouter le modèle dans la liste des models présents (stockée)
                                         */

                                    }
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


        });


        /*
        * -----------------------------------
        * |                                 |
        * |    Following code only here     |
        * |            for tests            |
        * |                                 |
        * -----------------------------------
        */
        HashMap<String, Function<HashMap<String,String>, Column>> columnTypes = new HashMap<>();

        /*(map) -> {
            Log.d(FileListActivity.class.getName(), map.get("id"));
            TextColumn tc = new TextColumn(map.get("id"));
            tc.setName(map.get("name"));
            tc.setValue(map.get("value"));
            return tc;
        }*/
        columnTypes.put("text", new Function<HashMap<String, String>, Column>() {
            @Override
            public Column apply(HashMap<String, String> map) {
                Log.d(FileListActivity.class.getName(), map.get("id"));
                return new TextColumn(map.get("id"));
            }
        });

        /*(map) -> {
            Log.d(FileListActivity.class.getName(), map.get("id"));
            ValueColumn vc = new ValueColumn(map.get("id"));
            vc.setName(map.get("name"));
            vc.setValue(Double.parseDouble(map.get("value")));
            vc.setMin(Double.parseDouble(map.get("min")));
            vc.setMax(Double.parseDouble(map.get("max")));
            return vc;
        }*/
        columnTypes.put("value", new Function<HashMap<String, String>, Column>() {
            @Override
            public Column apply(HashMap<String, String> map) {
                Log.d(FileListActivity.class.getName(), map.get("id"));
                return new ValueColumn(map.get("id"));
            }
        });

        ModelParser mp = new ModelParser(columnTypes);
        XmlPullParser parser = Xml.newPullParser();
        try {
            InputStream in_s = getResources().openRawResource(R.raw.model1);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            mp.parse(parser);
        } catch (IOException e) {
            //TODO gestion des exceptions
        } catch (XmlPullParserException e) {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_disconnect) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public class CustomAdapter extends ArrayAdapter<ListModelItem> {
        private ArrayList<ListModelItem> items;
        private int ressource;

        public CustomAdapter(Context context, int resource, ArrayList<ListModelItem> items) {
            super(context, resource, items);
            this.ressource = ressource;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListModelItem l = items.get(position);
            View v = convertView;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.file_model_list_element_layout, null);

            TextView tv = (TextView) v.findViewById(R.id.model_name);
            TextView rb = (TextView) v.findViewById(R.id.model_last_update);

            tv.setText(l.getItemName());
            rb.setText(l.getLastModif().toString());

            return v;
        }
    }
}
