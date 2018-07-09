package org.androidtown.tripdiary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kms0080 on 2017-06-06.
 */

public class MoneybookActivity extends AppCompatActivity {


    //int sum=0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moneybook);

        final MoneyActivityDB dbHelper = new MoneyActivityDB(getApplicationContext(), "MoneyBook.db", null, 1);

        // 테이블에 있는 모든 데이터 출력
        final TextView result = (TextView) findViewById(R.id.result);
        //final TextView txtAvg=(TextView)findViewById(R.id.avg);

        final EditText etDate = (EditText) findViewById(R.id.date);
        final EditText etItem = (EditText) findViewById(R.id.item);
        final EditText etPrice = (EditText) findViewById(R.id.price);
        //final EditText etPeople=(EditText)findViewById(R.id.people);

        // 날짜는 현재 날짜로 고정
        // 현재 시간 구하기
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        // 출력될 포맷 설정
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        etDate.setText(simpleDateFormat.format(date));

        //result.setText(dbHelper.moneybook_getResult());

        // DB에 데이터 추가
        Button insert = (Button) findViewById(R.id.insert);
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = etDate.getText().toString();
                String item = etItem.getText().toString();
                //int people=Integer.parseInt(etPeople.getText().toString());
                int price = Integer.parseInt(etPrice.getText().toString());
                //int avg;
                dbHelper.moneybook_insert(date, item, price);
                result.setText(dbHelper.moneybook_getResult());

                //sum+=price;
                //avg=sum/people;
                //txtAvg.setText(String.valueOf(avg+" won"));
            }
        });

        // DB에 있는 데이터 수정
        Button update = (Button) findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = etItem.getText().toString();
                int price = Integer.parseInt(etPrice.getText().toString());

                dbHelper.moneybook_update(item, price);
                result.setText(dbHelper.moneybook_getResult());
            }
        });

        // DB에 있는 데이터 삭제
        Button delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = etItem.getText().toString();

                dbHelper.moneybook_delete(item);
                result.setText(dbHelper.moneybook_getResult());
            }
        });

        // DB에 있는 데이터 조회
        Button select = (Button) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText(dbHelper.moneybook_getResult());
            }
        });
    }
}