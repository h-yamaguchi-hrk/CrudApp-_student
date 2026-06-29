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
            String name = etName.getText().toString();
            String gradeStr = etGrade.getText().toString();

            if (!name.isEmpty() && !gradeStr.isEmpty()) {
                Student student = new Student(name, Integer.parseInt(gradeStr));
                DatabaseHelper.insertStudent(student, success -> {
                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(RegisterActivity.this, "登録成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "登録失敗", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else {
                Toast.makeText(this, "入力してください", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
