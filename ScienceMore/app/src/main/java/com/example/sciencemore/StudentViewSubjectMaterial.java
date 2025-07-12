package com.example.sciencemore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class StudentViewSubjectMaterial extends AppCompatActivity {
    private LinearLayout cardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_view_subject_material);
        cardContainer = findViewById(R.id.cardContainer);

        List<CardItem> cardDataList = new ArrayList<>();
        cardDataList.add(new CardItem("Card One", "This is the description for card number one."));
        cardDataList.add(new CardItem("Card Two", "Here's some content for the second card."));

        populateCardViews(cardDataList);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_student_view_subject_material);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void populateCardViews(List<CardItem> dataList) {
        // Clear any existing views
        cardContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < dataList.size(); i++) {  //for each item in data list, a new card will be added to the ui
            CardItem currentItem = dataList.get(i);

            // Inflate the card_item.xml layout
            View cardViewLayout = inflater.inflate(R.layout.card_item, cardContainer, false);


            TextView titleTextView = cardViewLayout.findViewById(R.id.titleTextView);
            TextView descriptionTextView = cardViewLayout.findViewById(R.id.descriptionTextView);
            Button actionButton = cardViewLayout.findViewById(R.id.actionButton);

            // Populate the views with data
            titleTextView.setText("File " + (i + 1));
            descriptionTextView.setText(currentItem.getDescription());
            actionButton.setText("Download");

            // Set a click listener for the button (optional)
            final int cardIndex = i;
            actionButton.setOnClickListener(new View.OnClickListener() {  //below code is just used to test the functioning of button
                @Override
                public void onClick(View v) {
                    Toast.makeText(StudentViewSubjectMaterial.this,
                            "Button clicked for: " + currentItem.getLink(),
                            Toast.LENGTH_SHORT).show();
                }
            });

            // Add the inflated CardView to the container
            cardContainer.addView(cardViewLayout);
        }
    }
}