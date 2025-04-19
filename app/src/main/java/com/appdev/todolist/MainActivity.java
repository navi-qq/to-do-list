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
import android.widget.LinearLayout.LayoutParams;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> tasks = new ArrayList<>();
    ImageView addTaskBtn;
    LinearLayout taskContainer;
    LinearLayout container;
    CheckBox checkbox;
    EditText editText;
    ImageView pencilIcon;
    ImageView deleteIcon;
    LayoutParams containerParams;
    LayoutParams iconParams;
    MarginLayoutParams editTextParams;

    int centerPosition = Gravity.CENTER;
    int iconSize;
    int editTextSize;
    int pencilIconMargin;


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

        addTaskBtn = findViewById(R.id.addTaskBtn);
        taskContainer = findViewById(R.id.taskContainer);

        iconSize = dpConverter(22);
        editTextSize = dpConverter(250);
        pencilIconMargin = dpConverter(8);

        containerParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.HORIZONTAL);
        iconParams = new LayoutParams(iconSize, iconSize);
        editTextParams = new MarginLayoutParams(editTextSize, LinearLayout.LayoutParams.WRAP_CONTENT);

        iconParams.gravity = centerPosition;
        containerParams.gravity = centerPosition;
        iconParams.setMargins(pencilIconMargin,0, pencilIconMargin,0);

        addTaskBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                render();
            }
        });
    }

    public int dpConverter(int dps) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public void render() {
        taskContainer.removeAllViews();
        tasks.forEach(task -> {
            container = new LinearLayout(getApplicationContext());
            checkbox = new CheckBox(getApplicationContext());
            editText = new EditText(getApplicationContext());
            pencilIcon = new ImageView(getApplicationContext());
            deleteIcon = new ImageView(getApplicationContext());

            container.setLayoutParams(containerParams);

            editText.setText(task);
            editText.setLayoutParams(editTextParams);

            pencilIcon.setImageResource(R.drawable.pencil_icon);
            pencilIcon.setLayoutParams(iconParams);

            deleteIcon.setImageResource(R.drawable.trash_icon);
            deleteIcon.setLayoutParams(iconParams);

            container.addView(checkbox);
            container.addView(editText);
            container.addView(pencilIcon);
            container.addView(deleteIcon);

            taskContainer.addView(container);
        });
    }
}