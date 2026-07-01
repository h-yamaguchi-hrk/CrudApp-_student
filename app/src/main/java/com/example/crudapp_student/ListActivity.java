package com.example.crudapp_student;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private String currentSort = "id";
    private int currentGradeFilter = 0;
    private Button btnSearch, btnSortId, btnSortName, btnDeleteAll, btnExport;
    private EditText etSearch;
    private ListView listView;
    private TextView tvStats;
    private List<Student> currentStudentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);
        etSearch = findViewById(R.id.etSearch);
        tvStats = findViewById(R.id.tvStats);
        btnSearch = findViewById(R.id.btnSearch);
        btnSortId = findViewById(R.id.btnSortId);
        btnSortName = findViewById(R.id.btnSortName);
        btnExport = findViewById(R.id.btnExport);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        Spinner spinnerGrade = findViewById(R.id.spinnerGrade);

        String[] grades = {"すべて", "1年生", "2年生", "3年生"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrade.setAdapter(adapter);

        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentGradeFilter = position;
                refreshList(etSearch.getText().toString(), currentGradeFilter, currentSort);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSearch.setOnClickListener(v -> refreshList(etSearch.getText().toString(), currentGradeFilter, currentSort));
        btnSortId.setOnClickListener(v -> { currentSort = "id"; refreshList(etSearch.getText().toString(), currentGradeFilter, currentSort); });
        btnSortName.setOnClickListener(v -> { currentSort = "name"; refreshList(etSearch.getText().toString(), currentGradeFilter, currentSort); });
        
        btnExport.setOnClickListener(v -> exportToLog());
        btnDeleteAll.setOnClickListener(v -> showDeleteAllDialog());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void exportToLog() {
        Log.d("STUDENT_EXPORT", "--- CSV START ---");
        for (Student s : currentStudentList) {
            Log.d("STUDENT_EXPORT", s.getId() + "," + s.getName() + "," + s.getGrade());
        }
        Log.d("STUDENT_EXPORT", "--- CSV END ---");
        Toast.makeText(this, "Logcatに出力しました", Toast.LENGTH_SHORT).show();
    }

    private void updateStats(List<Student> students) {
        int count = students.size();
        int sum = 0;
        for (Student s : students) {
            sum += s.getGrade();
        }
        
        // 修正：0除算を防止する
        double average = 0;
        if (count > 0) {
            average = (double) sum / count;
        }
        
        tvStats.setText("件数: " + count + "件 / 平均学年: " + String.format("%.1f", average));
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
                                refreshList(etSearch.getText().toString(), currentGradeFilter, currentSort);
                            }
                        });
                    });
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    private void setButtonsEnabled(boolean enabled) {
        btnSearch.setEnabled(enabled);
        btnSortId.setEnabled(enabled);
        btnSortName.setEnabled(enabled);
        // 修正：出力ボタンも正しく制御する
        btnExport.setEnabled(enabled);
        btnDeleteAll.setEnabled(enabled);
    }

    private void refreshList(String query, int grade, String sortBy) {
        setButtonsEnabled(false);
        DatabaseHelper.getStudentsFiltered(query, grade, sortBy, students -> {
            runOnUiThread(() -> {
                // 修正：画面がすでに閉じられている場合は処理を中断する（クラッシュ防止）
                if (isFinishing() || isDestroyed()) return;

                if (students == null) {
                    Toast.makeText(ListActivity.this, "DB接続失敗", Toast.LENGTH_LONG).show();
                    setButtonsEnabled(true);
                    return;
                }
                currentStudentList = students;
                updateStats(students);

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
