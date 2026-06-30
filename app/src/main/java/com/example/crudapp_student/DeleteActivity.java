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
        final Button btnSubmit = findViewById(R.id.btnDeleteSubmit);

        btnSubmit.setOnClickListener(v -> {
            btnSubmit.setEnabled(false); // 連打防止
            String idStr = etId.getText().toString();

            if (!idStr.isEmpty()) {
                try {
                    int id = Integer.parseInt(idStr);
                    DatabaseHelper.deleteStudent(id, result -> {
                        runOnUiThread(() -> {
                            if (result == null) {
                                Toast.makeText(DeleteActivity.this, "DB接続失敗", Toast.LENGTH_SHORT).show();
                                btnSubmit.setEnabled(true);
                            } else if (result) {
                                Toast.makeText(DeleteActivity.this, "削除成功", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(DeleteActivity.this, "削除失敗", Toast.LENGTH_SHORT).show();
                                btnSubmit.setEnabled(true);
                            }
                        });
                    });
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "IDには数字を入力してください", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                }
            } else {
                Toast.makeText(this, "IDを入力してください", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
