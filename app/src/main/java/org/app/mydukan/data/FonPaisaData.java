package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by Shivayogi Hiremath on 10/08/2017.
 */



/*
{
        "amount": "51.0",
        "payment_detail": "PAYTM,",
        "error_msg": null,
        "addnl_detail": "MYDUKAN_UserID:jaXVRe8e9Wg6tlZDpnfz5iVIE1d2||User_MobileNO:7760902997||User_EmailId:shivayogih72@gmail.com",
        "sign": "6757c6cdf85c842208b3fcc8def1955f8e4ad4ecd4bc199fae27755529df98b23ce330cb2a773892603c069bee3af37ddfd52cc45f0f6b4e3b852cb80524bf379438aeade35e55c318cfddc33c8bf3959c6e5f407c8f41bf92268e3ef1f2ad0406da77bb2e13ddd849659cdc12e8bf857277c609e091cfcc7cff02f04003226ccec7819b91e80b5adf13478d7e5f99068887550fb98df60502b73c4853cc88c514d40167ee7a1cff5b44b85bda3a48f31e15a9f82eb170a5843ced11ac6e64f28daa6ceea03609a7b5871ab130b6335b0cbb93b268e6516b17a1ceb6b8bb7110d93c9a6a1b8e31900120c3e11737d796924220e82e7455d3f3a52249adc55bd1",
        "merchant_id": "FPTEST",
        "payment_reference": "170810091637wqf",
        "error": null,
        "payment_type": "Other Wallet",
        "id": "FPTEST",
        "invoice": "be3c342f84994f0ab821736a3068e489",
        "merchant_display": "fonepaisa",
        "status": "S"
        }

*/



public class FonPaisaData implements Serializable {

    String status;
    String amount;
    String invoice;
    String error_msg;
    String addnl_detail;
    String id;
    String merchant_id;
    String payment_type;
    String merchant_display;
    String error;
    String payment_detail;
    String sign;
    String payment_reference;


    public FonPaisaData() {
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayment_detail() {
        return payment_detail;
    }

    public void setPayment_detail(String payment_detail) {
        this.payment_detail = payment_detail;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getAddnl_detail() {
        return addnl_detail;
    }

    public void setAddnl_detail(String addnl_detail) {
        this.addnl_detail = addnl_detail;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getPayment_reference() {
        return payment_reference;
    }

    public void setPayment_reference(String payment_reference) {
        this.payment_reference = payment_reference;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoice() {
        return invoice;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public String getMerchant_display() {
        return merchant_display;
    }

    public void setMerchant_display(String merchant_display) {
        this.merchant_display = merchant_display;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



}
