package com.example.grocerylist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.example.grocerylist.Database.DatabaseHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> list = new ArrayList<>();
    ListView list_view;
    ArrayAdapter arrayAdapter;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list_view = findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, list);
        list_view.setAdapter(arrayAdapter);
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        loadItemsFromDatabase();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.pop_up_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.item_update:
                                //function for update
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_dialog, null, false);
                                builder.setTitle("Update Item");
                                final EditText editText = v.findViewById(R.id.etItem);
                                editText.setText(list.get(position));

                                //set custome view to dialog
                                builder.setView(v);

                                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!editText.getText().toString().isEmpty()) {
                                            list.set(position, editText.getText().toString().trim());
                                            arrayAdapter.notifyDataSetChanged();
                                            Toast.makeText(MainActivity.this, "Item Updated!", Toast.LENGTH_SHORT).show();

                                        } else {
                                            editText.setError("add item here !");
                                        }
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                builder.show();

                                break;

                            case R.id.item_del:
                                //fucntion for del
                                Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                                list.remove(position);
                                arrayAdapter.notifyDataSetChanged();

                                break;

                        }

                        return true;
                    }
                });

                //don't forgot this
                popupMenu.show();

            }
        });
    }

    private void loadItemsFromDatabase() {
        Cursor cursor = database.query(databaseHelper.getTableName(), null, null, null, null, null, null);
        list.clear();
        if (cursor.moveToFirst()) {
            int itemColumnIndex = cursor.getColumnIndex(databaseHelper.getItemColumnName());
            do {
                String item = cursor.getString(itemColumnIndex);
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        arrayAdapter.notifyDataSetChanged();
    }

    private void addItemToDatabase(String item) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.getItemColumnName(), item);
        long id = database.insert(DatabaseHelper.getTableName(), null, values);
        if (id != -1) {
            list.add(item);
            arrayAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(MainActivity.this, "Falha ao adicionar item ao banco de dados.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.add_item:
                _addItem();
                break;
        }
        return true;
    }

    private void _addItem() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Adicione um novo item");

        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_dialog, null, false);
        builder.setView(v);
        final EditText etItem = v.findViewById(R.id.etItem);
        builder.setPositiveButton("add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (!etItem.getText().toString().isEmpty()) {
                    String newItem = etItem.getText().toString().trim();
                    addItemToDatabase(newItem);
                } else {
                    etItem.setError("Adicione item aqui");
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        builder.show();

    }
    protected void onDestroy() {
        super.onDestroy();
        database.close();
        databaseHelper.close();
    }

}