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

public class TeacherMarkAssignment extends AppCompatActivity {

    private LinearLayout cardContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_mark_assignment);
        cardContainer = findViewById(R.id.cardContainer);

        List<Assigments> assigmentsList = new ArrayList<>();
        assigmentsList.add(new Assigments("http", "Dinidu Dididy"));
        populateCardViews(assigmentsList);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void populateCardViews(List<Assigments> assigments){
        cardContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i =0; i < assigments.size(); i++){
            Assigments currentItem = assigments.get(i);

            View cardViewLayout = inflater.inflate(R.layout.mark_assignment_card,cardContainer, false);

            LinearLayout cardContentLayout = cardViewLayout.findViewById(R.id.cardContentLayout);

            TextView name = cardContentLayout.findViewById(R.id.txtStudentName);
            Button downloadbtn = cardViewLayout.findViewById(R.id.btnDownloadAnswerSheet);

            name.setText(currentItem.getName());

            final int cardIndex = i;
            downloadbtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Toast.makeText(TeacherMarkAssignment.this, "download link" + currentItem.getLink(), Toast.LENGTH_SHORT).show();
                }
            });

            cardContainer.addView(cardViewLayout);
        }
    }
}