package Express.util;

public enum OrderStatus {

    PICKED((byte)0, "已接单"),
    COLLECTED((byte)1, "已取件"),

    SENT((byte)8, "已送达"),
    SATISFY((byte)9, "已完成");

    private Byte code;
    private String message;

    OrderStatus(Byte code, String message){
        this.code = code;
        this.message = message;
    }

    public Byte getCode() {
        return code;
    }
}
