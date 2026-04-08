package com.example.gallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

        // Ask for permissions first
        askPermissions();

        btnTakePhoto.setOnClickListener(v -> takePhoto());
        btnChooseFolder.setOnClickListener(v -> chooseFolder());

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ImageDetailActivity.class);
            intent.putExtra("imagePath", imageList.get(position).getAbsolutePath());
            startActivity(intent);
        });
    }

    void askPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 and above
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_MEDIA_IMAGES
                    }, REQUEST_PERMISSIONS);
        } else {
            // Android 12 and below
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            // After permissions granted load default folder
            currentFolder = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM), "Camera");
            loadImages(currentFolder);
        }
    }

    void takePhoto() {
        // Check camera permission first
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permission needed!", Toast.LENGTH_SHORT).show();
            askPermissions();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());

                // Save to Pictures folder - more compatible
                File folder = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), "MADAssignment");
                if (!folder.exists()) folder.mkdirs();

                currentPhotoFile = new File(folder, "IMG_" + timestamp + ".jpg");
                currentFolder = folder;

                Uri photoUri = FileProvider.getUriForFile(this,
                        getPackageName() + ".fileprovider", currentPhotoFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, REQUEST_CAMERA);

            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No camera app found!", Toast.LENGTH_SHORT).show();
        }
    }

    void chooseFolder() {
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

        if (requestCode == REQUEST_FOLDER && resultCode == RESULT_OK
                && data != null) {
            Uri uri = data.getData();
            String path = uri.getPath();
            if (path != null && path.contains(":")) {
                path = Environment.getExternalStorageDirectory()
                        + "/" + path.split(":")[1];
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
                    if (name.endsWith(".jpg") || name.endsWith(".jpeg")
                            || name.endsWith(".png") || name.endsWith(".gif")) {
                        imageList.add(file);
                    }
                }
            }
        }
        adapter = new ImageAdapter(this, imageList);
        gridView.setAdapter(adapter);

        if (imageList.isEmpty()) {
            Toast.makeText(this, "No images found. Take a photo or choose a folder!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentFolder != null) {
            loadImages(currentFolder);
        }
    }
}