package au.edu.federation.itech3107.studentattendance30395623;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {

    private EditText etLoginUserId, etLoginPassword;
    private Button btnLogin;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        etLoginUserId = findViewById(R.id.etLoginUserId);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String userId = etLoginUserId.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();
            //Verify your instructor's sign-in information
            boolean isValid = dbHelper.validateTeacher(userId, password);
            if (isValid) {

                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "If you do not have the information of this teacher, please go to register first", Toast.LENGTH_SHORT).show();
            }

        });
    }
}
