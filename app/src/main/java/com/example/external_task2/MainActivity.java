package com.example.external_task2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadFileList();
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
    }


    private void loadFileList() {
        TextView tv = findViewById(R.id.textview);
        tv.setText("");
        if (currentDir != null) {
            for (File obj : currentDir.listFiles()) {
                if (obj.isDirectory()) {
                    tv.setText(tv.getText() + "\n" + obj.getName() + " > ");
                } else {
                    tv.setText(tv.getText() + "\n" + obj.getName());
                }
            }
        }
    }

    File currentDir = Environment.getExternalStorageDirectory();

    public void newFile(View view) {

        AlertDialog.Builder dailog = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText fileName = new EditText(this);
        EditText fileContent = new EditText(this);
        layout.addView(fileName);
        layout.addView(fileContent);
        dailog.setView(layout);
        dailog.setTitle("Create New File");
        dailog.setPositiveButton("Create", (dialog, which) -> {
            writeFile(fileName.getText().toString(), fileContent.getText().toString());
            loadFileList();
        }).setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        }).show();

    }

    private void writeFile(String fileName, String fileContent) {

        File file = new File(currentDir, fileName);
        try {
            FileOutputStream fs = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(fs);
            pw.println(fileContent);
            pw.flush();
            pw.close();
            fs.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void openFile(View view) {
        AlertDialog.Builder dailog = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText fileName = new EditText(this);
        layout.addView(fileName);
        dailog.setView(layout);
        dailog.setTitle("File/Directory Name to open");
        dailog.setPositiveButton("Open", (dialog, which) -> {
            readFile(fileName.getText().toString());

        }).setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        }).show();

    }

    private void readFile(String fileName) {
        File file = new File(currentDir, fileName);
        if (file.isFile()) {
            TextView tv = findViewById(R.id.textview);
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append("\n");

                }
                br.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            tv.setText(stringBuilder.toString());

        } else {
            currentDir = file;
            loadFileList();
        }
    }

    public void rename(View view) {

        AlertDialog.Builder dailog = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText oldName = new EditText(this);
        oldName.setHint("Enter file name");
        EditText newName = new EditText(this);
        newName.setHint("Enter new name");
        layout.addView(oldName);
        layout.addView(newName);
        dailog.setView(layout);
        dailog.setTitle("Rename File / Folder");
        dailog.setPositiveButton("Rename", (dialog, which) -> {
            File oldFile = new File(currentDir, oldName.getText().toString());
            File newFile = new File(currentDir, newName.getText().toString());
            oldFile.renameTo(newFile);
            loadFileList();
        }).setNegativeButton("Close", (dialog, which) -> {
            dialog.cancel();
        }).show();

    }

    public void delete(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText fileName = new EditText(this);
        fileName.setHint("Enter File Name to Delete");
        layout.addView(fileName);
        dialog.setView(layout);
        dialog.setTitle("Delete File");
        dialog.setPositiveButton("Delete", (dailog, which) -> {
            File file = new File(currentDir, fileName.getText().toString());
            file.delete();
            loadFileList();
        }).setNegativeButton("Close", (dailog, which) -> {
            dailog.cancel();
        }).show();

    }

    public void makeDir(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        EditText fileName = new EditText(this);
        fileName.setHint("Directory Name");
        layout.addView(fileName);
        builder.setView(layout);
        builder.setTitle("Create New Directory");
        builder.setPositiveButton("Create", (dailog, which) -> {
            File file = new File(currentDir, fileName.getText().toString());
            file.mkdir();
            loadFileList();
        }).setNegativeButton("Close", (dailog, which) -> {
            dailog.cancel();
        }).show();

    }

    public void dirUP(View view) {
        currentDir = currentDir.getParentFile();
        loadFileList();
    }

}