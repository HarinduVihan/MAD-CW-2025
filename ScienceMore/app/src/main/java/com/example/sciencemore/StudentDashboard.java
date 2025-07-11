package com.example.sciencemore;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboard extends AppCompatActivity {

    private LinearLayout cardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_dashboard);

        cardContainer = findViewById(R.id.cardContainer);

        List<CardItem> cardDataList = new ArrayList<>();
        cardDataList.add(new CardItem("Subject one","Science More"));
        cardDataList.add(new CardItem("Subject two","Maths less"));

        //call populateCardViews function
        populateCardViews(cardDataList);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void populateCardViews(List<CardItem> dataList){
        //clear any existing views
        cardContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        //get the data from list and add a card view
        for(int i = 0; i < dataList.size(); i++){
            CardItem currentItem = dataList.get(i);

            //Inflate the subject card
            View cardViewLayout = inflater.inflate(R.layout.student_dashboard_subject_card,cardContainer,false);

            TextView subject = cardViewLayout.findViewById(R.id.subjecttxt);

            //populate the views with data
            subject.setText(currentItem.getDescription());

            //Add the inflated cardview to the container
            cardContainer.addView(cardViewLayout);
        }
    }
    public void back(View v){
        startActivity(new Intent(StudentDashboard.this, StudentTeacherLogin.class));
    }
}