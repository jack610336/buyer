package com.jack.buy4u.client;

/**
 * Created by Jack_Wang on 2018/1/28.
 */

public class Order {

    String uid;
    int itemId;
    int groupId;
    int qty;
    String itemName;


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Order(){

    }

    public Order(String uid, int itemId, int groupId, int qty , String itemName) {
        this.uid = uid;
        this.itemId = itemId;
        this.groupId = groupId;
        this.qty = qty;
        this.itemName = itemName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
