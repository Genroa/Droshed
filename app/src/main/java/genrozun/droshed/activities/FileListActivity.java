package genrozun.droshed.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import genrozun.droshed.model.Model;
import genrozun.droshed.sync.DataManager;
import genrozun.droshed.ListModelItem;
import genrozun.droshed.R;
import genrozun.droshed.sync.SheetUpdateService;

public class FileListActivity extends AppCompatActivity {
    private String modelName;
    private Context appContext;
    private RelativeLayout layout;
    ArrayList<ListModelItem> models;
    CustomAdapter adapter;

    private BroadcastReceiver newModelReceiver;
    private BroadcastReceiver updateModelReceiver;

    public FileListActivity() {
        // New model receiver
        newModelReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String result = intent.getStringExtra("status");
                if (result.equals(SheetUpdateService.OPERATION_OK)) {
                    Snackbar.make(layout, "Success getting model", Snackbar.LENGTH_SHORT).show();
                    String modelName = intent.getStringExtra("model_name");
                    int modelVersion = DataManager.getLastVersionNumberForModel(getApplicationContext(), modelName);
                    models.add(new ListModelItem(modelName, modelVersion));
                    adapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(layout, "Le modèle n'existe pas.", Snackbar.LENGTH_SHORT).show();
                }
            }
        };

        //Update model receiver
        updateModelReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String modelName = intent.getStringExtra("model_name");
                int modelVersion = intent.getIntExtra("model_version", 0);
                for (ListModelItem item: models) {
                    if (item.getItemName().equals(modelName)) {
                        item.setVersion(modelVersion);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);
        models = toListItems(DataManager.getModelsList(getApplicationContext()));
        LocalBroadcastManager.getInstance(this).registerReceiver(newModelReceiver, new IntentFilter("droshed-new-model"));
        LocalBroadcastManager.getInstance(this).registerReceiver(updateModelReceiver, new IntentFilter("droshed-sync"));
        this.appContext = getApplicationContext();

        layout = (RelativeLayout) findViewById(R.id.activity_file_list);

        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.addFab);

        addFab.setOnClickListener((v) -> {
            LayoutInflater li = LayoutInflater.from(getApplicationContext());
            View promptView = li.inflate(R.layout.prompt_dialog_layout, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FileListActivity.this);

            alertDialogBuilder.setView(promptView);

            final EditText userInput = (EditText) promptView.findViewById(R.id.editTextDialogUserInput);

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",(DialogInterface dialog, int id) -> {
                        modelName = userInput.getText().toString();

                        if (modelName.length() == 0) {
                            Snackbar.make(layout, "Merci d'entrer le nom d'un modèle", Snackbar.LENGTH_LONG).show();
                        } else {
                            Log.i(FileListActivity.class.getName(), modelName);
                            SheetUpdateService.startGetNewModel(appContext, modelName);
                        }
                    })
                    .setNegativeButton("Cancel",(DialogInterface dialog, int id) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        adapter = new CustomAdapter(getApplicationContext(), R.layout.file_model_list_element_layout, models);
        ListView lv = (ListView) layout.findViewById(R.id.list_view_models);
        lv.setAdapter(adapter);

        /*
        * -----------------------------------
        * |                                 |
        * |    Following code only here     |
        * |    for tests of xml parser      |
        * |                                 |
        * -----------------------------------
        */
        /*HashMap<String, Function<HashMap<String,String>, Column>> columnTypes = new HashMap<>();

        /*(map) -> {
            Log.d(FileListActivity.class.getName(), map.get("id"));
            TextColumn tc = new TextColumn(map.get("id"));
            tc.setName(map.get("name"));
            tc.setValueFromString(map.get("value"));
            return tc;
        }*/
        /*columnTypes.put("text", new Function<HashMap<String, String>, Column>() {
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
            vc.setValueFromString(Double.parseDouble(map.get("value")));
            vc.setMin(Double.parseDouble(map.get("min")));
            vc.setMax(Double.parseDouble(map.get("max")));
            return vc;
        }*/
        /*columnTypes.put("value", new Function<HashMap<String, String>, Column>() {
            @Override
            public Column apply(HashMap<String, String> map) {
                Log.d(FileListActivity.class.getName(), map.get("id"));
                return new ValueColumn(map.get("id"));
            }
        });*/

        /*Log.e(FileListActivity.class.getName(), "before");
        ModelParser mp = new ModelParser();
        XmlPullParser parser = Xml.newPullParser();
        ArrayList<Column> cols = null;
        try {
            InputStream in_s = getResources().openRawResource(R.raw.model1);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in_s, null);
            cols = mp.parse(parser);
            Log.e(FileListActivity.class.getName(), "cols" + cols.toString());
        } catch (IOException e) {
            //TODO gestion des exceptions
            Log.e(FileListActivity.class.getName(), e.toString());
        } catch (XmlPullParserException e) {
            Log.e(FileListActivity.class.getName(), e.toString());
        }*/

        /*Model m = Model.createModelFromModelFile("model1", getApplicationContext());
        Log.i(FileListActivity.class.getName(), m.toString());*/


        ListView modelList = (ListView) findViewById(R.id.list_view_models);
        modelList.setOnItemClickListener((adapter, view, position, id) -> {
            ListModelItem item = (ListModelItem) adapter.getItemAtPosition(position);
            Log.i("MODELLIST", item.toString());

            Intent intent = new Intent(getApplicationContext(), SheetEditActivity.class);
            intent.putExtra("model_name", item.getItemName());

            startActivity(intent);
        });

        modelList.setOnItemLongClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            LayoutInflater li = LayoutInflater.from(getApplicationContext());
            View promptView = li.inflate(R.layout.prompt_delete_layout, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FileListActivity.this);
            alertDialogBuilder.setView(promptView);

            TextView deleteText = (TextView) promptView.findViewById(R.id.delete_prompt_text);

            String modelName = models.get(position).getItemName();
            deleteText.setText("Do you really want to delete : " + modelName);

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", (DialogInterface dialog, int clickId) ->{
                        models.remove(position);
                        DataManager.deleteModelFromModelsList(appContext, modelName);
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", (DialogInterface dialog, int clickId) -> {
                        dialog.cancel();
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        });
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

        public CustomAdapter(Context context, int resource, ArrayList<ListModelItem> items) {
            super(context, resource, items);
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListModelItem l = items.get(position);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.file_model_list_element_layout, null);

            TextView tv = (TextView) convertView.findViewById(R.id.model_name);
            TextView rb = (TextView) convertView.findViewById(R.id.model_last_version);

            tv.setText(l.getItemName());
            int version = l.getVersion();
            rb.setText((version == 0 ? "Pas de données":"Version : " + version));

            return convertView;
        }
    }

    private ArrayList<ListModelItem> toListItems(ArrayList<String> models) {
        ArrayList<ListModelItem> items = new ArrayList<>(models.size());
        for(String modelName : models) {
            Log.i("TO_ITEM", "Item to convert: "+modelName);
            items.add(new ListModelItem(modelName, DataManager.getLastVersionNumberForModel(getApplicationContext(), modelName)));
        }
        return items;
    }
}
