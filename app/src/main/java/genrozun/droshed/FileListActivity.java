package genrozun.droshed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
                                        //TODO: Call le service pour récupérer le modèle.

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
}
