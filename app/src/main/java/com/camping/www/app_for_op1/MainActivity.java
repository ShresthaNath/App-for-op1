package com.camping.www.app_for_op1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {


EditText Name,Vehicle,Items,check_in,check_out,txtname;
Datas datas;
Button button,button2,ch,up;
ImageView img;
StorageReference nStorageRef;
DatabaseReference dbreff;
private StorageTask uploadTask;
public Uri imguri;
DatabaseReference reff;
Member member;
long maxid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nStorageRef= FirebaseStorage.getInstance().getReference("Images");
        dbreff=FirebaseDatabase.getInstance().getReference().child("Datas");


        ch=(Button)findViewById(R.id.btnchoose);
        up=(Button)findViewById(R.id.btnupload);
        img=(ImageView)findViewById(R.id.imgview);
        txtname=(EditText)findViewById(R.id.picname);
        datas=new Datas();
        ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Filechooser();
            }
        });
        up.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(uploadTask !=null && uploadTask.isInProgress())
                {
                    Toast.makeText(MainActivity.this,"Upload in progress",Toast.LENGTH_LONG).show();
                }else {

                    Fileuploader();
                }
            }
        });

        Name=(EditText)findViewById(R.id.Name);
        Vehicle=(EditText)findViewById(R.id.Vehicle);
        Items=(EditText)findViewById(R.id.Items);
        check_in=(EditText)findViewById(R.id.check_in);
        check_out= (EditText)findViewById(R.id.check_out);
        button=(Button)findViewById(R.id.button);
        button2=(Button)findViewById(R.id.button2);
        member=new Member();
        reff= FirebaseDatabase.getInstance().getReference().child("Member");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                     maxid = (dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check_in1=Integer.parseInt(check_in.getText().toString().trim());
                int check_out1=Integer.parseInt(check_out.getText().toString().trim());
                member.setName1(Name.getText().toString().trim());
                member.setVehicle1(Vehicle.getText().toString().trim());
                member.setItems1(Items.getText().toString().trim());
                member.setCheck_in1(check_in1);
                member.setCheck_out1(check_out1);
                reff.child(String.valueOf(maxid+1)).setValue(member);

                Toast.makeText(MainActivity.this,"data inserted successfully",Toast.LENGTH_LONG).show();





            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff=FirebaseDatabase.getInstance().getReference().child("Member").child("1");
                reff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String Name2=dataSnapshot.child("Name").getValue().toString();
                        String Vehicle2=dataSnapshot.child("Vehicle").getValue().toString();
                        String check_in2=dataSnapshot.child("Check_in").getValue().toString();
                        String check_out2=dataSnapshot.child("check_out").getValue().toString();
                        String Items2=dataSnapshot.child("items").getValue().toString();
                        Name.setText(Name2);
                        Vehicle.setText(Vehicle2);
                        Items.setText(Items2);
                        check_in.setText(check_in2);
                        check_out.setText(check_out2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private String getExtension(Uri uri)
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    private void Fileuploader()
    {
        String imageid;
        imageid=System.currentTimeMillis()+"."+getExtension(imguri);
        datas.setPicname(txtname.getText().toString().trim());
        dbreff.push().setValue(datas);
        StorageReference Ref=nStorageRef.child(imageid);



        uploadTask=Ref.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(MainActivity.this,"Image Uploaded successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }
    private void Filechooser()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imguri=data.getData();
            img.setImageURI(imguri);
        }
    }
}
