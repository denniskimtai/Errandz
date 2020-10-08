package app.infiniverse.grocery;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {


    String name, email, mobile, address, password,cpassword;
    EditText etName, etMobile, etEmail, etAddress, etPassword , etcPassword;
    Button register;
    TextView loginnext;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginnext = findViewById(R.id.loginnext);
        etName = findViewById(R.id.name);
        etEmail = findViewById(R.id.email);
        etMobile = findViewById(R.id.mobile);
        etPassword = findViewById(R.id.password);
        etcPassword = findViewById(R.id.cpassword);
        register = findViewById(R.id.register);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        alertDialog = new AlertDialog.Builder(this);

        loginnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                name = etName.getText().toString();
                email = etEmail.getText().toString();
                mobile = etMobile.getText().toString();
                password = etPassword.getText().toString();
                cpassword = etcPassword.getText().toString();

                if ((!name.equals("")) && (!email.equals("")) && (!mobile.equals(""))   && (!password.equals("")) && (!cpassword.equals(""))) {


                        if(!cpassword.equals(password)) {
                            etcPassword.setError("Password Didn't Matched");

                        }else if(!mobile.matches("[0-9]{10}"))
                        {

                            etMobile.setError("Enter Valid Number");

                        }else if(!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))
                        {

                            etEmail.setError("Enter Valid Email Address");

                        }
                        else registerUser(email, password);

                }else{
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please fill all Fields above", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

    }

    public void registerUser(String userEmail, String userPassword) {

        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }else {
            progressDialog.setMessage("Registering. Please wait...");
            progressDialog.show();
        }

        //using firebase to register new user
        firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //check if user is created successfully
                if (task.isSuccessful()){

                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    //go to login activity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);

                }else{
                    //if registration is not successful retry registration
                    if (progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }

                    alertDialog.setMessage("Registration failed please try again. Ensure your internet connection is okay");
                    alertDialog.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            registerUser(email, password);

                        }
                    });

                    alertDialog.setNegativeButton("Browse Products", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //go to home activity
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(intent);

                        }
                    });

                    alertDialog.show();

                }



            }
        });
    }

}