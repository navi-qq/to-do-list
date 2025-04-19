package com.appdev.todolist;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    LinearLayout container;
    CheckBox checkbox;
    EditText editText;
    ImageView pencilIcon;
    ImageView deleteIcon;


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


        int iconDp = 22;
        int editTextDp = 250;
        int iconSize = dpConverter(iconDp);
        int editTextSize = dpConverter(editTextDp);

        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.HORIZONTAL);
        MarginLayoutParams pencilIconParams = new MarginLayoutParams(iconSize, iconSize);
        MarginLayoutParams deleteIconParams = new MarginLayoutParams(iconSize,iconSize);
        MarginLayoutParams editTextParams = new MarginLayoutParams(editTextSize, LinearLayout.LayoutParams.WRAP_CONTENT);

        containerParams.gravity = Gravity.CENTER;

        pencilIconParams.setMargins(8,0,8,0);

        ImageView addTaskBtn = findViewById(R.id.addTaskBtn);
        LinearLayout taskContainer = findViewById(R.id.taskContainer);



        addTaskBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                container = new LinearLayout(getApplicationContext());
                checkbox = new CheckBox(getApplicationContext());
                editText = new EditText(getApplicationContext());
                pencilIcon = new ImageView(getApplicationContext());
                deleteIcon = new ImageView(getApplicationContext());

                container.setLayoutParams(containerParams);

                editText.setHint("Untitled Task");
                editText.setLayoutParams(editTextParams);

                pencilIcon.setImageResource(R.drawable.pencil_icon);
                pencilIcon.setLayoutParams(pencilIconParams);

                deleteIcon.setImageResource(R.drawable.trash_icon);
                deleteIcon.setLayoutParams(deleteIconParams);

                container.addView(checkbox);
                container.addView(editText);
                container.addView(pencilIcon);
                container.addView(deleteIcon);

                taskContainer.addView(container);

            }
        });
    }

    public int dpConverter(int dps) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
}