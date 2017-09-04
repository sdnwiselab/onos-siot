package com.github.sdnwiselab.siot;


public class SiotChannel {
    public String name;
    public String description;
    public String model;
    public String brand;
    public String device_type;
    public String bt_mac_address;
    public String wf_mac_address;
    public String mobility_status;
    public String location;
    public String location_name;
    public String aut_ind_loc0;
    public String public_flag0;
    public String oor_flag0;
    public String clor_flag0;
    public String por_flag0;
    public String cwor_flag0;
    public String sor_flag0;
    public String aut_ind_loc1;
    public String public_flag1;
    public String oor_flag1;
    public String clor_flag1;
    public String por_flag1;
    public String cwor_flag1;
    public String sor_flag1;
    public String field1;
    public String field2;
    public String field3;
    public String field4;
    public String field5;
    public String field6;
    public String field7;
    public String field8;
    public String field9;
    public String field10;
    public String field11;
    public String field12;
    public String field13;
    public String field14;
    public String field15;
    public String field16;

    public SiotChannel(String id, String macAddress) {

        this.name = id;
        this.description = "";
        this.model = "";
        this.brand = "";
        this.device_type = "";
        this.bt_mac_address = "";
        this.wf_mac_address = macAddress;
        this.mobility_status = "0";
        this.location = "";
        this.location_name = "";
        this.aut_ind_loc0 = "0";
        this.aut_ind_loc1 = "1";
        this.public_flag0 = "0";
        this.public_flag1 = "1";
        this.oor_flag0 = "0";
        this.oor_flag1 = "1";
        this.clor_flag0 = "0";
        this.clor_flag1 = "1";
        this.por_flag0 = "0";
        this.por_flag1 = "1";
        this.cwor_flag0 = "0";
        this.cwor_flag1 = "1";
        this.sor_flag0 = "0";
        this.sor_flag1 = "1";
        this.field1 = "";
        this.field2 = "";
        this.field3 = "";
        this.field4 = "";
        this.field5 = "";
        this.field6 = "";
        this.field7 = "";
        this.field8 = "";
        this.field9 = "";
        this.field10 = "";
        this.field11 = "";
        this.field12 = "";
        this.field13 = "";
        this.field14 = "";
        this.field15 = "";
        this.field16 = "";
    }


    public String getUrlParameters() {
        String urlParameters = "&channel[name]=" + this.name + "&channel[description]=" + this.description +
                "&channel[model]=" + this.model + "&channel[brand]=" + this.brand + "&channel[device_type]=" + this.device_type +
                "&channel[bt_mac_address]=" + this.bt_mac_address + "&channel[wf_mac_address]=" + this.wf_mac_address +
                "&channel[mobility_status]=" + this.mobility_status + "&channel[location]=" + this.location +
                "&channel[location_name]=" + this.location_name + "&channel[aut_ind_loc]=" + this.aut_ind_loc0 +
                "&channel[aut_ind_loc]=" + this.aut_ind_loc1 + "&channel[public_flag]=" + this.public_flag0 +
                "&channel[public_flag]=" + this.public_flag1 + "&channel[oor_flag]=" + this.oor_flag0 +
                "&channel[oor_flag]=" + this.oor_flag1 + "&channel[clor_flag]=" + this.clor_flag0 +
                "&channel[clor_flag]=" + this.clor_flag1 + "&channel[por_flag]=" + this.por_flag0 +
                "&channel[por_flag]=" + this.por_flag1 + "&channel[cwor_flag]=" + this.cwor_flag0 +
                "&channel[cwor_flag]=" + this + cwor_flag1 + "&channel[sor_flag]=" + this.sor_flag0 + "&channel[sor_flag]=" +
                this.sor_flag1 + "&channel[field1]=" + this.field1 + "&channel[field2]=" + this.field2 + "&channel[field3]=" +
                this.field3 + "&channel[field4]=" + this.field4 + "&channel[field5]=" + this.field5 +
                "&channel[field6]=" + this.field6 + "&channel[field7]=" + this.field7 + "&channel[field8]=" + this.field8 +
                "&channel[field9]=" + this.field9 + "&channel[field10]=" + this.field10 + "&channel[field11]=" + this.field11 +
                "&channel[field12]=" + this.field12 + "&channel[field13]=" + this.field13 + "&channel[field14]=" + this.field14 +
                "&channel[field15]=" + this.field15 + "&channel[field16]=" + this.field16;
        return urlParameters;

    }
}




