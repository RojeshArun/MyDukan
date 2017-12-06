package org.app.mydukan.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


@SuppressWarnings("serial")
public class Order extends Object implements Serializable {

    private String orderId = "";
    private OrderInfo orderinfo = null;
    private HashMap<String,OrderProduct> productlist = new HashMap<>();

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String id) {
        this.orderId = id;
    }

    public OrderInfo getOrderInfo(){return orderinfo;}

    public HashMap<String,OrderProduct> getProductList() {
        return productlist;
    }

    public void setProductList(HashMap<String,OrderProduct> productList) {
        this.productlist = productList;
    }
}
