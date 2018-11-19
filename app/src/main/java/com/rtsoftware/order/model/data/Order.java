package com.rtsoftware.order.model.data;

import java.io.FileOutputStream;
import java.util.Map;

public class Order {
    /*
    name: order
    folder: orderId
    */
    String orderId;
    String tableId;

    // F002_>_F003
    String lstOrderId;

    /*
    status of order
        0: tạo, chỉnh sửa
        1: Đang đợi
        2: Đang xử lý
        3: Hoàn thành(Khách hàng sử dụng)
        4: Kết thúc
     */
    int status;

    //time Create or modifi
    long time;

    /*
    list food of order
    folder: foodId
    */
    Map<String, FoodInOrder> listOrder;

    //Tổng giá tiền
    int totalPrice;

    //id employee order
    String orderer;

    public Order(String orderId, String tableId, int status, long time, String lstOrderId, Map<String, FoodInOrder> listOrder,
                 int totalPrice, String orderer) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.status = status;
        this.time = time;
        this.lstOrderId = lstOrderId;
        this.listOrder = listOrder;
        this.totalPrice = totalPrice;
        this.orderer = orderer;
    }

    public Order() {
    }

    public String getLstOrderId() {
        return lstOrderId;
    }

    public void setLstOrderId(String lstOrderId) {
        this.lstOrderId = lstOrderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Map<String, FoodInOrder> getListOrder() {
        return listOrder;
    }

    public void setListOrder(Map<String, FoodInOrder> listOrder) {
        this.listOrder = listOrder;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderer() {
        return orderer;
    }

    public void setOrderer(String orderer) {
        this.orderer = orderer;
    }

    public static class FoodInOrder {
        String foodId;
        String foodName;
        int foodFrice;
        String foodUnit;
        //Số lượng
        int quantity;

        /*
        Trạng thái món ăn
        1: đang xử lý
        2: hoàn thành chế biến
        3: kết thúc(đã giao đến bàn)
         */
        int status;


        public FoodInOrder() {
        }

        public FoodInOrder(String foodId, String foodName, int foodFrice, String foodUnit, int quantity, int status) {
            this.foodId = foodId;
            this.foodName = foodName;
            this.foodFrice = foodFrice;
            this.foodUnit = foodUnit;
            this.quantity = quantity;
            this.status = status;
        }

        public String getFoodName() {
            return foodName;
        }

        public void setFoodName(String foodName) {
            this.foodName = foodName;
        }

        public String getFoodId() {
            return foodId;
        }

        public void setFoodId(String foodId) {
            this.foodId = foodId;
        }

        public int getFoodFrice() {
            return foodFrice;
        }

        public void setFoodFrice(int foodFrice) {
            this.foodFrice = foodFrice;
        }

        public String getFoodUnit() {
            return foodUnit;
        }

        public void setFoodUnit(String foodUnit) {
            this.foodUnit = foodUnit;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

}
