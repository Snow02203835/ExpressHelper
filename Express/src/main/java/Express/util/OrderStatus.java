package Express.util;

public enum OrderStatus {

    PICKED((byte)0, "已接单"),
    COLLECTED((byte)1, "已取件"),
    SENT((byte)2, "已送达"),
    CANCEL((byte)3, "已取消"),
    BEEN_CANCEL((byte)4, "被取消"),
    SATISFY((byte)9, "已完成");

    private final Byte code;
    private String message;

    OrderStatus(Byte code, String message){
        this.code = code;
        this.message = message;
    }

    public Byte getCode() {
        return code;
    }
}
