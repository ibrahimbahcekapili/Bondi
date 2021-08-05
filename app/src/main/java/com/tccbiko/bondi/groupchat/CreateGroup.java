package com.tccbiko.bondi.groupchat;

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
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tccbiko.bondi.R;
import com.tccbiko.bondi.databinding.ActivityCreateGroupBinding;
import com.tccbiko.bondi.user.Users;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateGroup extends AppCompatActivity {


    private ActivityCreateGroupBinding binding;
    private List<Users> list = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;
    private int IMAGE_GALLERY_REQUEST=111;
    private Uri imageURi;
    private String profilephoto;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_create_group);
        Intent intent = getIntent();
        key = intent.getStringExtra("groupIDD");
        binding.tamamla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.bio.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Lütfen bir grup adı girin", Toast.LENGTH_LONG).show();}
                else {
                    setInformation();
                    startActivity(new Intent(CreateGroup.this, GroupChatScreen.class)
                            .putExtra( "groupID",key)
                            .putExtra("groupname",binding.bio.getText().toString())
                            .putExtra("photo",profilephoto));
                    finish();
                }

            }
        });
if(profilephoto==null){
    profilephoto="https://www.kindpng.com/picc/m/154-1546024_ehr-my-team-team-png-icon-transparent-png.png";
}
        pictureClick();
    }
    private void setInformation() {




        DatabaseReference chatInfoDb1 = FirebaseDatabase.getInstance().getReference().child("GroupIDs").child(key);


        HashMap newChatMap = new HashMap();
        newChatMap.put("groupname",binding.bio.getText().toString());
        newChatMap.put("photo",profilephoto);

        chatInfoDb1.updateChildren(newChatMap);

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

            StorageReference mountainsRef = FirebaseStorage.getInstance().getReference().child("ProfilImages/" + key+ "." + getFileExtention(imageURi));
            mountainsRef.putFile(imageURi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();

                    final String sdownload_url = String.valueOf(downloadUrl);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("profilePhoto", sdownload_url);

                   profilephoto=sdownload_url;

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });

        }

    }




}