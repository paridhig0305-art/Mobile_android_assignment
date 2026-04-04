package com.example.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnTakePhoto, btnChooseFolder;
    GridView gridView;
    ImageAdapter adapter;
    ArrayList<File> imageList = new ArrayList<>();

    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_PERMISSIONS = 2;
    static final int REQUEST_FOLDER = 3;

    File currentPhotoFile;
    File currentFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTakePhoto    = findViewById(R.id.btnTakePhoto);
        btnChooseFolder = findViewById(R.id.btnChooseFolder);
        gridView        = findViewById(R.id.gridView);

        // Set default folder to DCIM/Camera
        currentFolder = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");

        requestPermissions();

        btnTakePhoto.setOnClickListener(v -> takePhoto());

        btnChooseFolder.setOnClickListener(v -> chooseFolder());

        // When image is clicked open detail screen
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ImageDetailActivity.class);
            intent.putExtra("imagePath", imageList.get(position).getAbsolutePath());
            startActivity(intent);
        });
    }

    void requestPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
    }

    void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create file to save photo
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timestamp + ".jpg";

            // Save to DCIM/Camera folder
            File folder = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
            if (!folder.exists()) folder.mkdirs();

            currentPhotoFile = new File(folder, fileName);

            Uri photoUri = FileProvider.getUriForFile(this,
                    getPackageName() + ".fileprovider", currentPhotoFile);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    void chooseFolder() {
        // Open folder picker
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_FOLDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Toast.makeText(this, "Photo saved!", Toast.LENGTH_SHORT).show();
            loadImages(currentFolder);
        }

        if (requestCode == REQUEST_FOLDER && resultCode == RESULT_OK && data != null) {
            // Get folder path from URI
            Uri uri = data.getData();
            String path = uri.getPath();
            // Convert to real path
            if (path.contains(":")) {
                path = Environment.getExternalStorageDirectory() + "/" + path.split(":")[1];
            }
            currentFolder = new File(path);
            loadImages(currentFolder);
        }
    }

    void loadImages(File folder) {
        imageList.clear();
        if (folder != null && folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                            name.endsWith(".png") || name.endsWith(".gif")) {
                        imageList.add(file);
                    }
                }
            }
        }
        adapter = new ImageAdapter(this, imageList);
        gridView.setAdapter(adapter);

        if (imageList.isEmpty()) {
            Toast.makeText(this, "No images found in this folder", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadImages(currentFolder);
    }
}