package Gateway.util;

/**
 * 返回的错误码
 * @author Ming Qiu
 */
public enum ResponseCode {
    /***************************************************
     *    系统级错误
     **************************************************/
    URL_OUT_SCOPE(403, "无权访问该url"),
    //所有需要登录才能访问的API都可能会返回以下错误
    AUTH_INVALID_JWT(501,"JWT不合法"),
    AUTH_NEED_LOGIN(704, "需要先登录");

    private final int code;
    private final String message;
    ResponseCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage(){
        return message;
    }

}
