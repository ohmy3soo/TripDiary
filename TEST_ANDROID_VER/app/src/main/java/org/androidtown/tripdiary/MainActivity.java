package org.androidtown.tripdiary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

        ImageButton buttonMenu1;
        ImageButton buttonMenu2;
        ImageButton buttonMenu3;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            buttonMenu1 = (ImageButton) findViewById(R.id.main_start);
            buttonMenu2 = (ImageButton) findViewById(R.id.main_archive);
            buttonMenu3 = (ImageButton) findViewById(R.id.main_help);


            buttonMenu1.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                    startActivity(intent);
                }
            });
            buttonMenu2.setOnClickListener(new View.OnClickListener(){
                public void onClick(View view){
                    Intent intent = new Intent(getApplicationContext(), StorageActivity.class);
                    startActivity(intent);
                }
            });
        /*
        buttonMenu3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });
        */
        }
}
