package com.kc.apollo.model;

/**
 * Created by lijunying on 17/1/18.
 */
public class PriceModel {

    private String brand;
    private String agencyPrice;
    private String sellPrice;
    private String barCode;

    public PriceModel(){

    }

    public PriceModel(String brand, String agencyPrice, String sellPrice, String barCode) {
        this.brand = brand;
        this.agencyPrice = agencyPrice;
        this.sellPrice = sellPrice;
        this.barCode = barCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAgencyPrice() {
        return agencyPrice;
    }

    public void setAgencyPrice(String agencyPrice) {
        this.agencyPrice = agencyPrice;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }
}
