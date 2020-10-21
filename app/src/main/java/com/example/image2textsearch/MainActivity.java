package com.example.image2textsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button searchBtn,detectTextBtn,captureBtn;
    ImageView imageView;
    TextView textView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;

    String resultTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBtn=findViewById(R.id.searchButton);
        captureBtn=findViewById(R.id.captureButton);
        detectTextBtn=findViewById(R.id.detectTextButton);

        imageView=findViewById(R.id.imageView);
        textView=findViewById(R.id.textView);

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
             dispatchTakePictureIntent();
             textView.setText("");
            }
        });

      detectTextBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              detectTextFromImage();
          }
      });

    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePictureIntent.resolveActivity(getPackageManager())!=null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }

    }

    private void detectTextFromImage()
    {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector= FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText)
            {
               displayTextFromImage(firebaseVisionText);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Error: "+ e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText)
    {
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if (blockList.size()==0){
            Toast.makeText(this,"No Text Found In Image.",Toast.LENGTH_SHORT).show();
        }else
        {
           /* for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks())
            {
                String text =block.getText();
                textView.setText(text);
            }*/
            
            for (int i=0 ; i< blockList.size();i++){
                List<FirebaseVisionText.Line> lines = blockList.get(i).getLines();
                for (int j=0;j<lines.size();j++){
                    List<FirebaseVisionText.Element> elements = lines.get(i).getElements();
                    for (int k=0;k<elements.size();k++){
                        for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks())
                        {
                            String text =block.getText();
                            textView.setText(text);
                        }
                    }
                }
            }
        }
    }


    public void openSearchActivity(View view) {
        Intent intent=new Intent(this,SearchActivity.class);

        resultTv=textView.getText().toString();
        intent.putExtra("result",resultTv);
        startActivity(intent);
    }



}