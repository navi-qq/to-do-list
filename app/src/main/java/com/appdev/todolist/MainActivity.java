package com.appdev.todolist;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> tasks = new ArrayList<>();
    TaskStatusHandler taskStatusHandler;
    RemoveTaskHandler removeTaskHandler;
    EditTaskHandler editTaskHandler;
    ImageView addTaskBtn;
    LinearLayout taskContainer;
    LinearLayout container;
    CheckBox checkbox;
    EditText editText;
    ImageButton editButton;
    ImageButton deleteButton;
    LayoutParams containerParams;
    LayoutParams iconParams;
    MarginLayoutParams editTextParams;

    Dialog editTaskDialog;
    Button saveTaskButton;
    TextView editTaskDialogTitle;
    EditText taskDescription;

    int centerPosition = Gravity.CENTER;
    int iconSize;
    int editTextSize;
    int pencilIconMargin;

    int editedTaskIndex;
    String updatedTaskDescription;


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

        Dialog formDialog = new Dialog(MainActivity.this);
        formDialog.setContentView(R.layout.form_dialog);
        formDialog.setCancelable(false);
        formDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        editTaskDialog = new Dialog(MainActivity.this);
        editTaskDialog.setContentView(R.layout.edit_task_dialog);
        editTaskDialog.setCancelable(false);
        editTaskDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Button addBtnDialog = formDialog.findViewById(R.id.addBtnDialog);
        Button cancelBtnDialog = formDialog.findViewById(R.id.cancelTaskButton);
        EditText inputFieldDialog = formDialog.findViewById(R.id.inputFieldDialog);

        saveTaskButton = editTaskDialog.findViewById(R.id.saveTaskButton);
        Button cancelTaskButton = editTaskDialog.findViewById(R.id.cancelTaskButton);
        editTaskDialogTitle = editTaskDialog.findViewById(R.id.editTaskDialogTitle);
        taskDescription = editTaskDialog.findViewById(R.id.taskDescription);

        addBtnDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tasks.add(inputFieldDialog.getText().toString());
                inputFieldDialog.setText("");
                render();
                formDialog.dismiss();
            }
        });

        cancelBtnDialog.setOnClickListener(v -> formDialog.dismiss());
        cancelTaskButton.setOnClickListener(v -> editTaskDialog.dismiss());

        addTaskBtn = findViewById(R.id.addTaskBtn);
        taskContainer = findViewById(R.id.taskContainer);

        taskStatusHandler = new TaskStatusHandler();
        removeTaskHandler = new RemoveTaskHandler();
        editTaskHandler = new EditTaskHandler();

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
                formDialog.show();
            }
        });

        saveTaskButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                updatedTaskDescription = taskDescription.getText().toString();
                tasks.set(editedTaskIndex, updatedTaskDescription);
                render();
                editTaskDialog.dismiss();
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
            editButton = new ImageButton(getApplicationContext());
            deleteButton = new ImageButton(getApplicationContext());

            container.setLayoutParams(containerParams);
            container.setId(tasks.indexOf(task));

            editText.setText(task, TextView.BufferType.SPANNABLE);
            editText.setLayoutParams(editTextParams);

            editButton.setBackgroundResource(R.drawable.pencil_icon);
            editButton.setScaleType(ImageView.ScaleType.CENTER);
            editButton.setLayoutParams(iconParams);

            deleteButton.setBackgroundResource(R.drawable.trash_icon);
            deleteButton.setLayoutParams(iconParams);

            container.addView(checkbox);
            container.addView(editText);
            container.addView(editButton);
            container.addView(deleteButton);

            checkbox.setOnCheckedChangeListener(taskStatusHandler);
            deleteButton.setOnClickListener(removeTaskHandler);
            editButton.setOnClickListener(editTaskHandler);

            taskContainer.addView(container);
        });
    }

     class TaskStatusHandler implements CompoundButton.OnCheckedChangeListener {

        final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int parentId = ((View) buttonView.getParent()).getId();
            ViewGroup taskSelected = (ViewGroup) taskContainer.getChildAt(parentId);
            EditText text = (EditText) taskSelected.getChildAt(1);

            Spannable spannable = text.getText();
            int length = text.getText().length();

            if (isChecked) {
                text.setTextColor(Color.GRAY);
                spannable.setSpan(STRIKE_THROUGH_SPAN, 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return;
            }

            spannable.removeSpan(STRIKE_THROUGH_SPAN);
            text.setTextColor(Color.BLACK);
        }

    }
    class RemoveTaskHandler implements OnClickListener {

        @Override
        public void onClick(View v) {
            int parentId = ((View) v.getParent()).getId();
            tasks.remove(parentId);
            render();
        }
    }


    class EditTaskHandler implements OnClickListener {

        @Override
        public void onClick(View v) {
            int parentId = ((View) v.getParent()).getId();
            editTaskDialogTitle.setText(String.format("Task %s", parentId + 1));
            taskDescription.setText(tasks.get(parentId));
            editedTaskIndex = parentId;
            editTaskDialog.show();
        }
    }
}