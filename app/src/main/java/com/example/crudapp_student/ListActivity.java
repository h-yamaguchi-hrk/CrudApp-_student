package com.example.crudapp_student;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private String currentSort = "id";
    private Button btnSearch, btnSortId, btnSortName; // 制御用にメンバ変数化

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        final ListView listView = findViewById(R.id.listView);
        final EditText etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnSortId = findViewById(R.id.btnSortId);
        btnSortName = findViewById(R.id.btnSortName);

        // 初回表示
        refreshList("", currentSort, listView);

        btnSearch.setOnClickListener(v -> {
            String query = etSearch.getText().toString();
            refreshList(query, currentSort, listView);
        });

        btnSortId.setOnClickListener(v -> {
            currentSort = "id";
            refreshList(etSearch.getText().toString(), currentSort, listView);
        });

        btnSortName.setOnClickListener(v -> {
            currentSort = "name";
            refreshList(etSearch.getText().toString(), currentSort, listView);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSearch.setEnabled(enabled);
        btnSortId.setEnabled(enabled);
        btnSortName.setEnabled(enabled);
    }

    private void refreshList(String query, String sortBy, ListView listView) {
        setButtonsEnabled(false); // 処理開始時にボタンを無効化
        
        DatabaseHelper.getStudentsFiltered(query, sortBy, students -> {
            runOnUiThread(() -> {
                if (students == null) {
                    Toast.makeText(ListActivity.this, "DB接続失敗", Toast.LENGTH_LONG).show();
                    setButtonsEnabled(true);
                    return;
                }
                final List<String> displayList = new ArrayList<>();
                for (Student s : students) {
                    displayList.add("ID: " + s.getId() + ", 名前: " + s.getName() + ", 学年: " + s.getGrade());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ListActivity.this, android.R.layout.simple_list_item_1, displayList);
                listView.setAdapter(adapter);
                
                setButtonsEnabled(true); // 処理完了後に有効化
            });
        });
    }
}
