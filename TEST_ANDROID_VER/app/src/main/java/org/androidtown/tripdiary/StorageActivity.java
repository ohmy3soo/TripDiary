package org.androidtown.tripdiary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by kms0080 on 2017-06-06.
 */

public class StorageActivity extends AppCompatActivity {
    ArrayList<String> items;
    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        items = new ArrayList<>() ;
        String fileName = "list.txt";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/TripDiary");

        directory.mkdirs();
        File file = new File(directory, fileName);
        items.clear();
        try {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fIn);
            if(fIn != null) {
                BufferedReader reader = new BufferedReader(isr);
                String str = "";
                while ((str = reader.readLine()) != null) {
                    items.add(str);
                }
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }catch (Throwable t) {
            t.printStackTrace();
        }

        adapter = new ArrayAdapter(this, android.R.layout.simple_selectable_list_item, items) ;

        ListView listview = (ListView) findViewById(R.id.listview) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){

                String item = ((TextView)view).getText().toString();

                Intent intent = new Intent(getApplicationContext(), TripActivity.class);
                intent.putExtra("fileName", item);
                startActivity(intent);
            }
        });

        //int count = getItemNum();

        adapter.notifyDataSetChanged();
    }

    /*
    public int getItemNum(){
        String fileName = "data.txt";
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/TripDiary");
        String str ="";
        directory.mkdirs();

        int itemNum=0;
        File file = new File(directory, fileName);
        try {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader reader = new BufferedReader(isr);
            str = reader.readLine();
            fIn.close();
            if(str == null)
                return 0;
            itemNum = Integer.parseInt(str);
            Toast.makeText(this, itemNum+"", Toast.LENGTH_SHORT).show();
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return itemNum;
    }
    */
}
