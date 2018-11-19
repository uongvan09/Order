package com.rtsoftware.order.model.tranfer;

public class TranferConfirmToMain {
    boolean isSuccess;

    public TranferConfirmToMain(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
