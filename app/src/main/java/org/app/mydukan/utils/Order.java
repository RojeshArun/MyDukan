package org.app.mydukan.utils;

/**
 * Created by ksamudra on 10/9/2016.
 */
public class Order {
    private int qty = 0;
    private double unitprice = 0;
    private String productkey = null;
    private String productname = null;

    public void setQuantity(int nos) {
        this.qty = nos;
    }

    public double getUnitPrice() {
        return unitprice;
    }

    public void setUnitPrice(double singleUnitPrice) {
        this.unitprice = singleUnitPrice;
    }

    public int getqty(){return qty;}

    public double getTotalPrice() {
        return unitprice * qty;
    }

    public void setProductID(String firebaseProdKey) {
        this.productkey = firebaseProdKey;
    }
    public String getProductID(){return productkey;}

    public  void setProductName(String myproductname){
       this.productname = myproductname;
    }

    public String getProductname(){return productname;}
}
