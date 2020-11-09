package app.infiniverse.grocery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by root on 24/3/18.
 */

public class Recent_Products_Adapter extends RecyclerView.Adapter<Recent_Products_Adapter.ProductsViewHolder> {

    public static final String PREFS = "PREFS";
    Context context;
    SharedPreferences sp;
    private String[] product_id;
    private String[] product_name;
    private String[] product_desc;
    private String[] product_img;
    private String[] product_price;
    private String[] product_brand;
    private String[] product_sp;
    private String[] product_dp;

    private String GET_IMAGE_URL = "http://errandz.xyz/denniskimtai1/get_product_image.php";
    String image_url;

    public Recent_Products_Adapter(String[] product_id, String[] product_name, String[] product_desc, String[] product_img, String[] product_price, String[] product_brand, String[] product_sp, String[] product_dp, Context context) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_desc = product_desc;
        this.product_img = product_img;
        this.product_price = product_price;
        this.product_brand = product_brand;
        this.product_sp = product_sp;
        this.product_dp = product_dp;
        this.context = context;
    }


    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_recent_products, parent, false);
        return new ProductsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ProductsViewHolder holder, int position) {
        String id = product_id[position];
        String name = product_name[position];
        String desc = product_desc[position];
        final String img = product_img[position];
        String price = product_price[position];
        String selling_price = product_sp[position];
        String brand = product_brand[position];
        String discount = product_dp[position];

        holder.pro_id.setText(id);
        holder.pro_name.setText(name);
        holder.pro_desc.setText(desc);
        holder.pro_price.setText("Ksh " + price);
        holder.pro_sp.setText("Ksh " + selling_price);
        holder.pro_brand.setText(brand);
        holder.pro_discount.setText(discount + " %   OFF");

        if (Integer.parseInt(discount) <= 0) {
            holder.pro_discount.setVisibility(View.GONE);
        }
        if(selling_price.trim().equals(price.trim())){
            holder.pro_price.setVisibility(View.GONE);
        }

        //get image url
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_IMAGE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String s1 = response.replace("\\", "");

                image_url = "http://www.errandz.xyz/wp/wp-content/uploads/" + s1.replace("\"", "");

                Picasso.with(context).load(image_url).into(holder.pro_img);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            //send params needed to db
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                    params.put("image_id", img);


                return params;

            }
        };

        Volley.newRequestQueue(context).add(stringRequest);

    }

    @Override
    public int getItemCount() {
        return product_id.length;
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder {

        TextView pro_id;
        TextView pro_name;
        TextView pro_desc;
        TextView pro_price;
        TextView pro_sp;
        TextView pro_brand;
        TextView pro_discount;
        ImageView pro_img;
        LinearLayout layout;


        public ProductsViewHolder(final View itemView) {

            super(itemView);
            pro_id = itemView.findViewById(R.id.product_id);
            pro_name = itemView.findViewById(R.id.product_name);
            pro_desc = itemView.findViewById(R.id.product_short_desc);
            pro_img = itemView.findViewById(R.id.product_img);
            pro_price = itemView.findViewById(R.id.product_price);
            pro_sp = itemView.findViewById(R.id.selling_price);
            pro_brand = itemView.findViewById(R.id.brand_name);
            pro_discount = itemView.findViewById(R.id.discount);
            strikeThroughText(pro_price);
            layout = itemView.findViewById(R.id.product_card);

            sp = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    builder.setMessage("id " + pro_id.getText().toString() + "\n"
//                            + "name " + pro_name.getText().toString() + "\n"
//                            + "desc " + pro_desc.getText().toString() + "\n"
//                            + "price " + pro_price.getText().toString() + "\n"
//                            + "sp " + pro_sp.getText().toString() + "\n"
//                            + "short desc " + pro_brand.getText().toString() + "\n"
//                            + "discount " + pro_discount.getText().toString());
//
//                    builder.show();

                    //go to product detail activity with product details
                    Intent intent = new Intent(context, ProductDetailActivity.class);

                    intent.putExtra("product_id", pro_id.getText().toString());
                    intent.putExtra("product_name", pro_name.getText().toString());
                    intent.putExtra("product_description", pro_desc.getText().toString());
                    intent.putExtra("product_price", pro_price.getText().toString());
                    intent.putExtra("product_selling_price", pro_sp.getText().toString());
                    intent.putExtra("product_short_description", pro_brand.getText().toString());
                    intent.putExtra("product_discount", pro_discount.getText().toString());
                    intent.putExtra("product_image", image_url);

                    context.startActivity(intent);

                }
            });
            pro_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //go to product detail activity with product details
                    Intent intent = new Intent(context, ProductDetailActivity.class);

                    intent.putExtra("product_id", pro_id.getText().toString());
                    intent.putExtra("product_name", pro_name.getText().toString());
                    intent.putExtra("product_description", pro_desc.getText().toString());
                    intent.putExtra("product_price", pro_price.getText().toString());
                    intent.putExtra("product_selling_price", pro_sp.getText().toString());
                    intent.putExtra("product_short_description", pro_brand.getText().toString());
                    intent.putExtra("product_discount", pro_discount.getText().toString());
                    intent.putExtra("product_image", image_url);

                    context.startActivity(intent);
                }
            });
            pro_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //go to product detail activity with product details
                    Intent intent = new Intent(context, ProductDetailActivity.class);

                    intent.putExtra("product_id", pro_id.getText().toString());
                    intent.putExtra("product_name", pro_name.getText().toString());
                    intent.putExtra("product_description", pro_desc.getText().toString());
                    intent.putExtra("product_price", pro_price.getText().toString());
                    intent.putExtra("product_selling_price", pro_sp.getText().toString());
                    intent.putExtra("product_short_description", pro_brand.getText().toString());
                    intent.putExtra("product_discount", pro_discount.getText().toString());
                    intent.putExtra("product_image", image_url);

                    context.startActivity(intent);

                }
            });


        }

        private void strikeThroughText(TextView price) {
            price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


    }

}