package dev.artem.mostransport.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import dev.artem.mostransport.MyCallback;
import dev.artem.mostransport.R;
import dev.artem.mostransport.models.User;
import dev.artem.mostransport.utils.DatabaseWrapper;

public class RegistrationActivity extends AppCompatActivity {

    Button btnRegister;
    ImageView btnBack;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        init();
    }
    private void init(){
        btnRegister = findViewById(R.id.BTNReg);
        btnBack = findViewById(R.id.backBTN);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference("userpass");
        initListeners();
    }
    private void initListeners(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegistrationButtonClicked();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, AuthActivity.class));
            }
        });

    }

    public void onRegistrationButtonClicked() {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper();
        databaseWrapper.getRootAsSnapshot(new MyCallback() {
            @Override
            public void onCallback(DataSnapshot rootSnapshot) {
                final EditText mailText = findViewById(R.id.mailET);
                final EditText passText = findViewById(R.id.pass1ET);
                final EditText postText = findViewById(R.id.postET);
                final EditText pass2Text = findViewById(R.id.pass2ET);
                final EditText nameText = findViewById(R.id.nameET);
                final EditText phoneText = findViewById(R.id.phoneET);

                if (nameText.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Введите имя", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!passText.getText().toString().equals(pass2Text.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (databaseWrapper.sameUsernameExists(rootSnapshot, nameText.getText().toString())) {
                    String message = "Пользователь с таким именем уже существует";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    return;
                }


                auth.createUserWithEmailAndPassword(mailText.getText().toString(), passText.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                DatabaseWrapper databaseWrapper = new DatabaseWrapper();
                                databaseWrapper.getRootAsSnapshot(new MyCallback() {
                                    @Override
                                    public void onCallback(DataSnapshot snapshot) {
                                        User user = new User();
                                        user.setEmail(mailText.getText().toString());
                                        user.setName(nameText.getText().toString());
                                        user.setPass(passText.getText().toString());
                                        user.setPhone(phoneText.getText().toString());
                                        user.setPost(postText.getText().toString());

                                        users.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                                        Toast.makeText(getApplicationContext(), "Вы зарегистрировались", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegistrationActivity.this, AuthActivity.class));
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                String message = "Что-то пошло не так";
                                if (e instanceof FirebaseAuthUserCollisionException) {
                                    message = "Такой пользователь уже существует";
                                } else if (e instanceof FirebaseAuthWeakPasswordException) {
                                    message = "Слабый пароль";
                                } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    message = "Неверный формат e-mail";
                                }
                                if (!(e instanceof FirebaseAuthUserCollisionException)){
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();}
                                else Toast.makeText(getApplicationContext(), "Что-то пошло не так", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}