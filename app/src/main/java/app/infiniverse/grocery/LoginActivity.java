package app.infiniverse.grocery;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS = "PREFS";
    EditText etLogin_id, etPassword;
    Button btnLogin;
    String login_id, password;
    TextView signup, forget;
    SharedPreferences sp;
    SharedPreferences.Editor edit;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        edit = sp.edit();

        signup = findViewById(R.id.signup);
        etLogin_id = findViewById(R.id.loginid);
        etPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        forget = findViewById(R.id.forget);

        //initialize firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        alertDialog = new AlertDialog.Builder(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), ForgetActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void login() {
        login_id = etLogin_id.getText().toString();
        password = etPassword.getText().toString();
        if ((!login_id.equals("")) && (!password.equals(""))) {

            //progress dialog
            progressDialog.setMessage("Logging in.Please wait...");
            progressDialog.show();

            //login using firebase
            firebaseAuth.signInWithEmailAndPassword(login_id, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    progressDialog.dismiss();
                    //check if login is successfull
                    if (task.isSuccessful()){

                        //save to shared pref
                        edit.putString("loginid", firebaseAuth.getCurrentUser().getUid());
                        edit.commit();

                        //for now go to home activity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);

                    }else {

                        //if login was not successful
                        alertDialog.setMessage("Login was not successful. Ensure you have an account and your credentials are correct then try again. ");

                        alertDialog.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });

                        alertDialog.setNegativeButton("Browse Products", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //go to home activity
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);

                            }
                        });

                        alertDialog.show();


                    }


                }
            });

        } else {

            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please fill all Fields above before proceding", Snackbar.LENGTH_SHORT);
            snackbar.show();

        }
    }
}