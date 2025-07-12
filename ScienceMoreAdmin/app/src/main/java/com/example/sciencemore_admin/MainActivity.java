package com.example.sciencemore_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void enterLogin(View v){
        //go to login page
        startActivity(new Intent(MainActivity.this, AdminLogin.class));
    }

    public void enterRegister(View v){

        // check whether an admin already registered
        CollectionReference collRef = db.collection("Admin");
        Query query = collRef.whereEqualTo("adminUserName","");
        query.get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){

               QuerySnapshot querySnapshot = task.getResult();
               if (querySnapshot != null && !querySnapshot.isEmpty()) {
                   //go to register page
                   startActivity(new Intent(MainActivity.this, AdminRegister.class));

               }else {
                   Toast.makeText(this, "Already registered", Toast.LENGTH_SHORT).show();
                  // startActivity(new Intent(MainActivity.this, AdminRegister.class));
               }

           }else {
               Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
           }

        });

    }
}