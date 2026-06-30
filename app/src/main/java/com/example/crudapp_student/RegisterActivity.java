package com.example.crudapp_student;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etName = findViewById(R.id.etName);
        final EditText etGrade = findViewById(R.id.etGrade);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            btnSubmit.setEnabled(false);
            String name = etName.getText().toString();
            String gradeStr = etGrade.getText().toString();

            // 名前と学年の両方が入力されているかチェック
            if (!name.isEmpty() && !gradeStr.isEmpty()) {
                int grade = Integer.parseInt(gradeStr);
                
                // 学年のバリデーション (1〜3のみ許可)
                if (grade < 1 || grade > 3) {
                    Toast.makeText(this, "学年は1〜3の間で入力してください", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                    return;
                }

                Student student = new Student(name, grade);
                DatabaseHelper.insertStudent(student, result -> {
                    runOnUiThread(() -> {
                        if (result == null) {
                            Toast.makeText(RegisterActivity.this, "DB接続失敗", Toast.LENGTH_SHORT).show();
                            btnSubmit.setEnabled(true);
                        } else if (result) {
                            Toast.makeText(RegisterActivity.this, "登録成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "登録失敗", Toast.LENGTH_SHORT).show();
                            btnSubmit.setEnabled(true);
                        }
                    });
                });
            } else {
                Toast.makeText(this, "入力してください", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
