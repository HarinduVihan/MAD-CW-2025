package com.example.sciencemore_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminLogin extends AppCompatActivity {

    //get firestore instance
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String userName;
    String password;

    boolean usernameFound;

    EditText usernametxt;
    EditText passwordtxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void back(View v){
        startActivity(new Intent(AdminLogin.this, MainActivity.class));

    }

    public void clickLogin(View v){

        //assign text inputs
        usernametxt = findViewById(R.id.editUserName);
        passwordtxt = findViewById(R.id.editPassword);

        //get values and convert to string
        userName = usernametxt.getText().toString();
        password = passwordtxt.getText().toString();

        try{
            //check the password and username
            CollectionReference collectionReference = db.collection("Admin");
            Query query = collectionReference.whereEqualTo("adminUserName",userName).whereEqualTo("adminPassword",password);
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){

                    QuerySnapshot querySnapshot = task.getResult();
                    if(querySnapshot != null && !querySnapshot.isEmpty()){

                        usernameFound = true;
                        Toast.makeText(this, "Login success", Toast.LENGTH_SHORT).show();
                        //open admin dashboard
                       // startActivity(new Intent(AdminLogin.this,AdminDashboard.class));
                    }else {
                        Toast.makeText(this, "Enter correct username and password", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}