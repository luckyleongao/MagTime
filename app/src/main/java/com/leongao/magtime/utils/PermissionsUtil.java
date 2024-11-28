//package com.leongao.magtime.utils;
//
//import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.provider.Settings;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.leongao.magtime.Manifest;
//
//import timber.log.Timber;
//
//public class PermissionsUtil {
//
//    private static final int STORAGE_PERMISSION_CODE = 23;
//
//    public boolean checkStoragePermissions(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            // Android is 11 (R) or above
//            return Environment.isExternalStorageManager();
//        } else {
//            // Below android 11
//            int write = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            int read = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE);
//
//            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
//        }
//    }
//
//    private void requestForStoragePermissions(Context context) {
//        // Android is 11 (R) or above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
//                intent.setData(uri);
//                storageActivityResultLauncher.launch(intent);
//            } catch (Exception e){
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                storageActivityResultLauncher.launch(intent);
//            }
//        } else {
//            // Below android 11
//            ActivityCompat.requestPermissions(
//                    (Activity) context,
//                    new String[]{
//                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                            android.Manifest.permission.READ_EXTERNAL_STORAGE
//                    },
//                    STORAGE_PERMISSION_CODE
//            );
//        }
//
//    }
//
//    private ActivityResultLauncher<Intent> storageActivityResultLauncher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
//                    new ActivityResultCallback<ActivityResult>() {
//
//                        @Override
//                        public void onActivityResult(ActivityResult o) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                                //Android is 11 (R) or above
//                                if (Environment.isExternalStorageManager()) {
//                                    //Manage External Storage Permissions Granted
//                                    Timber.d("onActivityResult: Manage External Storage Permissions Granted");
//                                } else {
//                                    Toast.makeText(, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                //Below android 11
//                                onRequestPermissionsResult();
//                            }
//                        }
//                    });
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == STORAGE_PERMISSION_CODE){
//            if(grantResults.length > 0){
//                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//
//                if(read && write){
//                    Toast.makeText(MainActivity.this, "Storage Permissions Granted", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(MainActivity.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//}
