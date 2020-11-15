package app.infiniverse.grocery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.HashMap;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {


    public static final String PREFS = "PREFS";
    SharedPreferences sp;
    ConstraintLayout cl;
    private ProgressBar mProgressBar;

    private DatabaseReference mDatabase;
    private FirebaseAuth firebaseAuth;
    private String userId;

    private int i = 0;

    private String [] order_id;

    JSONArray myarray = new JSONArray();

    private final String GET_ORDER_URL = "http://errandz.xyz/woocommerce/get_order.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mProgressBar = findViewById(R.id.progressBar);

        //realtime firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("Order Ids");
        firebaseAuth = FirebaseAuth.getInstance();

        userId = firebaseAuth.getUid();


        cl = findViewById(R.id.ll_empty);
        mProgressBar.setVisibility(View.VISIBLE);


        sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        class RecentProducts extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
//                String orderUrl = getResources().getString(R.string.base_url) + "orders/";
//
//                try {
//                    URL url = new URL(orderUrl);
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    httpURLConnection.setRequestMethod("POST");
//                    httpURLConnection.setDoInput(true);
//                    httpURLConnection.setDoOutput(true);
//                    OutputStream outputStream = httpURLConnection.getOutputStream();
//                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
//                    String post_Data = URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
//
//                    bufferedWriter.write(post_Data);
//                    bufferedWriter.flush();
//                    bufferedWriter.close();
//                    outputStream.close();
//                    InputStream inputStream = httpURLConnection.getInputStream();
//                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//                    String result = "", line = "";
//                    while ((line = bufferedReader.readLine()) != null) {
//                        result += line;
//                    }
//                    return result;
//                } catch (Exception e) {
//                    return e.toString();
//                }

                return "";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                //get orders from firebase database

                //get cart items from firebase
                mDatabase.child(userId).child("order_history_ids").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            order_id = new String[(int) dataSnapshot.getChildrenCount()];

                            for(DataSnapshot datas: dataSnapshot.getChildren()){


                                order_id[i] = datas.getValue().toString();
                                myarray.put(datas.getValue().toString());

                                i++;

                            }

                           //send order ids to api to get order details
                            StringRequest request = new StringRequest(Request.Method.POST,
                                    GET_ORDER_URL,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                                            builder.setTitle("Received Message");

                                            try {

                                                JSONArray orderArray = new JSONArray(response);

                                                String[] order_ids = new String[orderArray.length()];
                                                String[] order_savings = new String[orderArray.length()];
                                                String[] order_payableamts = new String[orderArray.length()];
                                                String[] order_status = new String[orderArray.length()];
                                                String[] order_dts = new String[orderArray.length()];


                                                JSONObject json_data = new JSONObject();
                                                for (int i = 0; i < orderArray.length(); i++) {
                                                    json_data = orderArray.getJSONObject(i);
                                                    order_ids[i] = json_data.getString("order_id");
                                                    order_savings[i] = json_data.getString("product_name");
                                                    order_payableamts[i] = json_data.getString("total_amount");
                                                    order_status[i] = json_data.getString("status");
                                                    order_dts[i] = json_data.getString("date_created");

                                                }

                                                mProgressBar.setVisibility(View.GONE);
                                                if (orderArray.length() == 0) {

                                                    cl.setVisibility(View.VISIBLE);
                                                } else {
                                                    RecyclerView order_recyclerview = findViewById(R.id.recyclerview_order_details);
                                                    order_recyclerview.setNestedScrollingEnabled(false);
                                                    order_recyclerview.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                                                    order_recyclerview.setAdapter(new Customer_Order_Adapter(order_ids, order_savings, order_payableamts, order_status, order_dts, OrderActivity.this));
                                                }

                                            } catch (JSONException e) {
                                                builder.setCancelable(true);
                                                builder.setTitle("No Internet Connection");
                                                builder.setMessage(e.getMessage());
                                                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                    }
                                                });
                                                builder.show();
                                            }


                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Toast.makeText(OrderActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }){
                                @Override
                                protected Map<String,String> getParams(){
                                    Map<String,String> params = new HashMap<>();
                                    params.put("order_ids", myarray.toString());

                                    return params;
                                }

                            };

                            Volley.newRequestQueue(OrderActivity.this).add(request);

                            request.setRetryPolicy(new DefaultRetryPolicy(
                                    100000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                        }

                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(OrderActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                });



//                AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
//                builder.setTitle("Received Message");
//
//                try {
//
//                    JSONArray orderArray = new JSONArray(s);
//
//                    String[] order_ids = new String[orderArray.length()];
//                    String[] order_savings = new String[orderArray.length()];
//                    String[] order_payableamts = new String[orderArray.length()];
//                    String[] order_status = new String[orderArray.length()];
//                    String[] order_dts = new String[orderArray.length()];
//
//
//                    JSONObject json_data = new JSONObject();
//                    for (int i = 0; i < orderArray.length(); i++) {
//                        json_data = orderArray.getJSONObject(i);
//                        order_ids[i] = json_data.getString("id");
//                        order_savings[i] = json_data.getString("savings");
//                        order_payableamts[i] = json_data.getString("payableamt");
//                        order_status[i] = json_data.getString("status");
//                        order_dts[i] = json_data.getString("order_dt");
//
//                    }
//
//                    mProgressBar.setVisibility(View.GONE);
//                    if (orderArray.length() == 0) {
//
//                        cl.setVisibility(View.VISIBLE);
//                    } else {
//                        RecyclerView order_recyclerview = findViewById(R.id.recyclerview_order_details);
//                        order_recyclerview.setNestedScrollingEnabled(false);
//                        order_recyclerview.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
//                        order_recyclerview.setAdapter(new Customer_Order_Adapter(order_ids, order_savings, order_payableamts, order_status, order_dts, OrderActivity.this));
//                    }
//                } catch (JSONException e) {
//                    builder.setCancelable(true);
//                    builder.setTitle("No Internet Connection");
////                    builder.setMessage(s);
//                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    });
//                    builder.show();
//                }

            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }


        }
        RecentProducts recentProducts = new RecentProducts();
        recentProducts.execute(sp.getString("loginid", null));


    }
}
