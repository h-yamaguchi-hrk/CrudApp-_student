package com.example.crudapp_student;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private String currentSort = "id";
    private int currentGradeFilter = 0; // 0: すべて
    private Button btnSearch, btnSortId, btnSortName, btnDeleteAll;
    private EditText etSearch;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnSortId = findViewById(R.id.btnSortId);
        btnSortName = findViewById(R.id.btnSortName);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        Spinner spinnerGrade = findViewById(R.id.spinnerGrade);

        // Spinnerの設定
        String[] grades = {"すべて", "1年生", "2年生", "3年生"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrade.setAdapter(adapter);

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // バグ仕込み4：ポジションの判定ミス（1年生を選んでも「すべて」になるなど、ズレている）
                currentGradeFilter = position - 1; 
                if (currentGradeFilter < 0) currentGradeFilter = 0;
                refreshList(etSearch.getText().toString(), currentGradeFilter, currentSort);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSearch.setOnClickListener(v -> refreshList(etSearch.getText().toString(), currentGradeFilter, currentSort));
        btnSortId.setOnClickListener(v -> { currentSort = "id"; refreshList(etSearch.getText().toString(), currentGradeFilter, currentSort); });
        btnSortName.setOnClickListener(v -> { currentSort = "name"; refreshList(etSearch.getText().toString(), currentGradeFilter, currentSort); });

        btnDeleteAll.setOnClickListener(v -> showDeleteAllDialog());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void showDeleteAllDialog() {
        new AlertDialog.Builder(this)
                .setTitle("一括削除")
                .setMessage("すべての学生データを削除しますか？")
                .setPositiveButton("OK", (dialog, which) -> {
                    DatabaseHelper.deleteAllStudents(result -> {
                        runOnUiThread(() -> {
                            if (result != null && result) {
                                Toast.makeText(ListActivity.this, "全削除しました", Toast.LENGTH_SHORT).show();
                                // バグ仕込み5：削除後に一覧をリフレッシュし忘れている（画面上のリストが消えない）
                            }
                        });
                    });
                })
                .setNegativeButton("キャンセル", (dialog, which) -> {
                    // バグ仕込み6：キャンセルボタンなのに削除処理を呼んでしまっている
                    DatabaseHelper.deleteAllStudents(result -> {});
                })
                .show();
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSearch.setEnabled(enabled);
        btnSortId.setEnabled(enabled);
        btnSortName.setEnabled(enabled);
        btnDeleteAll.setEnabled(enabled);
    }

    private void refreshList(String query, int grade, String sortBy) {
        setButtonsEnabled(false);
        DatabaseHelper.getStudentsFiltered(query, grade, sortBy, students -> {
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
                setButtonsEnabled(true);
            });
        });
    }
}
