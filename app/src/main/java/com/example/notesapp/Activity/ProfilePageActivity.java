package com.example.notesapp.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.notesapp.AppDataBase;
import com.example.notesapp.DAO.ProfileDao;
import com.example.notesapp.DataModelClass.ProfileData;
import com.example.notesapp.R;
import com.example.notesapp.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePageActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int GALLERY_REQUEST = 101;
    private static final int CAMERA_PERMISSION = 2000;
    private static final int LOCATION_PERMISSION_REQUEST = 3000;

    private Uri selectedImageUri = null;
    private static final String TAG = "ProfileImageFix";

    CircleImageView profileImage;
    TextInputEditText name, phno, address;
    TextView locationText, loggedUserName;
    Button saveBtn, editBtn, locationBtn, btnChangePhoto;

    int userId;
    ProfileData existingProfile;
    ProfileDao profileDao;

    FusedLocationProviderClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        getSupportActionBar().hide();



        // UI
        profileImage = findViewById(R.id.imgProfile);
        name = findViewById(R.id.edtFullName);
        phno = findViewById(R.id.edtMobile);
        address = findViewById(R.id.edtAddressLine);
        locationText = findViewById(R.id.txtLocation);
        loggedUserName = findViewById(R.id.txtLoggedUser);
        saveBtn = findViewById(R.id.btnSaveProfile);
        editBtn = findViewById(R.id.btnEditProfile);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        locationBtn = findViewById(R.id.btnGetLocation);

        // Session
        SessionManager sm = new SessionManager(this);
        userId = sm.getUserId();
        loggedUserName.setText("Logged in as: " + sm.getUserName());

        // DB
        profileDao = AppDataBase.getInstance(this).profileDao();
        locationClient = LocationServices.getFusedLocationProviderClient(this);

        btnChangePhoto.setOnClickListener(v -> showImagePickerDialog());
        saveBtn.setOnClickListener(v -> saveProfile());
        editBtn.setOnClickListener(v -> enableEditing());
        locationBtn.setOnClickListener(v -> getCurrentLocation());

        loadUserProfile();

        animateView(editBtn,R.anim.scale);
        animateView(saveBtn,R.anim.scale);
    }

    // ----------------------------------------------------------
    // IMAGE PICKER DIALOG
    // ----------------------------------------------------------
    private void showImagePickerDialog() {
        String[] options = {"Camera", "Gallery"};

        new AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                })
                .show();
    }

    // ----------------------------------------------------------
    // OPEN CAMERA
    // ----------------------------------------------------------
    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION
            );
            return;
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    // ----------------------------------------------------------
    // OPEN GALLERY
    // ----------------------------------------------------------
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    // ----------------------------------------------------------
    // onActivityResult (Google Photos FIX applied)
    // ----------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        //  GALLERY PICK
        if (requestCode == GALLERY_REQUEST) {

            Uri pickedUri = data.getData();
            if (pickedUri == null) return;

            // Copy URI safely (Google Photos fix)
            Uri safeUri = copyUriToInternalFile(pickedUri);

            if (safeUri != null) {
                selectedImageUri = safeUri;
                profileImage.setImageURI(safeUri);
            } else {
                Toast.makeText(this, "Cannot load image. Try a different one.", Toast.LENGTH_SHORT).show();
            }

            enableEditing();
            return;
        }

        //  CAMERA CAPTURE
        if (requestCode == CAMERA_REQUEST && data.getExtras() != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                Uri uri = getImageUriFromBitmap(photo);
                selectedImageUri = uri;
                profileImage.setImageURI(uri);
            }
            enableEditing();
        }
        animateView(profileImage,R.anim.fade_in);
    }

    // ----------------------------------------------------------
    // Copy URI to Internal Storage (Fix for Google Photos)
    // ----------------------------------------------------------
    private Uri copyUriToInternalFile(Uri sourceUri) {
        try {
            InputStream in = getContentResolver().openInputStream(sourceUri);
            if (in == null) return null;

            String fileName = "profile_" + System.currentTimeMillis() + ".jpg";
            File outFile = new File(getFilesDir(), fileName);

            OutputStream out = new FileOutputStream(outFile);
            byte[] buffer = new byte[4096];
            int length;

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            out.flush();
            out.close();
            in.close();

            return Uri.fromFile(outFile);

        } catch (Exception e) {
            Log.e(TAG, "Error copying Google Photos URI", e);
            return null;
        }
    }

    // Converts camera bitmap → URI
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "Profile_Pic",
                null
        );
        return Uri.parse(path);
    }

    // ----------------------------------------------------------
    // SAVE PROFILE
    // ----------------------------------------------------------
    private void saveProfile() {
        String fullName = name.getText().toString().trim();
        String mobile = phno.getText().toString().trim();
        String addr = address.getText().toString().trim();
        String loc = locationText.getText().toString().trim();

        Executors.newSingleThreadExecutor().execute(() -> {

            if (existingProfile == null) {
                ProfileData newP = new ProfileData(
                        userId,
                        fullName,
                        mobile,
                        addr,
                        loc,
                        selectedImageUri != null ? selectedImageUri.toString() : null
                );
                profileDao.insertUser(newP);

            } else {
                existingProfile.fullName = fullName;
                existingProfile.mobile = mobile;
                existingProfile.address = addr;
                existingProfile.currentAddress = loc;

                if (selectedImageUri != null)
                    existingProfile.imageUri = selectedImageUri.toString();

                profileDao.updateUser(existingProfile);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show();
                disableEditing();
            });
        });
    }

    // ----------------------------------------------------------
    // LOAD PROFILE
    // ----------------------------------------------------------
    private void loadUserProfile() {
        Executors.newSingleThreadExecutor().execute(() -> {

            existingProfile = profileDao.getProfileData(userId);

            runOnUiThread(() -> {

                if (existingProfile != null) {
                    // EXISTING USER → FILL FIELDS + DISABLE EDITING
                    name.setText(existingProfile.fullName);
                    phno.setText(existingProfile.mobile);
                    address.setText(existingProfile.address);
                    locationText.setText(existingProfile.currentAddress);

                    if (existingProfile.imageUri != null && !existingProfile.imageUri.isEmpty()) {
                        selectedImageUri = Uri.fromFile(new File(existingProfile.imageUri));
                        profileImage.setImageURI(selectedImageUri);
                    }

                    disableEditing();  // existing user’s fields locked

                } else {
                    // NEW USER → ENABLE editing immediately
                    enableEditing();
                }
            });
        });
        animateView(profileImage,R.anim.fade_in);
        animateView(name,R.anim.slide_up);
        animateView(phno,R.anim.slide_up);
        animateView(address,R.anim.slide_up);
        animateView(saveBtn,R.anim.fade_in);
    }


    // ----------------------------------------------------------
    // ENABLE / DISABLE editing
    // ----------------------------------------------------------
    private void enableEditing() {
        name.setEnabled(true);
        phno.setEnabled(true);
        address.setEnabled(true);
        locationBtn.setEnabled(true);
        saveBtn.setEnabled(true);
        btnChangePhoto.setEnabled(true);

        animateView(name, R.anim.slide_up);
        animateView(phno, R.anim.slide_up);
        animateView(address, R.anim.slide_up);
        animateView(locationBtn, R.anim.slide_up);
    }

    private void disableEditing() {
        name.setEnabled(false);
        phno.setEnabled(false);
        address.setEnabled(false);
        locationBtn.setEnabled(false);
        saveBtn.setEnabled(false);
        btnChangePhoto.setEnabled(false);

        animateView(saveBtn, R.anim.fade_in);
    }

    // ----------------------------------------------------------
    // LOCATION FETCH
    // ----------------------------------------------------------
    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST
            );
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                locationText.setText("Lat: " + lat + " | Lng: " + lng);

                getAddressLatLng(this, lat, lng, addr ->
                        locationText.setText("Address: " + addr));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults,
                                           int deviceId) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);

        if (requestCode == CAMERA_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }

        if (requestCode == LOCATION_PERMISSION_REQUEST &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }

    // ----------------------------------------------------------
    // Reverse Geocode
    // ----------------------------------------------------------
    public void getAddressLatLng(Context c, double lat, double lng, AddressCallback cb) {

        new Thread(() -> {

            String txt = "";
            Geocoder g = new Geocoder(c, Locale.getDefault());

            try {
                List<Address> addr = g.getFromLocation(lat, lng, 1);
                if (addr != null && !addr.isEmpty())
                    txt = addr.get(0).getAddressLine(0);

            } catch (Exception ignored) {}

            String finalText = txt;

            new Handler(Looper.getMainLooper()).post(() ->
                    cb.onAddressReceived(finalText));
        }).start();
    }

    public interface AddressCallback {
        void onAddressReceived(String address);
    }

    // Animation

    private  void animateView(View view, int animRes){
        view.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this,animRes));
    }
}
