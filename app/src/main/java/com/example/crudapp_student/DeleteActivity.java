package com.example.crudapp_student;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DeleteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        final EditText etId = findViewById(R.id.etDeleteId);
        Button btnSubmit = findViewById(R.id.btnDeleteSubmit);

        btnSubmit.setOnClickListener(v -> {
            String idStr = etId.getText().toString();

            if (!idStr.isEmpty()) {
                DatabaseHelper.deleteStudent(Integer.parseInt(idStr), success -> {
                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(DeleteActivity.this, "削除成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(DeleteActivity.this, "削除失敗", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            } else {
                Toast.makeText(this, "IDを入力してください", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
