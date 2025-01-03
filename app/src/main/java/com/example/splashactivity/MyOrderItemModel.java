package com.example.splashactivity;

import com.google.firebase.Timestamp;

import java.util.Date;

public class MyOrderItemModel {
    private String productId;
    private String productTitle;
    private String productImage;
    private String orderStatus;
    private String address;
    private String coupenId;
    private String cuttedPrice;
    private Timestamp orderedDate;
    private Timestamp packedDate;
    private Timestamp shippedDate;
    private Timestamp deliveredDate;
    private Timestamp cancelledDate;
    private String discountedPrice;
    private Long freeCoupens;
    private String fullName;
    private String orderID;
    private String paymentMethod;
    private String pincode;
    private String productPrice;
    private Long productQuantity;
    private String userID;
    private String deliveryPrice;

    private int rating=0;
    private  boolean cancellationRequested;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public MyOrderItemModel(String productId, String orderStatus, String address, String coupenId, String cuttedPrice, Timestamp orderedDate, Timestamp packedDate, Timestamp shippedDate, Timestamp deliveredDate, Timestamp cancelledDate, String discountedPrice, Long freeCoupens, String fullName, String orderID, String paymentMethod, String pincode, String productPrice, Long productQuantity, String userID,String deliveryPrice,String productTitle,String productImage,Boolean cancellationRequested) {
        this.productId = productId;
        this.orderStatus = orderStatus;
        this.address = address;
        this.coupenId = coupenId;
        this.cuttedPrice = cuttedPrice;
        this.orderedDate = orderedDate;
        this.packedDate = packedDate;
        this.shippedDate = shippedDate;
        this.deliveredDate = deliveredDate;
        this.cancelledDate = cancelledDate;
        this.discountedPrice = discountedPrice;
        this.freeCoupens = freeCoupens;
        this.fullName = fullName;
        this.orderID = orderID;
        this.paymentMethod = paymentMethod;
        this.pincode = pincode;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.userID = userID;
        this.deliveryPrice=deliveryPrice;
        this.productTitle=productTitle;
        this.productImage=productImage;
        this.cancellationRequested=cancellationRequested;
    }

    public boolean isCancellationRequested() {
        return cancellationRequested;
    }

    public void setCancellationRequested(boolean cancellationRequested) {
        this.cancellationRequested = cancellationRequested;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCoupenId() {
        return coupenId;
    }

    public void setCoupenId(String coupenId) {
        this.coupenId = coupenId;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    public Timestamp getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Timestamp orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Timestamp getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(Timestamp packedDate) {
        this.packedDate = packedDate;
    }

    public Timestamp getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Timestamp shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Timestamp getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Timestamp deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public Timestamp getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Timestamp cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public Long getFreeCoupens() {
        return freeCoupens;
    }

    public void setFreeCoupens(Long freeCoupens) {
        this.freeCoupens = freeCoupens;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public Long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}