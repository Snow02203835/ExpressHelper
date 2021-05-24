package Express.util;

/**
 * @author snow create 2021/05/24 00:30
 */
public enum FeedbackType {

    /**
     * 订单投诉类型
     */
    PACKAGE_DAMAGED((byte)0),       //快递损坏
    DELIVERED_NOT_ON_TIME((byte)1), //未按时送达
    BAD_ATTITUDE((byte)2),          //服务态度差
    PACKAGE_LOST((byte)3),          //快递丢失
    OTHER((byte)9),                 //其他类型

    /**
     * 系统投诉类型
     */
    UNSTABLE_SYSTEM((byte)10),             //系统稳定性差
    ;

    private final Byte code;

    FeedbackType(Byte code){
        this.code = code;
    }

    public Byte getCode(){
        return this.code;
    }
}
