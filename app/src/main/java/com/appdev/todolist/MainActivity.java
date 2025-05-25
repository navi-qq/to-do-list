package com.appdev.todolist;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.util.Log;
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
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    List<String> tasks = new ArrayList<>();
    List<Task> tasks = new ArrayList<>();
    SharedPreferences taskDataPreferences;
    SharedPreferences taskHistoryDataPreferences;
    Gson gson = new Gson();
    String json;
    TaskStatusHandler taskStatusHandler;
    RemoveTaskHandler removeTaskHandler;
    EditTaskHandler editTaskHandler;
    ImageView addTaskBtn;
    LinearLayout taskContainerFirst;
    LinearLayout taskContainerSecond;
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

    Spannable spannable;
    int centerPosition = Gravity.CENTER;
    int iconSize;
    int editTextSize;
    int pencilIconMargin;

    int editedTaskIndex;
    String updatedTaskDescription;
    final StrikethroughSpan STRIKE_THROUGH_SPAN = new StrikethroughSpan();

    DateFormat df;

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

        taskDataPreferences = getApplicationContext().getSharedPreferences("taskDataPref", MODE_PRIVATE);
        taskHistoryDataPreferences = getApplicationContext().getSharedPreferences("taskHistoryDataPref", MODE_PRIVATE);

        loadTaskData();
        initialize();
        render();
        df = new SimpleDateFormat("h:mm a");
    }

    public void initialize() {
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
                if (inputFieldDialog.getText().toString().isEmpty()) return;

                tasks.add(new Task(inputFieldDialog.getText().toString(), false, null));
                inputFieldDialog.setText("");
                saveTaskData();
                loadTaskData();
                render();
                formDialog.dismiss();
            }
        });

        cancelBtnDialog.setOnClickListener(v -> formDialog.dismiss());
        cancelTaskButton.setOnClickListener(v -> editTaskDialog.dismiss());

        addTaskBtn = findViewById(R.id.addTaskButton);
        ImageView taskRecordButton = findViewById(R.id.taskRecordButton);

        ScrollView taskFirstScrollViewContainer = findViewById(R.id.taskFirstScrollViewContainer);
        ScrollView taskSecondScrollViewContainer = findViewById(R.id.taskSecondScrollViewContainer);

        LinearLayout collapseTitleFirst = findViewById(R.id.collapseTitleFirst);
        LinearLayout collapseTitleSecond = findViewById(R.id.collapseTitleSecond);

        ImageView collapseArrowIconFirst = findViewById(R.id.collapseArrowIconFirst);
        ImageView collapseArrowIconSecond = findViewById(R.id.collapseArrowIconSecond);

        taskContainerFirst = findViewById(R.id.taskContainerFirst);
        taskContainerSecond = findViewById(R.id.taskContainerSecond);

        CollapseListHandler collapseListHandlerFirst = new CollapseListHandler(taskFirstScrollViewContainer,
                collapseArrowIconFirst);

        CollapseListHandler collapseListHandlerSecond = new CollapseListHandler(taskSecondScrollViewContainer,
                collapseArrowIconSecond);



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
                tasks.get(editedTaskIndex).setNewTaskDescription(updatedTaskDescription);
                saveTaskData();
                loadTaskData();
                render();
                editTaskDialog.dismiss();
            }
        });

        taskRecordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent taskRecords = new Intent(MainActivity.this, TaskRecord.class);
                MainActivity.this.startActivity(taskRecords);
            }
        });

        collapseTitleFirst.setOnClickListener(collapseListHandlerFirst);
        collapseTitleSecond.setOnClickListener(collapseListHandlerSecond);
    }

    public int dpConverter(int dps) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public void render() {
        taskContainerFirst.removeAllViews();
        taskContainerSecond.removeAllViews();
        tasks.forEach(task -> {
            container = new LinearLayout(getApplicationContext());
            checkbox = new CheckBox(getApplicationContext());
            editText = new EditText(getApplicationContext());
            editButton = new ImageButton(getApplicationContext());
            deleteButton = new ImageButton(getApplicationContext());

            container.setLayoutParams(containerParams);
            container.setId(tasks.indexOf(task));

            checkbox.setChecked(task.taskStatus);

            editText.setText(task.taskDescription, TextView.BufferType.SPANNABLE);
            editText.setLayoutParams(editTextParams);
            editText.setFocusable(false);
            editText.setClickable(false);

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

            if (task.taskStatus) {
                spannable = editText.getText();
                editText.setTextColor(Color.GRAY);
                spannable.setSpan(STRIKE_THROUGH_SPAN, 0, task.taskDescription.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                taskContainerSecond.addView(container);
                return;
            }

            taskContainerFirst.addView(container);
        });
    }

    public void saveTaskData() {
        SharedPreferences.Editor taskDataEditor = taskDataPreferences.edit();
        json = gson.toJson(tasks);
        taskDataEditor.putString("taskData", json);
        taskDataEditor.apply();


        String date = df.format(Calendar.getInstance().getTime());
        if (date.equals("11:59 PM")) {
            SharedPreferences.Editor taskHistoryDataEditor = taskHistoryDataPreferences.edit();
            taskHistoryDataEditor.putString("taskHistoryData", json);
            taskHistoryDataEditor.apply();
        }
    }

    public void loadTaskData() {
        json = taskDataPreferences.getString("taskData", null);
        Type listType = new TypeToken<ArrayList<Task>>(){}.getType();
        tasks = gson.fromJson(json, listType);
    }

     class TaskStatusHandler implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int parentId = ((View) buttonView.getParent()).getId();
            
            if (isChecked) {
                tasks.get(parentId).setTaskStatus(isChecked);
                saveTaskData();
                loadTaskData();
                render();
                return;
            }

            tasks.get(parentId).setTaskStatus(isChecked);
            saveTaskData();
            loadTaskData();
            render();
        }

    }
    class RemoveTaskHandler implements OnClickListener {

        @Override
        public void onClick(View v) {
            int parentId = ((View) v.getParent()).getId();
            tasks.remove(parentId);
            saveTaskData();
            loadTaskData();
            render();
        }
    }


    class EditTaskHandler implements OnClickListener {

        @Override
        public void onClick(View v) {
            int parentId = ((View) v.getParent()).getId();
            editTaskDialogTitle.setText(String.format("Task %s", parentId + 1));
            taskDescription.setText(tasks.get(parentId).taskDescription);
            editedTaskIndex = parentId;
            editTaskDialog.show();
        }
    }

    class CollapseListHandler implements OnClickListener {

        ScrollView scrollViewContainer;
        ImageView icon;

        CollapseListHandler(ScrollView scrollViewContainer,
                            ImageView icon) {
            this.scrollViewContainer = scrollViewContainer;
            this.icon = icon;
        }

        @Override
        public void onClick(View v) {

            if (scrollViewContainer.getVisibility() == View.VISIBLE) {
                scrollViewContainer.setVisibility(View.INVISIBLE);
                icon.setRotation(0);
                return;
            }

            scrollViewContainer.setVisibility(View.VISIBLE);
            icon.setRotation(-270);
        }
    }
}