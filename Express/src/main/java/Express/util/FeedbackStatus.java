package Express.util;

/**
 * @author snow create 2021/04/19 14:46
 */
public enum FeedbackStatus {

    EXPECTING((byte)0, "待受理"),
    HANDLING((byte)1, "处理中"),
    RESPONDED((byte)2, "已答复"),
    CANCEL((byte)3, "已取消");

    private final Byte code;
    private String message;

    FeedbackStatus(Byte code, String message) {
        this.code = code;
        this.message = message;
    }

    public Byte getCode() {
        return code;
    }
}
