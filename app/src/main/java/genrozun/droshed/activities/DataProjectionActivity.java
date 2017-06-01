package genrozun.droshed.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
        listEditableItems = new ArrayList<>();

        Model m = Model.createModelFromModelFile("model1", getApplicationContext()); //TODO : Warning : Model chargé en dur
        String columnId = "col1";

        Column c = m.getColumn(columnId);
        for (Line l : m.getLines()) {
            listEditableItems.add(new ListEditableItem(c.getValue(l.getID()), c.getInputType(), l.getName()));
        }

        //adapter = new CustomAdapter(getApplicationContext(), R.layout.editable_list_element, listEditableItems);
        //ListView lv = (ListView) findViewById(R.id.data_projection_list);
        //lv.setAdapter(adapter);
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

        public CustomAdapter(Context context, int resource, ArrayList<ListEditableItem> items) {
            super(context, resource, items);
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


            //rb.setText((version == 0 ? "Pas de données":"Version : " + version));
            return convertView;
        }
    }
}


