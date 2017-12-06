package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by arpithadudi on 10/10/16.
 */

@SuppressWarnings("serial")
public class OrderProduct_test extends Object implements Serializable {

    private String productid = "";
    private String productname = "";
    private String  price = "0";
    private String quantity = "0";

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductname() {
        return productname;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}