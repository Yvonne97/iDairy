package com.example.hp.milkproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpWorkerActivity extends AppCompatActivity {

    EditText editTextPassword, editTextEmail, editTextFname, editTextSname;
    Button buttonSignUp;
    private FirebaseAuth mAuth;
    String email;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_worker);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        email = bundle.getString("email");

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextFname = findViewById(R.id.editTextFname);
        editTextSname = findViewById(R.id.editTextSname);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        editTextEmail.setText(email);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextSname.getText().toString().equals("") || editTextFname.getText().toString().equals("") || editTextPassword.getText().toString().equals("") ||
                        editTextEmail.getText().toString().equals("")){
                    Toast.makeText(SignUpWorkerActivity.this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Signing you up");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(SignUpWorkerActivity.this, "Unable to sigh you up", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }else {
                                        final Map<String, String> map = new HashMap<>();
                                        map.put("email", editTextEmail.getText().toString());
                                        map.put("sname", editTextSname.getText().toString());
                                        map.put("fname", editTextFname.getText().toString());
                                        map.put("uid", FirebaseAuth.getInstance().getUid());
                                        map.put("role", "Worker");

                                        FirebaseFirestore chats = FirebaseFirestore.getInstance();


                                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                        firebaseFirestore.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                                                firebaseFirestore.collection("Worker").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        progressDialog.dismiss();
                                                        Intent intent = new Intent(SignUpWorkerActivity.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });


    }
}
