package com.example.bvarg.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class autenticacion extends AppCompatActivity {

    //Autenticacion
    FirebaseAuth mAuth;
    EditText correo;
    EditText contrasena;
    Button ingresar;
    Button registrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacion);

        mAuth = FirebaseAuth.getInstance();
        correo = findViewById(R.id.editText_correo);
        contrasena = findViewById(R.id.editText_contrasena);
        contrasena.setError("La contraseña debe contener al menos 6 caracteres");
        ingresar = findViewById(R.id.button_ingresar);
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!correo.getText().toString().equals("") && !contrasena.getText().toString().equals("")){
                    logearUsuario(correo.getText().toString(),contrasena.getText().toString());
                }
                else{
                    if(correo.getText().toString().equals("")){
                        correo.setError("No debe ser Vacio");
                    }
                    if(contrasena.getText().toString().equals("")){
                        contrasena.setError("No debe ser Vacio");
                    }
                }

            }
        });
        registrarse = findViewById(R.id.button_Registrar);
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!correo.getText().toString().equals("") && !contrasena.getText().toString().equals("")) {
                    if(contrasena.getText().length()>5){
                        registrarUsuario(correo.getText().toString(), contrasena.getText().toString());
                    }
                    else{
                        contrasena.setError("La contraseña debe contener al menos 6 caracteres");
                    }

                }
                else{
                    if(correo.getText().toString().equals("")){
                        correo.setError("No debe ser Vacio");
                    }
                    if(contrasena.getText().toString().equals("")){
                        contrasena.setError("No debe ser Vacio");
                    }
                }
            }
        });
    }

    public void registrarUsuario(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sirvio", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(autenticacion.this, "Registro Exitoso.", Toast.LENGTH_SHORT).show();
                        } else {
                            correo.setError("Se debe indicar un correo electrónico válido");
                            // If sign in fails, display a message to the user.
                            Log.w("No sirvio", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(autenticacion.this, "Registro Fallido.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void logearUsuario(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sirvio", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            MainActivity.registrado = true;
                            Toast.makeText(autenticacion.this, "Autenticacion Correcta.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("So sirvio", "signInWithEmail:failure", task.getException());
                            Toast.makeText(autenticacion.this, "Autenticacion Incorrecta.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
