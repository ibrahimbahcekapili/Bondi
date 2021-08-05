package com.tccbiko.bondi.start;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.databinding.ActivityProfileScreen2Binding;
import com.tccbiko.bondi.user.Users;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ProfileScreen2 extends AppCompatActivity {

    private static final String TAG = "ProfileScreen2";
    private ActivityProfileScreen2Binding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private BottomSheetDialog bottomSheetDialog;
    private int IMAGE_GALLERY_REQUEST=111;
    private Uri imageURi;
    private String profilephoto;
    private String username;
    private String bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_profile_screen2);

binding.tamamla.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        getInformation();
        startActivity(new Intent(getApplicationContext(), StartScreen.class));
        finish();
    }
});

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firestore=FirebaseFirestore.getInstance();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        db.collection("Users").document(firebaseUser.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().getString("bio").equals("")) {
                            bio=task.getResult().getString("bio");
                                binding.bio.setText(task.getResult().getString("bio"));
                            }else{
                                bio= binding.bio.getText().toString();
                            }
                           username= task.getResult().getString("userName");
                           if(task.getResult().getString("profilePhoto")!=null || !task.getResult().getString("profilePhoto").equals("")){
                                profilephoto = task.getResult().getString("profilePhoto");
                                Glide.with(ProfileScreen2.this).load(task.getResult().getString("profilePhoto")).into(binding.imageProfile);
                            }else {
                               profilephoto="https://www.pasrc.org/sites/g/files/toruqf431/files/styles/freeform_750w/public/2021-03/blank-profile-picture-973460_1280.jpg?itok=QzRqRVu8";

                           }

                            binding.hello.setText("Merhaba "+username+"!");

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        if(firebaseUser!=null){

            //getInformation();
        }
        pictureClick();
    }

    private void pictureClick() {
        binding.fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showButtonSheetPickphoto();
            }
        });
    }

    private void showButtonSheetPickphoto() {
        View view= getLayoutInflater().inflate(R.layout.picture_button,null);

        ((View) view.findViewById(R.id.ln_gallery)).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @Override
            public void onClick(View v) {
                openGallery();
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog=new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog=null;
            }
        });
        bottomSheetDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void openGallery() {

        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Foto"),IMAGE_GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMAGE_GALLERY_REQUEST
                && resultCode == RESULT_OK
                && data !=null
                && data.getData()!=null){
            imageURi=data.getData();
            uploadtofirebase();

            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageURi);
                binding.imageProfile.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadtofirebase() {
        if (imageURi != null) {

            StorageReference mountainsRef = FirebaseStorage.getInstance().getReference().child("ProfilImages/" + FirebaseAuth.getInstance().getUid() + "." + getFileExtention(imageURi));
            mountainsRef.putFile(imageURi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    final String sdownload_url = String.valueOf(downloadUrl);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("profilePhoto", sdownload_url);

                    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(),"Fotoğraf yüklendi",Toast.LENGTH_SHORT).show();
                                Glide.with(ProfileScreen2.this).load(sdownload_url).into(binding.imageProfile);
                                profilephoto=sdownload_url;
                                   // getInformation();
                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });

        }

    }


    private void getInformation() {

        FirebaseFirestore FirebaseFirestore = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        FirebaseUser FirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (FirebaseUser!=null) {
            String UserID = FirebaseUser.getUid();
            Users users = new Users(UserID,
                    username,
                    FirebaseUser.getPhoneNumber(),profilephoto
                    ,
                    "",
                    binding.bio.getText().toString());
            FirebaseFirestore.collection("Users").document(FirebaseUser.getUid()).set(users)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        public void onSuccess(Void aVoid) {

                            //Toast.makeText(getApplicationContext(), "Kayıt Tamamlandı", Toast.LENGTH_LONG).show();

                        }
                    });
        }else {
            Toast.makeText(getApplicationContext(),"Bir hata oluştu",Toast.LENGTH_LONG).show();


        }

    }

    }
