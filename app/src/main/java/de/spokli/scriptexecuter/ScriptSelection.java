package de.spokli.scriptexecuter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ScriptSelection extends Activity {

    Button _btn_add = null;
    Button _btn_edit = null;
    Button _btn_execute = null;
    ListView _list_scripts = null;

    TextView selectedItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_selection);

        getAllViewReferences();
        setAllEventListeners();

        _btn_edit.setEnabled(false);
        _btn_execute.setEnabled(false);

        if (Helpers.getExternalStorageAvailable()) {
            fillList();
        }
    }

    private void getAllViewReferences() {
        _btn_add = (Button) findViewById(R.id.btn_add);
        _btn_edit = (Button) findViewById(R.id.btn_edit);
        _btn_execute = (Button) findViewById(R.id.btn_execute);
        _list_scripts = (ListView) findViewById(R.id.list_scripts);
    }

    private void setAllEventListeners() {
        _btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScriptSelection.this, ScriptCode.class);
                intent.putExtra("name", "");
                intent.putExtra("code", "");
                startActivityForResult(intent, 0);
            }
        });

        _btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_list_scripts.getCheckedItemPosition() < 0){
                    _btn_edit.setEnabled(false);
                    _btn_execute.setEnabled(false);
                    return;
                }
                String name = "" + _list_scripts.getItemAtPosition(_list_scripts.getCheckedItemPosition());
                Intent intent = new Intent(ScriptSelection.this, ScriptCode.class);
                intent.putExtra("name", name);
                startActivityForResult(intent, 0);
            }
        });

        _list_scripts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(selectedItem != null) {
                    selectedItem.setBackgroundColor(Color.TRANSPARENT);
                }

                TextView newItem = (TextView) view;
                selectedItem = newItem;
                newItem.setBackgroundColor(Color.LTGRAY);

                _list_scripts.setItemChecked(position, true);
                _btn_edit.setEnabled(true);
                _btn_execute.setEnabled(true);
            }
        });

        _list_scripts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                _btn_edit.setEnabled(true);
                _btn_execute.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                _btn_edit.setEnabled(false);
                _btn_execute.setEnabled(false);
            }
        });

        _btn_execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeScript("" + _list_scripts.getItemAtPosition(_list_scripts.getCheckedItemPosition()));
            }
        });
    }

    private void executeScript(String name) {
        File dir = Helpers.getDirectory();
        File script = new File(dir + File.separator + name);
        String cmd = "bash " + script + ";";
        runAsRoot(cmd);
    }

    private void runAsRoot(String... cmds) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            DataInputStream is = new DataInputStream(p.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            for (String tmpCmd : cmds) {
                os.writeBytes(tmpCmd + "\n");
            }
            String line = br.readLine();
            os.writeBytes("exit\n");
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillList() {

        File dir = Helpers.getDirectory();

        File[] dirContent = dir.listFiles();
        if(dirContent == null || dirContent.length < 0){
            return;
        }

        ArrayList<String> fileNames = new ArrayList<>();

        for (File f : dirContent) {
            fileNames.add(f.getName());
        }

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
        _list_scripts.setAdapter(listAdapter);
        _list_scripts.setSelection(0);
        listAdapter.notifyDataSetChanged();
        _list_scripts.invalidate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 0){
            if(resultCode > 0){
                // Successful
                fillList();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_script_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
