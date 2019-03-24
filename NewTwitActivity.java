package com.example.user.twitter;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewTwitActivity extends AppCompatActivity {

    Button close;
    Button addPic;
    Button newTwit;
    EditText message;
    Uri imageUri;
    ImageView image;
    Button goCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_twit_acticity);

        close = findViewById(R.id.close);
        newTwit = findViewById(R.id.new_twit);
        message = findViewById(R.id.message);
        addPic = findViewById(R.id.add_picture);
        image = findViewById(R.id.image);
        goCamera = findViewById(R.id.goCamera);


        goCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    if (photoFile != null) {
                        Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.user.twitter.fileprovider", photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        imageUri = photoUri;
                        startActivityForResult(intent, 201);
                    }
                }
            }
        });




        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 200);

            }
        });


        newTwit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("message", message.getText().toString());
                if (imageUri != null)
                    intent.putExtra("image_uri", imageUri.toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200) {
            if (resultCode == RESULT_OK && data != null) {
                Log.d("NewTwitActivity", "image arrive");
                Uri selectedImage = data.getData();

                GlideApp.with(getApplicationContext()).load(selectedImage).into(image);

                imageUri = selectedImage;
            }
        }
        if (requestCode == 201){
            if(requestCode == RESULT_OK) {
                GlideApp.with(getApplicationContext()).load(imageUri).into(image);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(timeStamp, ".jpg", storageDir);

        return image;

    }

}
