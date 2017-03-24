package com.friday.week8friday;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    /*
    create constants for the act of taking a picture, taking a video, saving a picture
    and granting storage permision. We also need to initialize and link the camera the video
    and the media controller
     */
    public static final int TAKE_PICTURE_ACT = 0;
    public static final int SAVE_PICTURE_ACT = 1;
    public static final int TAKE_VIDEO_ACT = 2;
    public static final int STORAGE_PERMISSION = 3;
    private ImageView iv;
    private VideoView vv;
    private MediaController mc;
    private String lastImageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView)findViewById(R.id.imageView);
        vv = (VideoView)findViewById(R.id.videoView);
        mc = new MediaController(this);

        mc.setMediaPlayer(vv);
        vv.setMediaController(mc);


    }


    /*
    Intent to take MediaStore.Action_image_caputure, if the intent is not null then start activity for result
    píctureIntent with the request code Take picture act
     */
    public void takePicture(View v){

        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(pictureIntent, TAKE_PICTURE_ACT);
        }
    }

    /*
    To save a picture first check the version of the android sdk the check if you have permision to write in external storage in the manifest
    if there is no permision then request for the permision
    else save the picture with permision
    Output: On request permision will be called so we need to override it for it to be useful
     */
    public void savePicture(View view){

        if (Build.VERSION.SDK_INT>=23 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION);
        }else{
            savePictureWithPermission();
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            savePictureWithPermission();
        }
    }


/*

 */
    public void savePictureWithPermission(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null){

            File photo = null;
            try {
                //build the filename
                String time = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
                String name = "IMAGE_" + time;

                //folder to save it!
                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                photo = File.createTempFile(name, ".jpg", directory);

                lastImageURI = photo.getAbsolutePath();

            }catch(IOException ioe){
                ioe.printStackTrace();
            }

            if (photo!=null){
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                startActivityForResult(intent, SAVE_PICTURE_ACT);

            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK){

            switch(requestCode){
                case TAKE_PICTURE_ACT:
                    Bundle extra = data.getExtras();
                    Bitmap image = (Bitmap)extra.get("data");
                    iv.setImageBitmap(image);
                    break;
                case SAVE_PICTURE_ACT:
                    Bitmap image2 = BitmapFactory.decodeFile(lastImageURI);
                    iv.setImageBitmap(image2);
                    break;
                case TAKE_VIDEO_ACT:
                    Uri video = data.getData();
                    vv.setVideoURI(video);
                    vv.start();
                    break;
            }
        }
    }

    public void takeVideo(View v){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, TAKE_VIDEO_ACT);
        }
    }
}
