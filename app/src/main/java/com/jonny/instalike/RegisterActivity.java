package com.jonny.instalike;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, name, email, password;
    private Button register;
    private TextView loginuser;

//    private DatabaseReference mRootRef;
    private FirebaseAuth fAuth ;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        username = findViewById(R.id.username);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginuser = findViewById(R.id.login_user);
        register = findViewById(R.id.register);
        pd = new ProgressDialog(this);

        fAuth = FirebaseAuth.getInstance();

        loginuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUsername = username.getText().toString(),
                        txtName = name.getText().toString(),
                        txtEmail = email.getText().toString(),
                        txtPassword = password.getText().toString();

                if (TextUtils.isEmpty(txtUsername) || TextUtils.isEmpty(txtName) || TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)) {
                    Toast.makeText(RegisterActivity.this, "EMPTY CREDENTIALS!", Toast.LENGTH_SHORT).show();
                } else if (txtPassword.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password is too SHORT!", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txtUsername, txtName, txtEmail, txtPassword);
                }
            }
        });

    }
    private void registerUser(String username, String name, String email, String password) {

                pd.setMessage("Please Wait");
                pd.show();
                fAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", name);
                        map.put("email", email);
                        map.put("username", username);
                        map.put("id",fAuth.getCurrentUser().getUid());
                        map.put("bio","");
                        map.put("imageurl","default");
                        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        Ref.child(fAuth.getCurrentUser().getUid()).setValue(map);

                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, "Update the profile for better Experience ", Toast.LENGTH_SHORT).show();
                        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

}
