package com.example.iostoandroidapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.iostoandroidapplication.enums.ImagePickerEnum;
import com.example.iostoandroidapplication.listeners.IImagePickerLister;
import com.example.iostoandroidapplication.utils.FileUtils;
import com.example.iostoandroidapplication.utils.UiHelper;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity implements IImagePickerLister {

    private static final int CAMERA_ACTION_PICK_REQUEST_CODE = 610;
    private static final int PICK_IMAGE_GALLERY_REQUEST_CODE = 609;
    public static final int CAMERA_STORAGE_REQUEST_CODE = 611;
    public static final int ONLY_CAMERA_REQUEST_CODE = 612;
    public static final int ONLY_STORAGE_REQUEST_CODE = 613;

    private String currentPhotoPath = "";
    private UiHelper uiHelper = new UiHelper();
    private ImageView imageView;
    private ImageView textView1;
    private SeekBar seekBar;
    TargetView targetView;

    TextView xMin, xMax, yMin, yMax, radius, cPoint;

    Uri image_uri2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.selectPictureButton).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                if (uiHelper.checkSelfPermissions(this))
                    uiHelper.showImagePickerDialog(this, (IImagePickerLister) this);
        });
        imageView = (ImageView) findViewById(R.id.imageView);

        xMax = findViewById(R.id.x_max);
        xMin = findViewById(R.id.x_min);
        yMax = findViewById(R.id.y_max);
        yMin = findViewById(R.id.y_min);
        radius = findViewById(R.id.radius);
        cPoint = findViewById(R.id.c_point);

        final ImageView iv = findViewById(R.id.textView1);
        textView1 = findViewById(R.id.textView1);

        final SeekBar sk = (SeekBar) findViewById(R.id.seekBar);
        targetView = findViewById(R.id.target_view);

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int p = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
//                if(p < 1000)
//                {
//                    p=1000;
//                    seekBar.setProgress(p);
//                }
//            }
                Toast.makeText(MainActivity.this, "" + p, Toast.LENGTH_SHORT);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                p = progress;
                double scale = ((p / 40.0f) + 1);

                targetView.setPoints(targetView.xCord, targetView.yCord, (p + 100));


            }
        });

        findViewById(R.id.calculate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });

        targetView.setPoints(500, 500, 100);

        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    float screenX = event.getX();
                    float screenY = event.getY();
                    int viewX = (int) (screenX - v.getLeft());
                    int viewY = (int) (screenY - v.getTop());

                    if (targetView.radius != 0)
                        targetView.setPoints(viewX, viewY, targetView.radius);
                    else
                        targetView.setPoints(viewX, viewY, 100);

                    return true;
                }
                return false;
            }
        });
    }

    private void calculate() {
        Log.i("infoo", "xList: " + targetView.listX.toString());
        Log.i("infoo", "yList: " + targetView.listY.toString());
        Log.i("infoo", "a: " + targetView.a);

        xMax.setText("xMax: " + (targetView.listX.get(targetView.listX.size() - 1) + targetView.xCord));
        xMin.setText("xMin: " + (targetView.listX.get(0) + targetView.xCord));
        yMax.setText("yMax: " + (targetView.listY.get(0) + targetView.yCord));
        yMin.setText("yMin: " + (targetView.listX.get(targetView.listX.size() - 1) + targetView.yCord));
        cPoint.setText("cPoint: (" + targetView.centerPoint.x + ", " + targetView.centerPoint.y + ")");
        radius.setText("Radius:  " + (targetView.radius));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                uiHelper.showImagePickerDialog(this, (IImagePickerLister) this);
            else if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                uiHelper.toast(this, "Visual Intelligence needs Storage access in order to store your profile picture.");
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                uiHelper.toast(this, "Visual Intelligence needs Camera access in order to take profile picture.");
                finish();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                uiHelper.toast(this, "Visual Intelligence needs Camera and Storage access in order to take profile picture.");
                finish();
            }
        } else if (requestCode == ONLY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                uiHelper.showImagePickerDialog(this, (IImagePickerLister) this);
            else {
                uiHelper.toast(this, "Visual Intelligence needs Camera access in order to take profile picture.");
                finish();
            }
        } else if (requestCode == ONLY_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                uiHelper.showImagePickerDialog(this, (IImagePickerLister) this);
            else {
                uiHelper.toast(this, "Visual Intelligence needs Storage access in order to store your profile picture.");
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_ACTION_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            //Uri uri = Uri.parse(currentPhotoPath);

            try {

                File file = getImageFile();
                Uri destinationUri = Uri.fromFile(file);
                openCropActivity(image_uri2, destinationUri);
            } catch (Exception e) {
                uiHelper.toast(this, "Error inside on activity result// camera action");
            }

            //openCropActivity(uri, uri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = UCrop.getOutput(data);
                showImage(uri);
            }
        } else if (requestCode == PICK_IMAGE_GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try {
                Uri sourceUri = data.getData();
                File file = getImageFile();
                Uri destinationUri = Uri.fromFile(file);
                openCropActivity(sourceUri, destinationUri);
            } catch (Exception e) {
                uiHelper.toast(this, "Error inside on activity result// gallery action");
            }
        }
    }

    private void openImagesDocument() {
       /*Intent pictureIntent = new Intent(Intent.ACTION_GET_CONTENT);
       pictureIntent.setType("image/*");
       pictureIntent.addCategory(Intent.CATEGORY_OPENABLE);
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
           String[] mimeTypes = new String[]{"image/jpeg", "image/png"};
           pictureIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
       }
       startActivityForResult(Intent.createChooser(pictureIntent, "Select Picture"), PICK_IMAGE_GALLERY_REQUEST_CODE);*/
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, PICK_IMAGE_GALLERY_REQUEST_CODE);
    }

    private void showImage(Uri imageUri) {
        try {
            File file;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                file = FileUtils.getFile(this, imageUri);
            } else {
                file = new File(currentPhotoPath);
            }
            InputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            uiHelper.toast(this, "Please select different profile picture.//inside show picture");
        }
    }

    @Override
    public void onOptionSelected(ImagePickerEnum imagePickerEnum) {
        if (imagePickerEnum == ImagePickerEnum.FROM_CAMERA)
            openCamera();
        else if (imagePickerEnum == ImagePickerEnum.FROM_GALLERY)
            openImagesDocument();
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        // Uri image_uri2;
        image_uri2 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri2);
        startActivityForResult(cameraIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
    }

    /*
          Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
           File file;
           try {
               file = getImageFile(); // 1
           } catch (Exception e) {
               e.printStackTrace();
               uiHelper.toast(this, "Please take another image -- error inside opencamera");
               return;
           }
           Uri uri;
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) // 2
               uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.concat(".provider"), file);
           else
               uri = Uri.fromFile(file); // 3
           pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 4
           startActivityForResult(pictureIntent, CAMERA_ACTION_PICK_REQUEST_CODE);
       }
    */
    private File getImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                ), "Camera"
        );
        System.out.println(storageDir.getAbsolutePath());
        if (storageDir.exists())
            System.out.println("File exists");
        else
            System.out.println("File not exists");
        File file = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        currentPhotoPath = "file:" + file.getAbsolutePath();
        return file;
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.colorAccent));
        //options.setCropGridStrokeWidth((int) 10);
        options.setMaxBitmapSize(10000);
        UCrop.of(sourceUri, destinationUri)
                .withMaxResultSize(1000, 1000)
                .withAspectRatio(5f, 5f)
                .start(this);
    }
}

