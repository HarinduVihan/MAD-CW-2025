package com.example.sciencemore_admin;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminRegister extends AppCompatActivity {

    //access a cloud firestore instance from activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userName;
    String password;
    String confirmPassword;

    EditText userNametxt;
    EditText passwordtxt;
    EditText confirmPasswordtxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    public void back(View v){
        startActivity(new Intent(AdminRegister.this, MainActivity.class));

    }

    public void clickRegister(View v){
        try{
            //assigning input by id
            userNametxt = findViewById(R.id.editUserName);
            passwordtxt = findViewById(R.id.editPassword);
            confirmPasswordtxt = findViewById(R.id.editConfirmPassword);

            //get values from input
            userName = userNametxt.getText().toString();
            password = passwordtxt.getText().toString();
            confirmPassword = confirmPasswordtxt.getText().toString();
            //check whether the passwords a equals
            if(Objects.equals(password, confirmPassword)){

                //put data to a hashmap
                Map<String, Object> admin = new HashMap<>();
                admin.put("adminUserName",userName);
                admin.put("adminPassword",password);

                //enter admin data in to firestore
                db.collection("Admin")
                        .add(admin)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                Toast.makeText(AdminRegister.this, "Registered success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AdminRegister.this, MainActivity.class));
                            }
                        })
//                    .addOnSuccessListener(documentReference -> {
//                        //Success callback
//                        System.out.println("Document added with ID: " + documentReference.getId());
//                        Log.d("success", "Document added");
//                        Toast.makeText(this, "Registered success", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(AdminRegister.this, MainActivity.class));
//                    })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("failure", "Error writing document", e);
                                Toast.makeText(AdminRegister.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else{
                Toast.makeText(this, "Unmatched passwords", Toast.LENGTH_SHORT).show();
                Log.d("passwordCheck", "Unmatched passwords");
            }
        } catch (Exception e) {
           // throw new RuntimeException(e);
            Log.d("catch", "Error writing document", e);
        }

    }
}