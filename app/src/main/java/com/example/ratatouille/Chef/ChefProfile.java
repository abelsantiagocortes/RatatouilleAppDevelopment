package com.example.ratatouille.Chef;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ratatouille.Class.Solicitud;
import com.example.ratatouille.Class.UserChef;
import com.example.ratatouille.ClientChef.RecipeAgreement;
import com.example.ratatouille.R;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class ChefProfile extends AppCompatActivity {

    TextView name;
    TextView certi;
    TextView exp;
    Button btn_solicitud;
    DatabaseReference dbNotifs;
    UserChef chef;
    FirebaseAuth current;
    CircularImageView ico;
    FirebaseStorage dbRatsStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_profile);

        Intent intent = getIntent();
        String id = intent.getStringExtra("ChefId");

        FirebaseDatabase dbRats = FirebaseDatabase.getInstance();
        dbNotifs = dbRats.getReference("solicitud");

        name=findViewById(R.id.nameChef);
        exp=findViewById(R.id.nameChef2);
        certi = findViewById(R.id.nameChef3);
        btn_solicitud = findViewById(R.id.btn_volver);
        ico =findViewById(R.id.chefin);
        dbRatsStorage = FirebaseStorage.getInstance();


        Query queryChefData = FirebaseDatabase.getInstance().getReference("userChef").orderByChild("userId").equalTo(id);
        queryChefData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dir : dataSnapshot.getChildren()){
                        chef = dir.getValue(UserChef.class);
                        name.setText(chef.getName());
                        exp.setText(chef.getExperiencia()+ " " + chef.getAños() + " años");
                        certi.setText(chef.getCertificados());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_solicitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current = FirebaseAuth.getInstance();
                FirebaseUser currentUser = current.getCurrentUser();
                String userId = currentUser.getUid();
                Solicitud notif = new Solicitud(userId,chef.getUserId());

                String key=dbNotifs.push().getKey();
                dbNotifs.child(key).setValue(notif);

                Intent intent2 = new Intent( getApplicationContext(), RecipeAgreement.class );
                intent2.putExtra("ChefObj", (Serializable) chef);
                startActivity(intent2);
            }
        });

        Query queryChefURL = FirebaseDatabase.getInstance().getReference("userChef").orderByChild("userId").equalTo(id);
        queryChefURL.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot dir : dataSnapshot.getChildren()){
                        Log.i("SP",dir.getValue(UserChef.class).getPhotoDownloadURL());
                        cargarImagen(dir, dbRatsStorage);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("LOGINFAILED","CHEF" );
            }
        });





    }

    private Bitmap cargarImagen(DataSnapshot dir, FirebaseStorage dbRatsStorage) {
        final Bitmap[] bitmap = {null};
        StorageReference sRf = dbRatsStorage.getReferenceFromUrl(dir.getValue(UserChef.class).getPhotoDownloadURL());
        try {
            final File localFile = File.createTempFile("images", "jpg");
            sRf.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmap[0] = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ico.setImageBitmap(bitmap[0]);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e ) {
            e.printStackTrace();
        }
        return bitmap[0];
    }
}
