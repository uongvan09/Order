package com.rtsoftware.order.model.data;

public class Table {
//    name table;
//    folder tableId;

    String tableId;
    /*
    Trạng thái bàn
    1: bận
    2: rảnh
     */
    int status;

    //Số lượng người trong bàn
    int quantitySeat;

    public Table(){}

    public Table(String tableId, int status, int quantitySeat) {
        this.tableId = tableId;
        this.status = status;
        this.quantitySeat = quantitySeat;
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

    public int getQuantitySeat() {
        return quantitySeat;
    }

    public void setQuantitySeat(int quantitySeat) {
        this.quantitySeat = quantitySeat;
    }
}
