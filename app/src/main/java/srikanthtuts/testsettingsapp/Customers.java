package srikanthtuts.testsettingsapp;

/**
 * Created by Buchale.Reddy on 02-12-2016.
 */
public class Customers {

    String userid;
    String password;
    String cxname;
    String phone;
    String address;
    String lastlogin;

    public Customers() {
    }

    public Customers(String userid, String password, String cxname, String phone, String address, String lastlogin) {
        this.userid = userid;
        this.password = password;
        this.cxname = cxname;
        this.phone = phone;
        this.address = address;
        this.lastlogin = lastlogin;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCxname() {
        return cxname;
    }

    public void setCxname(String cxname) {
        this.cxname = cxname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(String lastlogin) {
        this.lastlogin = lastlogin;
    }
}
