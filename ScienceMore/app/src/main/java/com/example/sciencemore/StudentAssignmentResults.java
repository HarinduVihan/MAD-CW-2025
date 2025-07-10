package com.example.sciencemore;

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

public class StudentAssignmentResults extends AppCompatActivity {

    private LinearLayout cardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assignment_results);
        cardContainer = findViewById(R.id.cardContainer);

        List<AssignmentResults> results = new ArrayList<>();
        results.add(new AssignmentResults("Science", "firs assignment" , "100"));
        results.add(new AssignmentResults("Science", "second assignment" , "90"));
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

            // IMPORTANT: Find the inner LinearLayout first
            LinearLayout cardContentLayout = cardViewLayout.findViewById(R.id.cardContentLayout);

            // Now, find the TextViews within that inner LinearLayout
            TextView subject  = cardContentLayout.findViewById(R.id.txtsubject);
            TextView descriptionTextView = cardContentLayout.findViewById(R.id.txtdescription);
            TextView marks = cardContentLayout.findViewById(R.id.txtmark);

            //populate the views with data
            subject.setText(currentItem.getSubject());
            descriptionTextView.setText(currentItem.getDescription());
            marks.setText(currentItem.getMarks() + "/100"); // Assuming getMarks() returns "100" or "100%"

            // You had 'final int cardIndex = i;' which wasn't used, safe to remove if not needed.

            cardContainer.addView(cardViewLayout);
        }
    }
}