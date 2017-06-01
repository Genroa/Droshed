package genrozun.droshed.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import genrozun.droshed.ListEditableItem;
import genrozun.droshed.R;
import genrozun.droshed.model.Column;
import genrozun.droshed.model.Line;
import genrozun.droshed.model.Model;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class DataProjectionActivity extends AppCompatActivity {

    private ArrayList<ListEditableItem> listEditableItems;
    private ArrayAdapter<ListEditableItem> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_projection);

        Intent intent = getIntent();
        listEditableItems = new ArrayList<>();

        Log.i("BUNDLE", intent.getBundleExtra("modelBundle").toString());
        Model model = (Model) intent.getBundleExtra("modelBundle").getSerializable("model");
        String projection = intent.getStringExtra("projection");
        String id = intent.getStringExtra("id");
        String targetID = intent.getStringExtra("targetID");

        if(projection.equals("column")) {
            Column col = model.getColumn(id);
            for(Line line : model.getLines()) {
                listEditableItems.add(new ListEditableItem(col.getValue(line.getID()), col.getInputType(), col.getName(), line.getID()));
            }
        } else {
            for(Column col : model.getColumns()) {
                listEditableItems.add(new ListEditableItem(col.getValue(id), col.getInputType(), col.getName(), col.getID()));
            }
        }

        adapter = new CustomAdapter(getApplicationContext(), listEditableItems);
        ListView lv = (ListView) findViewById(R.id.data_projection_list);
        lv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_disconnect:
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class CustomAdapter extends ArrayAdapter<ListEditableItem> {
        private ArrayList<ListEditableItem> items;

        public CustomAdapter(Context context, ArrayList<ListEditableItem> items) {
            super(context, 0, items);
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ListEditableItem e = items.get(position);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.editable_list_element, null);

            TextView title = (TextView) convertView.findViewById(R.id.editable_label);
            EditText edit = (EditText) convertView.findViewById(R.id.editable_edittext);

            edit.setInputType(e.getType());
            edit.setText(e.getValue().toString());
            edit.setHint("Entrez du texte ici");


            //rb.setText((version == 0 ? "Pas de données":"Version : " + version));
            return convertView;
        }
    }
}


