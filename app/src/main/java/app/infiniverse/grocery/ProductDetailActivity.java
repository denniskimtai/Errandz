package app.infiniverse.grocery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String PREFS = "PREFS";
    TextView tvProductName, tvProductDesc, tvMRP, tvPrice, tvSaved, tvSavedPer, tvQty, addtocart, product_id, buy_now, product_short_desc;
    ImageView ivProductImage, add, remove;
    String p_id;
    SharedPreferences sp;
    ScrollView view;
    private ProgressBar mProgressBar;
    AddToCart addToCart;

    private String string_product_id, product_name, product_description, product_price, product_selling_price, product_short_description, product_discount, product_image_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        //get strings from intent
        string_product_id = getIntent().getStringExtra("product_id");
        product_name = getIntent().getStringExtra("product_name");
        product_description = getIntent().getStringExtra("product_description");
        product_price = getIntent().getStringExtra("product_price");
        product_selling_price = getIntent().getStringExtra("product_selling_price");
        product_short_description = getIntent().getStringExtra("product_short_description");
        product_discount = getIntent().getStringExtra("product_discount");
        product_image_url = getIntent().getStringExtra("product_image");

        view = findViewById(R.id.product_page);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        addToCart=new AddToCart(ProductDetailActivity.this);
//        Bundle extras = getIntent().getExtras();
//        p_id = extras.getString("p_id");

        sp = getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        addtocart = findViewById(R.id.addtocart);
        product_id = findViewById(R.id.product_id);

        tvProductName = findViewById(R.id.product_name);
        tvProductDesc = findViewById(R.id.product_desc);
        tvMRP = findViewById(R.id.mrp);
        tvPrice = findViewById(R.id.price);
        tvQty = findViewById(R.id.product_qty);
        ivProductImage = findViewById(R.id.product_img);
        add = findViewById(R.id.add);
        remove = findViewById(R.id.remove);
        buy_now = findViewById(R.id.buynow);
        product_short_desc = findViewById(R.id.product_short_desc);
        tvMRP.setPaintFlags(tvMRP.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


        final int[] number = {1};
//        tvQty.setText("" + number[0]);

        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginid = sp.getString("loginid", null);
                if (loginid != null) {
                    addToCart.addToCart(product_id.getText().toString(),"1");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
                    builder.setTitle("Heyy..")
                            .setMessage("To add this item in your cart you have to login first. Do you want to login ")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent(ProductDetailActivity.this,LoginActivity.class);
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
        });
        buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginid = sp.getString("loginid", null);
                if (loginid != null) {
                    addToCart.addToCart(product_id.getText().toString(),"1");
                    Intent intent = new Intent(getApplicationContext(), MyCart.class);
                    startActivity(intent);
                    finish();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
                    builder.setTitle("Heyy..")
                            .setMessage("To add this item in your cart you have to login first. Do you want to login ")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent=new Intent(ProductDetailActivity.this,LoginActivity.class);
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
        });

        //set product values to text views
        tvProductName.setText(product_name);
        tvProductDesc.setText(product_description);
        tvMRP.setText(product_price);
        tvPrice.setText(product_selling_price);
        product_short_desc.setText(product_short_description);
        Picasso.with(ProductDetailActivity.this).load(product_image_url).into(ivProductImage);


//        double p_mrp = Double.parseDouble(product_price);
//        double p_sp = Double.parseDouble(product_selling_price);
//        double p_dp = (p_mrp - p_sp);
//        double p_dp_p = p_dp / (p_mrp / 100);

        mProgressBar.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);


//        ProductDetail productDetail = new ProductDetail();
//        productDetail.execute();

    }

    class ProductDetail extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String productUrl = getResources().getString(R.string.base_url) + "getProductDetail/" + p_id;

            try {
                URL url = new URL(productUrl);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String result = "", line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                return result;
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
            builder.setTitle("Received Message");

            try {

                JSONArray productDetailsArray = new JSONArray(s);
                JSONObject json_data = new JSONObject();
                json_data = productDetailsArray.getJSONObject(0);

                tvProductName.setText(json_data.getString("name"));
                tvProductDesc.setText(json_data.getString("description"));
                tvMRP.setText("Ksh " + json_data.getString("mrp"));
                tvPrice.setText("Ksh " + json_data.getString("selling_price"));
                product_id.setText(json_data.getString("id"));
                Picasso.with(ProductDetailActivity.this).load(getResources().getString(R.string.img_base_url) + "product_images/" + json_data.getString("image")).placeholder(R.drawable.watermark_icon).into(ivProductImage);

                double p_mrp = Double.parseDouble(json_data.getString("mrp"));
                double p_sp = Double.parseDouble(json_data.getString("selling_price"));
                double p_dp = (p_mrp - p_sp);
                double p_dp_p = p_dp / (p_mrp / 100);

                mProgressBar.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                builder.setCancelable(true);
                builder.setTitle("No Internet Connection");
                builder.setMessage("Please Connect to internet");
                builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


    }

}
