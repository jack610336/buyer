package com.jack.buy4u.client;


import android.text.TextUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jack_Wang on 2018/1/14.
 */

public class Item implements Serializable{

    public static final int Version = 2;
    public static final String TABLE = "items";

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private int id;
    private int groupId;
    private String name;
    private String description;
    private String photoPath;
    private String photoUrl;
    private Date start;
    private Date end;
    private int price;
    private int qty;
    private String priceString;
    private String starFormatted;
    private String endFormatted;
    private String qtyString;

    public String getQtyString() {
        if (qty != 0) {
            qtyString = qty+"";
        }
        return qtyString;

    }

    public void setQtyString(String qtyString) {
        if (TextUtils.isEmpty(qtyString)){
            qtyString = "0";
        }
        this.qtyString = qtyString;
        if (!TextUtils.isEmpty(qtyString)){
            qty = Integer.parseInt(qtyString);
        }

    }

    public String getPriceString() {
        priceString = String.valueOf(price);
        return priceString;
    }

    public void setPriceString(String priceString) {
        this.priceString = priceString;

        try {
            price = Integer.parseInt(priceString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            price = 0;
        }

    }


    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", photoPath='" + photoPath + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", price=" + price +
                ", qty=" + qty +
                '}';
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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

    public String getStarFormatted() {
        starFormatted = sdf.format(getStart());
        return starFormatted;
    }

    public void setStarFormatted(String starFormatted) {
        this.starFormatted = starFormatted;
    }

    public Date getStart() {
        if (start == null) {
            if (TextUtils.isEmpty(starFormatted)) {
                start = new Date();
            } else {
                try {
                    start = sdf.parse(starFormatted);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getEndFormatted() {
        endFormatted = sdf.format(getEnd());
        return endFormatted;
    }

    public void setEndFormatted(String endFormatted) {
        this.endFormatted = endFormatted;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
