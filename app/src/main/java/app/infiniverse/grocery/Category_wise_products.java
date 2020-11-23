package app.infiniverse.grocery;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

public class Category_wise_products extends AppCompatActivity implements AddorRemoveCallbacks {

    public static final String PREFS = "PREFS";
    LinearLayout ll;
    SharedPreferences sp;
    ConstraintLayout cl;
    int cart_count;
    private ProgressBar mProgressBar;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    private final String GET_PRODUCT_BY_CATEGORY_URL = "http://errandz.xyz/woocommerce/get_product_by_category.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_wise_products);
        Toolbar toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        sp = getApplicationContext().getSharedPreferences(PREFS, MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressBar = findViewById(R.id.progressBar);

        Bundle extras = getIntent().getExtras();
        cart_count = Integer.parseInt(extras.getString("cart_count"));

        ll = findViewById(R.id.ll_products);

        cl= findViewById(R.id.ll_empty);

        firebaseAuth = FirebaseAuth.getInstance();
        //realtime firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("Cart Items");

        Bundle bundle = getIntent().getExtras();
        final String sub_cat_id = bundle.getString("sub_cat_id");
        final String sub_category = bundle.getString("sub_category");

        getSupportActionBar().setTitle("");


        mProgressBar.setVisibility(View.VISIBLE);

        class Products extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
//                String productUrl = getResources().getString(R.string.base_url) + "getProductsOfSubCategory/" + sub_cat_id;
//
//                try {
//                    URL url = new URL(productUrl);
//
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    httpURLConnection.setRequestMethod("POST");
//                    httpURLConnection.setDoInput(true);
//                    httpURLConnection.setDoOutput(true);
//
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

                StringRequest request = new StringRequest(Request.Method.POST,
                        GET_PRODUCT_BY_CATEGORY_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(Category_wise_products.this);
                        builder.setTitle("Received Message");

                        try {

                            JSONArray productArray = new JSONArray(response);

                            String[] product_ids = new String[productArray.length()];
                            String[] product_names = new String[productArray.length()];
                            String[] product_descs = new String[productArray.length()];
                            String[] product_imgs = new String[productArray.length()];
                            String[] product_prices = new String[productArray.length()];
                            String[] product_brands = new String[productArray.length()];
                            String[] product_sps = new String[productArray.length()];
                            String[] product_dps = new String[productArray.length()];


                            JSONObject json_data = new JSONObject();
                            for (int i = 0; i < productArray.length(); i++) {
                                json_data = productArray.getJSONObject(i);
                                product_ids[i] = json_data.getString("product_id");
                                product_names[i] = json_data.getString("product_name");
                                product_descs[i] = json_data.getString("product_description").replace("<p>", "");
                                product_imgs[i] = json_data.getString("product_image");
                                product_prices[i] = json_data.getString("product_regular_price") + " /-";
                                product_brands[i] = json_data.getString("product_short_description").replace("<p>", "");
                                product_sps[i] = json_data.getString("product_price") + " /-";
                                double p_mrp = Double.parseDouble(json_data.getString("product_regular_price"));
                                double p_sp = Double.parseDouble(json_data.getString("product_price"));
                                double p_dp = (p_mrp - p_sp) / (p_mrp / 100);
                                int p_dp_i = (int) p_dp;
                                product_dps[i] = String.valueOf(p_dp_i);


                            }

                            mProgressBar.setVisibility(View.GONE);
                            if (productArray.length() == 0) {

                                cl.setVisibility(View.VISIBLE);
                            } else {
                                ll.setVisibility(View.VISIBLE);
                                RecyclerView product_recyclerview = findViewById(R.id.recyclerview_products);
                                product_recyclerview.setNestedScrollingEnabled(false);
                                product_recyclerview.setLayoutManager(new LinearLayoutManager(Category_wise_products.this));
                                product_recyclerview.setAdapter(new Recent_Products_Adapter(product_ids, product_names, product_descs, product_imgs, product_prices, product_brands, product_sps, product_dps, Category_wise_products.this));

                            }
                        } catch (JSONException e) {
                            builder.setCancelable(true);
                            builder.setTitle("No Internet Connection");
//                    builder.setMessage(s);
                            builder.setMessage("Please Connect to internet");
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

                    }
                }){
                    @Override
                    protected Map<String,String> getParams(){
                        Map<String,String> params = new HashMap<>();
                        params.put("slug", sub_category);

                        return params;
                    }

                };

                Volley.newRequestQueue(Category_wise_products.this).add(request);

                request.setRetryPolicy(new DefaultRetryPolicy(
                        100000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

//                AlertDialog.Builder builder = new AlertDialog.Builder(Category_wise_products.this);
//                builder.setTitle("Received Message");

//                try {
//
//                    JSONArray productArray = new JSONArray(s);
//
//                    String[] product_ids = new String[productArray.length()];
//                    String[] product_names = new String[productArray.length()];
//                    String[] product_descs = new String[productArray.length()];
//                    String[] product_imgs = new String[productArray.length()];
//                    String[] product_prices = new String[productArray.length()];
//                    String[] product_brands = new String[productArray.length()];
//                    String[] product_sps = new String[productArray.length()];
//                    String[] product_dps = new String[productArray.length()];
//
//
//                    JSONObject json_data = new JSONObject();
//                    for (int i = 0; i < productArray.length(); i++) {
//                        json_data = productArray.getJSONObject(i);
//                        product_ids[i] = json_data.getString("id");
//                        product_names[i] = json_data.getString("name");
//                        product_descs[i] = json_data.getString("description");
//                        product_imgs[i] = json_data.getString("image");
//                        product_prices[i] = json_data.getString("mrp") + " /-";
//                        product_brands[i] = json_data.getString("brand");
//                        product_sps[i] = "\u20B9" + json_data.getString("selling_price") + " /-";
//                        double p_mrp = Double.parseDouble(json_data.getString("mrp"));
//                        double p_sp = Double.parseDouble(json_data.getString("selling_price"));
//                        double p_dp = (p_mrp - p_sp) / (p_mrp / 100);
//                        int p_dp_i = (int) p_dp;
//                        product_dps[i] = String.valueOf(p_dp_i);
//
//
//                    }
//
//                    mProgressBar.setVisibility(View.GONE);
//                    if (productArray.length() == 0) {
//
//                        cl.setVisibility(View.VISIBLE);
//                    } else {
//                        ll.setVisibility(View.VISIBLE);
//                        RecyclerView product_recyclerview = findViewById(R.id.recyclerview_products);
//                        product_recyclerview.setNestedScrollingEnabled(false);
//                        product_recyclerview.setLayoutManager(new LinearLayoutManager(Category_wise_products.this));
//                        product_recyclerview.setAdapter(new Recent_Products_Adapter(product_ids, product_names, product_descs, product_imgs, product_prices, product_brands, product_sps, product_dps, Category_wise_products.this));
//
//                    }
//                } catch (JSONException e) {
//                    builder.setCancelable(true);
//                    builder.setTitle("No Internet Connection");
////                    builder.setMessage(s);
//                    builder.setMessage("Please Connect to internet");
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
        Products products = new Products();
        products.execute();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        final MenuItem menuItem = menu.findItem(R.id.cart);
        MenuItem menuItem2 = menu.findItem(R.id.action_search);
        menuItem2.setVisible(false);
        menuItem.setIcon(Converter.convertLayoutToImage(Category_wise_products.this, cart_count, R.drawable.ic_shopping_cart_white));


        if (sp.getString("loginid", null) != null) {
            class GetCartItemCount extends AsyncTask<String, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    //get cart items from firebase
                    mDatabase.child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){

                                cart_count = (int) dataSnapshot.getChildrenCount();
                                menuItem.setIcon(Converter.convertLayoutToImage(Category_wise_products.this,cart_count,R.drawable.ic_shopping_cart_white));

                            }else{

                                cart_count = 0;
                                menuItem.setIcon(Converter.convertLayoutToImage(Category_wise_products.this,cart_count,R.drawable.ic_shopping_cart_white));

                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                protected String doInBackground(String... params) {

//                    String urls = getResources().getString(R.string.base_url).concat("getItemCount/");
//                    try {
//                        URL url = new URL(urls);
//                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                        httpURLConnection.setRequestMethod("POST");
//                        httpURLConnection.setDoInput(true);
//                        httpURLConnection.setDoOutput(true);
//                        OutputStream outputStream = httpURLConnection.getOutputStream();
//                        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
//                        String post_Data = URLEncoder.encode("login_id", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8");
//
//                        bufferedWriter.write(post_Data);
//                        bufferedWriter.flush();
//                        bufferedWriter.close();
//                        outputStream.close();
//                        InputStream inputStream = httpURLConnection.getInputStream();
//                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//                        String result = "", line = "";
//                        while ((line = bufferedReader.readLine()) != null) {
//                            result += line;
//                        }
//                        return result;
//                    } catch (Exception e) {
//                        return e.toString();
//                    }

                    return "";
                }
            }

            //creating asynctask object and executing it
            GetCartItemCount catItemObj = new GetCartItemCount();
            catItemObj.execute(sp.getString("loginid", null));
        }

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.cart) {


            if (sp.getString("loginid", null) != null) {
                Intent i = new Intent(this, MyCart.class);
                startActivity(i);
                return true;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Category_wise_products.this);
                builder.setTitle("Heyy..")
                        .setMessage("To see your cart you have to login first. Do you want to login ")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Category_wise_products.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No Just Continue ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setCancelable(false);
                builder.show();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        invalidateOptionsMenu();

    }

    @Override
    public void onAddProduct() {
        cart_count++;
        invalidateOptionsMenu();

    }

    @Override
    public void onRemoveProduct() {
        cart_count--;
        invalidateOptionsMenu();
    }
}
