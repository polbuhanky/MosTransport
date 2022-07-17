package dev.artem.mostransport.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yandex.mapkit.MapKitFactory;

import dev.artem.mostransport.R;
import dev.artem.mostransport.utils.StatusBarUtils;

public class AuthActivity extends AppCompatActivity {
    Button btnSignIn;
    TextView btnToReg;
    FirebaseAuth auth;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
        StatusBarUtils.setColor(this, getResources().getColor(R.color.colorAccent), 0);
        autoAuth();

        init();
    }

    private void init() {
        btnSignIn = findViewById(R.id.BTNSign);
        btnToReg = findViewById(R.id.BTNReg);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("userpass");
        initListeners();
    }

    private void initListeners() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignInButtonClicked();
            }
        });
        btnToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AuthActivity.this, RegistrationActivity.class));
            }
        });
    }

    private void autoAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        SharedPreferences autoAuth = getSharedPreferences("auto_auth", Context.MODE_PRIVATE);
        if (autoAuth.contains("Password") && autoAuth.contains("Email") && autoAuth.contains("isNecessary")) {
            if (autoAuth.getBoolean("isNecessary", false)) {
                String mailText = autoAuth.getString("Email", "");
                String passText = autoAuth.getString("Password", "");
                auth.signInWithEmailAndPassword(mailText, passText).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast toast2 = Toast.makeText(getApplicationContext(),
                                "Вы вошли в аккаунт " + mailText, Toast.LENGTH_SHORT);
                        toast2.show();

                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }
        getWindow().setNavigationBarColor(getResources().getColor(R.color.BarColor));
    }


    public void onSignInButtonClicked() {
        EditText mailText = findViewById(R.id.login);
        EditText passText = findViewById(R.id.password);
        if (mailText.getText().toString().isEmpty()) {
            Toast.makeText(AuthActivity.this, "Введите e-mail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (passText.getText().toString().isEmpty()) {
            Toast.makeText(AuthActivity.this, "Введите пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(mailText.getText().toString(), passText.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                final CheckBox checkP = findViewById(R.id.checkBoxAuth);

                if (checkP.isChecked()) {
                    SharedPreferences autoAuth = getSharedPreferences("auto_auth", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = autoAuth.edit();
                    editor.putBoolean("isNecessary", true);
                    editor.putString("Email", mailText.getText().toString());
                    editor.putString("Password", passText.getText().toString());
                    editor.apply();
                    Toast toast2 = Toast.makeText(getApplicationContext(),
                            "Теперь вы будете входить автоматически", Toast.LENGTH_LONG);
                    toast2.show();
                }
                Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = "Что-то пошло не так. Проверьте интернет-соединение";
                if (e instanceof FirebaseAuthInvalidUserException) {
                    message = "Такого пользователя не существует";
                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    message = "Неверный пароль";
                }
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}