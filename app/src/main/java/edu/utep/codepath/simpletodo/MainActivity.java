package edu.utep.codepath.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "items_position";
    public static final int EDIT_TEXT_CODE = 20;

    List<String> items; //model
    ItemsAdapter itemsAdapter; //render items
    //UI objects
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadItems();

        //Reference(s)
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);


        // EDIT TEXT Click LISTENER
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT,items.get(position) ); //pass data
                i.putExtra(KEY_ITEM_POSITION,position);
                startActivityForResult(i, EDIT_TEXT_CODE ); //segue
                Log.d("MainActivity", "single click at pos: "+position);
            }
        };
        // REMOVE Click Listener
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                items.remove(position); //remove from model
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "item was removed from list", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

        //Setup our list
        itemsAdapter = new ItemsAdapter(items, onLongClickListener, onClickListener);
        rvItems.setAdapter(itemsAdapter); //set adapter on recycler view
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //Add Button Listener
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etItem.getText().toString();
                items.add(todoItem);
                //notify item has been added
                itemsAdapter.notifyItemInserted(items.size()-1);
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "item was added to list", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    //handle result of editing
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getStringExtra(KEY_ITEM_TEXT); //get the text being passed
            int position = data.getExtras().getInt(KEY_ITEM_POSITION); //gets the position/index
            //update model
            items.set(position, itemText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(), "item updated correctly", Toast.LENGTH_SHORT).show();
        } else {
            Log.w("MainActivity", "Unknown call to OnActivityResult");
        }
    }


    //gets FILE which we store our toodo list items
    private File getDataFile(){
        return new File(getFilesDir(), "data.txt");
    }

    //Loads items, every time reading the data file
    private void loadItems(){
        try{
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset() )); //populate all lines from file into list
        }catch(IOException e){
            Log.e("MainActivity", "error reading items", e);
            items = new ArrayList<>();
        }
    }

    //Saves items, writing them into the data file
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "error writing items", e);
        }
    }

}