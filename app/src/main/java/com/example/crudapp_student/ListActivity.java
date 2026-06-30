package com.example.crudapp_student;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final ListView listView = findViewById(R.id.listView);

        DatabaseHelper.getAllStudents(students -> {
            runOnUiThread(() -> {
                if (students == null) {
                    Toast.makeText(ListActivity.this, "DB接続失敗", Toast.LENGTH_LONG).show();
                    return;
                }
                final List<String> displayList = new ArrayList<>();
                for (Student s : students) {
                    displayList.add("ID: " + s.getId() + ", 名前: " + s.getName() + ", 学年: " + s.getGrade());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ListActivity.this, android.R.layout.simple_list_item_1, displayList);
                listView.setAdapter(adapter);
            });
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
