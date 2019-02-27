package de.spokli.scriptexecuter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ScriptCode extends AppCompatActivity {

    Button _btn_create = null;
    EditText _text_name = null;
    EditText _text_code = null;

    String name = "";
    String code = "";

    boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_code);

        getViewReferences();
        setAllEventListeners();

        Intent i = this.getIntent();
        name = i.getStringExtra("name");

        if (!name.isEmpty()) {

            editMode = true;

            _text_name.setText(name);
            _text_name.setEnabled(false);

            code = readScript(name);
            _text_code.setText(code);

            _btn_create.setText("Save changes");
        }
    }

    private void getViewReferences() {
        _text_name = (EditText) findViewById(R.id.text_name);
        _text_code = (EditText) findViewById(R.id.text_code);
        _btn_create = (Button) findViewById(R.id.btn_create);
    }

    private void setAllEventListeners() {
        _btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editMode) {
                    boolean success = editScript(_text_name.getText().toString(), _text_code.getText().toString());
                    if (success)
                        ScriptCode.this.finishActivity(0);
                    else
                        ScriptCode.this.finishActivity(-1);
                    ScriptCode.this.finish();
                } else {
                    if (Helpers.getExternalStorageWriteable() && !_text_name.getText().toString().isEmpty()) {
                        boolean success = createNewScript(_text_name.getText().toString(), _text_code.getText().toString());
                        if (success)
                            ScriptCode.this.finishActivity(1);
                        else
                            ScriptCode.this.finishActivity(-1);
                        ScriptCode.this.finish();
                    }
                }
            }
        });
    }

    private String readScript(String name) {
        File dir = Helpers.getDirectory();
        String code = "";

        File script = new File(dir + File.separator + name);
        if (script.exists()) {
            try {
                FileInputStream fis = new FileInputStream(script);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                code += br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return code;
    }

    private boolean createNewScript(String name, String code) {
        boolean success = false;

        File dir = Helpers.getDirectory();

        dir.mkdirs();

        File newScript = new File(dir + File.separator + name);
        if (!newScript.exists()) {
            try {
                success = newScript.createNewFile();

                if (success) {
                    FileOutputStream fos = new FileOutputStream(newScript);
                    PrintWriter pw = new PrintWriter(fos);
                    pw.print(code);
                    pw.flush();
                    pw.close();
                    fos.close();

                    Toast.makeText(this, "Created file " + newScript.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Script already existing", Toast.LENGTH_LONG).show();
        }

        return success;
    }

    private boolean editScript(String name, String code) {
        boolean success = false;

        File dir = Helpers.getDirectory();

        dir.mkdirs();

        File script = new File(dir + File.separator + name);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(script);
            PrintWriter pw = new PrintWriter(fos);
            pw.print(code);
            pw.flush();
            pw.close();
            fos.close();

            Toast.makeText(this, "Edited file " + script.getAbsolutePath(), Toast.LENGTH_LONG).show();
            success = true;
        } catch (IOException e) {
            success = false;
            e.printStackTrace();
        }
        return success;
    }

}
