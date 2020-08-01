package com.example.iostoandroidapplication.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.iostoandroidapplication.listeners.IImagePickerLister;
import com.example.iostoandroidapplication.enums.ImagePickerEnum;
import com.example.iostoandroidapplication.R;

import static com.example.iostoandroidapplication.MainActivity.CAMERA_STORAGE_REQUEST_CODE;
import static com.example.iostoandroidapplication.MainActivity.ONLY_CAMERA_REQUEST_CODE;
import static com.example.iostoandroidapplication.MainActivity.ONLY_STORAGE_REQUEST_CODE;


public class UiHelper {

    public void showImagePickerDialog(@NonNull Context callingClassContext, IImagePickerLister imagePickerLister) {
        new MaterialDialog.Builder(callingClassContext)
                .items(R.array.imagePicker)
                .canceledOnTouchOutside(true)
                .itemsCallback((dialog, itemView, position, text) -> {
                    if (position == 0) {
                        imagePickerLister.onOptionSelected(ImagePickerEnum.FROM_GALLERY);
                    } else if (position == 1) {
                        imagePickerLister.onOptionSelected(ImagePickerEnum.FROM_CAMERA);
                    }
                    dialog.dismiss();
                }).show();
    }

    public void toast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkSelfPermissions(@NonNull Activity activity) {
        if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_STORAGE_REQUEST_CODE);
            return false;
        } else if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, ONLY_CAMERA_REQUEST_CODE);
            return false;
        } else if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ONLY_STORAGE_REQUEST_CODE);
            return false;
        }
        return true;
    }
}
