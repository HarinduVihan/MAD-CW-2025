package com.example.sciencemore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudentAssignmentResults extends AppCompatActivity {

    private LinearLayout cardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignment_results);
        cardContainer = findViewById(R.id.cardContainer);

        List<AssignmentResults> results = new ArrayList<>();
        results.add(new AssignmentResults("Science", "firs assignment" , "100"));
        populateCardViews(results);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void populateCardViews(List<AssignmentResults> results){
        // Clear any existing views
        cardContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < results.size(); i++){
            AssignmentResults currentItem = results.get(i);

            View cardViewLayout = inflater.inflate(R.layout.result_card,cardContainer, false);

            TextView subject  = cardViewLayout.findViewById(R.id.txtsubject);
            TextView descriptionTextView = cardViewLayout.findViewById(R.id.txtdescription);
            TextView marks = cardViewLayout.findViewById(R.id.txtmark);

            //populate the views with data
            subject.setText(currentItem.getSubject());
            descriptionTextView.setText(currentItem.getDescription());
            marks.setText(currentItem.getMarks() + "100%");

            cardContainer.addView(cardViewLayout);


        }
    }
}