package com.example.ratatouille.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ratatouille.Chef.ChefProfile;
import com.example.ratatouille.Class.UserChef;
import com.example.ratatouille.ClientChef.AgreementClass;
import com.example.ratatouille.ClientChef.ClientChefDistance;
import com.example.ratatouille.R;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class ChefsAdapter  extends RecyclerView.Adapter<ChefsAdapter.MyViewHolder> {
    private List<ClientChefDistance> clientChefDistances;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        CircularImageView chefPhoto;
        TextView distance;
        TextView chefName;
        Button btnProfile;

        public MyViewHolder(LinearLayout v) {
            super(v);

            chefPhoto =  v.findViewById(R.id.imgChef);
            distance =  v.findViewById(R.id.txt_distance);
            chefName =  v.findViewById(R.id.txt_nameChef);
            chefName.setMovementMethod(new ScrollingMovementMethod());
            btnProfile =v.findViewById(R.id.buttonPerfil);


        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChefsAdapter(List<ClientChefDistance> imgs) {
        this.clientChefDistances = imgs;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChefsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,  int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chefdistance, parent, false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final int p = position;
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
       // holder.chefPhoto.setImageBitmap(clientChefDistances.get(position).getImgChef());
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        storageRef.child("images/userChef/" + clientChefDistances.get(position).getIdChef()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.chefPhoto.setImageBitmap(bMap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("No se pudo");
            }
        });

        holder.chefName.setText(clientChefDistances.get(position).getChefName());
        holder.distance.setText(String.valueOf(clientChefDistances.get(position).getDistance())+" km");

        holder.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent( view.getContext(), ChefProfile.class );
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent2.putExtra("ChefId",  clientChefDistances.get(p).getIdChef());
                view.getContext().startActivity(intent2);
            }
        });



    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return clientChefDistances.size();
    }
}