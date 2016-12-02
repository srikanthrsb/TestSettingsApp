package srikanthtuts.testsettingsapp;

/**
 * Created by Buchale.Reddy on 02-12-2016.
 */
public class CustomerOrders {

    String orderid;
    String orderdate;
    String storetype;
    String storeemailid;
    String userid;
    String productsku;
    String productname;
    String productdesc;
    String productimage;
    String oldprice;
    String newprice;
    String tax;
    String pricematch;
    String pmdate;

    public CustomerOrders() {
    }

    public CustomerOrders(String orderid, String orderdate, String storetype, String storeemailid, String userid, String productsku, String productname, String productdesc,
                          String productimage, String oldprice, String newprice, String tax, String pricematch, String pmdate) {
        this.orderid = orderid;
        this.orderdate = orderdate;
        this.storetype = storetype;
        this.storeemailid = storeemailid;
        this.userid = userid;
        this.productsku = productsku;
        this.productname = productname;
        this.productdesc = productdesc;
        this.productimage = productimage;
        this.oldprice = oldprice;
        this.newprice = newprice;
        this.tax = tax;
        this.pricematch = pricematch;
        this.pmdate = pmdate;
    }


    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getOrderdate() {
        return orderdate;
    }

    public void setOrderdate(String orderdate) {
        this.orderdate = orderdate;
    }

    public String getStoretype() {
        return storetype;
    }

    public void setStoretype(String storetype) {
        this.storetype = storetype;
    }

    public String getStoreemailid() {
        return storeemailid;
    }

    public void setStoreemailid(String storeemailid) {
        this.storeemailid = storeemailid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getProductsku() {
        return productsku;
    }

    public void setProductsku(String productsku) {
        this.productsku = productsku;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductdesc() {
        return productdesc;
    }

    public void setProductdesc(String productdesc) {
        this.productdesc = productdesc;
    }

    public String getProductimage() {
        return productimage;
    }

    public void setProductimage(String productimage) {
        this.productimage = productimage;
    }

    public String getOldprice() {
        return oldprice;
    }

    public void setOldprice(String oldprice) {
        this.oldprice = oldprice;
    }

    public String getNewprice() {
        return newprice;
    }

    public void setNewprice(String newprice) {
        this.newprice = newprice;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getPricematch() {
        return pricematch;
    }

    public void setPricematch(String pricematch) {
        this.pricematch = pricematch;
    }

    public String getPmdate() {
        return pmdate;
    }

    public void setPmdate(String pmdate) {
        this.pmdate = pmdate;
    }
}
