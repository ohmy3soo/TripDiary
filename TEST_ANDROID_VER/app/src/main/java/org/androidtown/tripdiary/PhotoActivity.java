package org.androidtown.tripdiary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by kms0080 on 2017-06-06.
 */

public class PhotoActivity extends AppCompatActivity{
    Uri imageUri;
    ImageView userImage;
    Bitmap bm;
    EditText comment;
    EditText title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        title=(EditText)findViewById(R.id.title);
        userImage = (ImageView) findViewById(R.id.userImage);//사진 이미지뷰
        comment = (EditText) findViewById(R.id.comment);//코멘트
        ImageButton btnTakePicture = (ImageButton) findViewById(R.id.btnTakePicture);//사진찍는 버튼
        ImageButton btnSave=(ImageButton)findViewById(R.id.btnSave);

        //사진찍기
        btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, 1);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String fileName=new String(title.getText()+".txt");
                String picName=new String(title.getText()+".jpeg");
                File sdCard=Environment.getExternalStorageDirectory();
                File directory1=new File(sdCard.getAbsolutePath()+"/TripDiary/TripPicture");
                File directory2=new File(sdCard.getAbsolutePath()+"/TripDiary/TripComment");
                if(!directory1.exists())directory1.mkdirs();//사진
                if(!directory2.exists())directory2.mkdirs();//코멘트

                File file1=new File(directory2, fileName);
                try {
                    FileOutputStream fOut=new FileOutputStream(file1);
                    OutputStreamWriter osw=new OutputStreamWriter(fOut);
                    osw.write(comment.getText().toString());
                    osw.close();

                    FileOutputStream fos=new FileOutputStream(directory1+"/"+picName);
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();

                    Intent intent=new Intent();
                    intent.putExtra("Title", title.getText().toString());
                    setResult(1, intent);
                    Toast.makeText(getApplicationContext(), "Stored", Toast.LENGTH_SHORT).show();
                    finish();

                } catch (Exception e) {
                    Log.i("파일 저장 실패:", e.getMessage());
                }
            }
        });
    }

    //사진 찍은거 이미지뷰에 저장
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                bm = (Bitmap) data.getExtras().get("data");
                userImage.setImageBitmap(bm);
            }
        }
    }
}
