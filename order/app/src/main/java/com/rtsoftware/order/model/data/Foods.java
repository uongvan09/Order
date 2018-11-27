package com.rtsoftware.order.model.data;

public class Foods {
    public static final String CLASSIFY_MON_CHINH="Món chính";
    public static final String CLASSIFY_DIEM_TAM="Điểm tâm";
    public static final String CLASSIFY_AN_VAT="Ăn vặt";
    public static final String CLASSIFY_TRANG_MIENG="Tráng miệng";
    public static final String CLASSIFY_NUOC_UONG="Nước uống";
    public static final String UNIT_BAT="bát";
    public static final String UNIT_COC="cốc";
    public static final String UNIT_CAI="cái";
    public static final String UNIT_CHAI="chai";
    public static final String UNIT_XUAT="xuất";
    public static final String UNIT_CON="con";
//    name: food
//    folder: foodId

    String foodId;
    String foodName;
    String foodImage;
    String foodInfo;
    int foodPrice;

    /*
    Phân nhóm món ăn
    Ăn vặt
    Điểm tâm
    Món chính
    Tráng miệng
    Nước uống
     */
    String foodClassify;

    /*
    Đơn vị tính
    Bát
    Cốc
    Chai
    Xuất
    Con
     */
    String foodUnit;

    public Foods() {
    }

    public Foods(String foodId, String foodName, String foodImage, String foodInfo,
                 int foodPrice, String foodClassify, String foodUnit) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodImage = foodImage;
        this.foodInfo = foodInfo;
        this.foodPrice = foodPrice;
        this.foodClassify=foodClassify;
        this.foodUnit= foodUnit;
    }

    public String getFoodUnit() {
        return foodUnit;
    }

    public void setFoodUnit(String foodUnit) {
        this.foodUnit = foodUnit;
    }

    public String getFoodClassify() {
        return foodClassify;
    }

    public void setFoodClassify(String foodClassify) {
        this.foodClassify = foodClassify;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public String getFoodInfo() {
        return foodInfo;
    }

    public void setFoodInfo(String foodInfo) {
        this.foodInfo = foodInfo;
    }

    public int getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(int foodPrice) {
        this.foodPrice = foodPrice;
    }
}
