package com.example.crudapp_student;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        final EditText etId = findViewById(R.id.etUpdateId);
        final EditText etName = findViewById(R.id.etUpdateName);
        final EditText etGrade = findViewById(R.id.etUpdateGrade);
        Button btnSubmit = findViewById(R.id.btnUpdateSubmit);

        btnSubmit.setOnClickListener(v -> {
            String idStr = etId.getText().toString();
            String name = etName.getText().toString();
            String gradeStr = etGrade.getText().toString();

            if (!idStr.isEmpty() && !name.isEmpty() && !gradeStr.isEmpty()) {
                Student student = new Student(Integer.parseInt(idStr), name, Integer.parseInt(gradeStr));
                DatabaseHelper.updateStudent(student, success -> {
                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(UpdateActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UpdateActivity.this, "更新失敗", Toast.LENGTH_SHORT).show();
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
