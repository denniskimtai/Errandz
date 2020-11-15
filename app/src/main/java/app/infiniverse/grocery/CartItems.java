package app.infiniverse.grocery;

public class CartItems {


    public String product_id;
    public String product_name;
    public String product_description;
    public String product_price;
    public String product_selling_price;
    public String product_short_description;
    public String product_discount;
    public String product_image_url;
    public String product_quantity;

    public CartItems() {
    }

    public CartItems(String product_id, String product_name, String product_description, String product_price,
               String product_selling_price, String product_short_description, String product_discount, String product_image_url, String product_quantity){

        this.product_id = product_id;
        this.product_name = product_name;
        this.product_description =product_description;
        this.product_price = product_price;
        this.product_selling_price = product_selling_price;
        this.product_short_description = product_short_description;
        this.product_discount = product_discount;
        this.product_image_url = product_image_url;
        this.product_quantity = product_quantity;

    }


    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_selling_price() {
        return product_selling_price;
    }

    public void setProduct_selling_price(String product_selling_price) {
        this.product_selling_price = product_selling_price;
    }

    public String getProduct_short_description() {
        return product_short_description;
    }

    public void setProduct_short_description(String product_short_description) {
        this.product_short_description = product_short_description;
    }

    public String getProduct_discount() {
        return product_discount;
    }

    public void setProduct_discount(String product_discount) {
        this.product_discount = product_discount;
    }

    public String getProduct_image_url() {
        return product_image_url;
    }

    public void setProduct_image_url(String product_image_url) {
        this.product_image_url = product_image_url;
    }

    public String getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(String product_quantity) {
        this.product_quantity = product_quantity;
    }
}
