package com.example.gallery;
import com.example.gallery.R;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView imageView;
    TextView tvName, tvPath, tvSize, tvDate;
    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.imageView);
        tvName    = findViewById(R.id.tvName);
        tvPath    = findViewById(R.id.tvPath);
        tvSize    = findViewById(R.id.tvSize);
        tvDate    = findViewById(R.id.tvDate);
        btnDelete = findViewById(R.id.btnDelete);

        // Get image path from Intent
        String imagePath = getIntent().getStringExtra("imagePath");
        File imageFile = new File(imagePath);

        // Show image
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        imageView.setImageBitmap(bitmap);

        // Show details
        tvName.setText("Name: " + imageFile.getName());
        tvPath.setText("Path: " + imageFile.getAbsolutePath());
        tvSize.setText("Size: " + (imageFile.length() / 1024) + " KB");
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date(imageFile.lastModified()));
        tvDate.setText("Date: " + date);

        // Delete button
        btnDelete.setOnClickListener(v -> {
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure you want to delete this image?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (imageFile.delete()) {
                            Toast.makeText(this, "Image deleted!", Toast.LENGTH_SHORT).show();
                            finish(); // Go back to gallery
                        } else {
                            Toast.makeText(this, "Could not delete!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
