package com.image.imagerec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.image.imagerec.databinding.ActivityScannerBinding;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

public class ScannerActivity extends AppCompatActivity {

    private ActivityScannerBinding binding;
    private CodeScanner codeScanner;
    private  CodeScannerView scannerView;
    private String QRresult;
    private BottomSheetBehavior bottomSheetBehavior;

    private final int MY_CAMERA_PERMISSION_CODE=1, CAMERA_REQUEST=101;
    private final int MY_GALLERY_PERMISSION_CODE = 2, GALLERY_REQUEST = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intiValues();


        codeScanner = new CodeScanner(this,binding.scannerView);
        codeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            QRresult = result.getText();
            displayResult(QRresult);
        }));

        binding.scannerView.setOnClickListener(v -> codeScanner.startPreview());

        binding.webSearch.setOnClickListener(v -> {
            if (QRresult!=null && !TextUtils.isEmpty(QRresult)) {
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(QRresult));
                startActivity(webIntent);
            }
        });

        binding.copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(null, QRresult);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ScannerActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

    }

    private void displayResult(String qRresult) {
        binding.coordinatorLayout.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        binding.result.setText(qRresult);

        if (qRresult.contains("https")){
            binding.webSearch.setVisibility(View.VISIBLE);
        }else {
            binding.webSearch.setVisibility(View.GONE);
        }
    }

    private void intiValues() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        View bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        binding.coordinatorLayout.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        requestForCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();
        View bottomSheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        binding.coordinatorLayout.setVisibility(View.GONE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        requestForCamera();
    }

    private void requestForCamera() {

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        else
        {
            codeScanner.startPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                codeScanner.startPreview();
            }
            else
            {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

}