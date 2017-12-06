package org.app.mydukan.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by arpithadudi on 8/17/16.
 */
@SuppressWarnings("serial")
public class Product extends Object implements Serializable {
    private PriceDrop priceDrop;
    private Offer offer;
    private String productId = "";
    private String name = "";
    private String description = "";
    private String url = "";
    private String price = "";
    private String categoryId = "";
    private String subcategory = "";
    private Long stockremaining = 0l;
    private HashMap<String,String> attributes = new HashMap<>();
    private ArrayList<String> subcategoryIds = new ArrayList<>();
    private Boolean isnew = false;

    private String dp = "";
    private String mop = "";
    private String mrp = "";

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getMop() {
        return mop;
    }

    public int getMopInt(){
        if(this.mop.equals("") || this.mop.equals("_")){
            return 0;
        }
        return (int)Double.parseDouble(this.mop);
    }

    public void setMop(String mop) {
        this.mop = mop;
    }

    public String getDp() {
        return dp;
    }
    public int getDpInt(){
        if(this.dp.equals("") || this.dp.equals("_")){
            return 0;
        }
        return (int)Double.parseDouble(this.dp);
    }
    public void setDp(String dp) {
        this.dp = dp;
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPriceInt() {
        return Integer.parseInt(price);
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price =price;
    }
    // todo setMOP, MRP

    public void setIsnew(boolean isnew) {
        this.isnew = isnew;
    }

    public boolean getIsnew() {
        return isnew;
    }

    public PriceDrop getPriceDrop() {
        return priceDrop;
    }

    public void setPriceDrop(PriceDrop priceDrop) {
        this.priceDrop = priceDrop;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String id) {
        this.categoryId = id;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }

    public HashMap<String,String> getAttributes() {
        return attributes;
    }

    public void setAttributes(HashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public void setStockremaining(Long stockremaining) {
        this.stockremaining = stockremaining;
    }

    public Long getStockremaining() {
        return stockremaining;
    }

    public void setSubcategoryIds(ArrayList<String> subcategoryIds) {
        this.subcategoryIds = subcategoryIds;
    }

    public ArrayList<String> getSubcategoryIds() {
        return subcategoryIds;
    }
}
