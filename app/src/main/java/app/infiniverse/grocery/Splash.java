package app.infiniverse.grocery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Splash extends FragmentActivity {

    private Animation animation;
    private ImageView logo;
    private TextView appTitle;
    private TextView appSlogan;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.logo);
        appTitle = findViewById(R.id.grocery);
        appSlogan = findViewById(R.id.slogan);

        firebaseAuth = FirebaseAuth.getInstance();



        if (ConnectivityReceiver.isConnected()) {

            if (savedInstanceState == null) {

            }

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    endSplash();
                }
            }, 3000);


        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet Connection ");
            builder.setMessage("Do you want to go to settings");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent j = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(j);
                    System.exit(0);
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                    System.exit(0);
                }
            });
            builder.show();
        }



    }


    private void endSplash() {

        if (firebaseAuth.getCurrentUser() != null){

            //if user is already logged in go to home activity
            Intent intent = new Intent(getApplicationContext(),
                    HomeActivity.class);
            startActivity(intent);
            finish();

        }else {

            Intent intent = new Intent(getApplicationContext(),
                    StartActivity.class);
            startActivity(intent);
            finish();

        }




    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

}

