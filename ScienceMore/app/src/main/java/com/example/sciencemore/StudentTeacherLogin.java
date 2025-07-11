package com.example.sciencemore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.Objects;

public class StudentTeacherLogin extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String type;
    String userName;
    String password;

    RadioGroup radioType;
    EditText usernametxt;
    EditText passwordtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_teacher_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //assigning radio group by id
        radioType = findViewById(R.id.idRadioGroup);
        //Set listener on RadioGroup
        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                //RadioButton radioButton = findViewById(checkedId);

                //Set selected text to textView
                if(radioButton != null){
                    type = radioButton.getText().toString();
                }
            }
        });
    }

    public void clickLogin(View v){

        //assign text inputs
        usernametxt = findViewById(R.id.editUserName);
        passwordtxt = findViewById(R.id.editPassword);

        //get values and convert to string
        userName = usernametxt.getText().toString();
        password = passwordtxt.getText().toString();

        try{
            //check user type
            if(Objects.equals(type, "Teacher")) {
                //check the password and username
                CollectionReference collectionReference = db.collection("Teacher");
                Query query = collectionReference.whereEqualTo("teacherName", userName).whereEqualTo("teacherPassword", password);
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // intent for pass teahcer name for other pages
                            Intent intent = new Intent(StudentTeacherLogin.this, StudentAssignmentResults.class);
                            intent.putExtra("teacherName" , userName);


                            Toast.makeText(this, "Login success as a Teacher", Toast.LENGTH_SHORT).show();
                            //open Teacher dashboard
                            // startActivity(new Intent(AdminLogin.this,TeacherDashboard.class));
//                            startActivity(new Intent(StudentTeacherLogin.this, StudentDashboard.class));
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Enter correct username and password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (Objects.equals(type, "Student")) {
                //check the password and username
                CollectionReference collectionReference = db.collection("Student");
                Query query = collectionReference.whereEqualTo("studentName", userName).whereEqualTo("studentPassword", password);
                query.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            // Intent for get student name to other pages
                            Intent intent = new Intent(StudentTeacherLogin.this, StudentAssignmentResults.class);
                            intent.putExtra("studentName" , userName);
                           Toast.makeText(this, "Login success as Student", Toast.LENGTH_SHORT).show();
                            //open Student dashboard
                            startActivity(new Intent(this, StudentAssignmentResults.class));
                            // start intent
                            startActivity(intent);
                        } else {
                           Toast.makeText(this, "Enter correct username and password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "Select the User type", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    public void back(View v){
        startActivity(new Intent(StudentTeacherLogin.this, MainActivity.class));

    }
}