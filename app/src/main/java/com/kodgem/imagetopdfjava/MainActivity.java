package com.kodgem.imagetopdfjava;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private ImageView imageView;
    private Button selectButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=findViewById(R.id.image_view);
        selectButton=findViewById(R.id.pick_image);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,5);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 5 && resultCode == RESULT_OK && data != null){
            Uri selectedImageUri= data.getData();
            String[] filePath= {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri,filePath,null,null,null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePath[0]);
            String myPath= cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap= BitmapFactory.decodeFile(myPath);
            imageView.setImageBitmap(bitmap);

            PdfDocument pdfDocument=new PdfDocument();
            PdfDocument.PageInfo pi= new PdfDocument.PageInfo.Builder(bitmap.getWidth(),bitmap.getHeight(),1).create();
            PdfDocument.Page page = pdfDocument.startPage(pi);
            Canvas canvas= page.getCanvas();
            Paint paint=new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawPaint(paint);

            bitmap= Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
            paint.setColor(Color.BLUE);
            canvas.drawBitmap(bitmap,0,0,null);
            pdfDocument.finishPage(page);


            //saving the bitmap
            File root = new File(Environment.getExternalStorageState(),"PDF folder 12");
            if (!root.exists()){
                root.mkdir();
            }

            File file = new File(root,"picture.pdf");
            try {
                FileOutputStream fileOutputStream= new FileOutputStream(file);
                pdfDocument.writeTo(fileOutputStream);

            } catch (IOException e) {
                e.printStackTrace();
            }

            pdfDocument.close();
        }
    }
}
