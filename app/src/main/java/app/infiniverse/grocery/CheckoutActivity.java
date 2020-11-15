package app.infiniverse.grocery;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private String payableAmount, firstName, lastName, deliveryAddress, deliveryCity, EmailAddress, PhoneNumber;

    private TextView txtViewPayableAmount;

    private EditText fName, lName, address, city, emailAddress, phoneNumber;

    private Button btnCompleteOrder;

    private String URL = "http://errandz.xyz/woocommerce/place_order.php";

    private ProgressDialog dialog;

    private AlertDialog.Builder builder;

    private String[] product_ids;
    private String[] product_qtys;

    private DatabaseReference mDatabase, mDatabaseCartItems;
    private FirebaseAuth firebaseAuth;
    private String userId;

    String orderId;

    int x = 0;
    int r = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Bundle b = this.getIntent().getExtras();
        product_ids = b.getStringArray("product_ids_array");
        product_qtys = b.getStringArray("product_qty_array");
        payableAmount = b.getString("total_amount");

        txtViewPayableAmount = findViewById(R.id.txtPayableAmount);

        txtViewPayableAmount.setText("Ksh " + payableAmount + "/-");

        dialog = new ProgressDialog(this);

        builder = new AlertDialog.Builder(this);

        //views initialization
        fName = findViewById(R.id.fName);
        lName = findViewById(R.id.lName);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        emailAddress = findViewById(R.id.emailAddress);
        phoneNumber = findViewById(R.id.phoneNumber);


        btnCompleteOrder = findViewById(R.id.btnCompleteOrder);

        btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if all edittext fields are field
                firstName = fName.getText().toString();
                if (TextUtils.isEmpty(firstName)){

                    fName.setError("Please enter your first name");
                    fName.requestFocus();
                    return;
                }

                lastName = lName.getText().toString();
                if (TextUtils.isEmpty(lastName)){

                    lName.setError("Please enter your last name");
                    lName.requestFocus();
                    return;
                }

                deliveryAddress = address.getText().toString();
                if (TextUtils.isEmpty(deliveryAddress)){

                    address.setError("Please enter your delivery address");
                    address.requestFocus();
                    return;
                }

                deliveryCity = city.getText().toString();
                if (TextUtils.isEmpty(deliveryCity)){

                    city.setError("Please enter your delivery city");
                    city.requestFocus();
                    return;
                }

                EmailAddress = emailAddress.getText().toString();
                if (TextUtils.isEmpty(EmailAddress)){

                    emailAddress.setError("Please enter your email address");
                    emailAddress.requestFocus();
                    return;
                }

                PhoneNumber = phoneNumber.getText().toString();
                if (TextUtils.isEmpty(PhoneNumber)){

                    phoneNumber.setError("Please enter your phone number");
                    phoneNumber.requestFocus();
                    return;
                }


                //realtime firebase database
                mDatabase = FirebaseDatabase.getInstance().getReference("Order Ids");
                firebaseAuth = FirebaseAuth.getInstance();
                mDatabaseCartItems = FirebaseDatabase.getInstance().getReference("Cart Items");

                userId = firebaseAuth.getUid();

                if (isNetworkAvailable()){

                    sendOrder();

                    //*************************************************************************************//

//                    //creating a string request to send request to the url
//                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//
//                                    builder.setMessage(response);
//                                    builder.show();
//
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    //displaying the error in toast if occurs
//                                    Toast.makeText(getApplicationContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//
//                                    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//
//                                        Toast.makeText(CheckoutActivity.this, "1", Toast.LENGTH_SHORT).show();
//
//
//                                    } else if (error instanceof AuthFailureError) {
//
//                                        Toast.makeText(CheckoutActivity.this, "2", Toast.LENGTH_SHORT).show();
//
//                                    } else if (error instanceof ServerError) {
//
//                                        Toast.makeText(CheckoutActivity.this, "3", Toast.LENGTH_SHORT).show();
//
//                                    } else if (error instanceof NetworkError) {
//
//                                        Toast.makeText(CheckoutActivity.this, "4", Toast.LENGTH_SHORT).show();
//
//                                    } else if (error instanceof ParseError) {
//
//                                        Toast.makeText(CheckoutActivity.this, "5", Toast.LENGTH_SHORT).show();
//
//                                    }
//
//                                }
//                            }){
//                        //send params needed to db
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//
//                            Map<String,String> params = new HashMap<>();
//                            params.put("fname", firstName);
//                            params.put("lname", lastName);
//                            params.put("address", deliveryAddress);
//                            params.put("city", deliveryCity);
//                            params.put("email_address", EmailAddress);
//                            params.put("phone_number", PhoneNumber);
//                            params.put("product_id", product_ids[x]);
//                            params.put("product_qty", product_qtys[x]);
//                            return params;
//
//                        }
//                    };
//
//                    //creating a request queue
//                    RequestQueue requestQueue = Volley.newRequestQueue(CheckoutActivity.this);
//
//                    //adding the string request to request queue
//                    requestQueue.add(stringRequest);
//
//                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                            100000,
//                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//                    //*************************************************************************************//
//
//
//                }else {
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
//                    builder.setMessage("No internet connection")
//                            .setCancelable(false)
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//
//                                }
//                            });
//                    builder.show();
//

                }


            }
        });


    }

    public void sendOrder(){

        //show dialog
        dialog.setMessage("Placing order...");
        dialog.show();

        //send orders to api

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                x++;
                //converting response to json object
                try {
                    JSONObject obj = new JSONObject(response);
                    orderId = obj.getString("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (x == product_ids.length){


                    //all orders are sent
                    //delete cart item from firebase
                    mDatabaseCartItems.child(userId).removeValue();
                    //push to firebase

                    mDatabase.child(userId).child("order_history_ids").push().setValue(orderId);

                    dialog.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                    builder.setTitle("Successful")
                            .setMessage("Order Placed Successfully")
                            .setIcon(R.drawable.ic_check_black)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Intent ii = new Intent(CheckoutActivity.this, OrderActivity.class);
                                    startActivity(ii);
                                    finish();
                                }
                            });
                    builder.show();

                }else{

                    //not all orders are sent
                    //delete cart item from firebase
                    mDatabase.child(userId).child(product_ids[x]).removeValue();
                    //push to firebase
                    mDatabase.child(userId).child("order_history_ids").setValue(orderId);

                    sendOrder();

                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();

                Toast.makeText(CheckoutActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
                    builder.setTitle("Failed")
                            .setMessage("Order not Placed. Please ensure your internet connection is stable and try again.")
                            .setCancelable(false)
                            .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    sendOrder();
                                }
                            });
                    builder.show();

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("fname", firstName);
                params.put("lname", lastName);
                params.put("address", deliveryAddress);
                params.put("city", deliveryCity);
                params.put("email_address", EmailAddress);
                params.put("phone_number", PhoneNumber);
                params.put("product_id", product_ids[x]);
                params.put("product_qty", product_qtys[x]);
                return params;
            }

        };

        Volley.newRequestQueue(this).add(stringRequest);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}