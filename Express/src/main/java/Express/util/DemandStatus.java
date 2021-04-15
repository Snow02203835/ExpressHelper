package Express.util;

public enum DemandStatus {

    UNPAID((byte)0, "待支付"),
    EXPECTING((byte)1, "待接单"),
    PICKED((byte)2, "已接单"),
    SATISFY((byte)3, "已完成");

    private Byte code;
    private String message;

    DemandStatus(Byte code, String message){
        this.code = code;
        this.message = message;
    }

    public Byte getCode() {
        return code;
    }
}
