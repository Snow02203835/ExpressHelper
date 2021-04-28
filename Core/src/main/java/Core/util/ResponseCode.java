package Core.util;

/**
 * 返回的错误码
 * @author Ming Qiu
 */
public enum ResponseCode {
    OK(0,"成功"),
    /***************************************************
     *    系统级错误
     **************************************************/
    INTERNAL_SERVER_ERR(500,"服务器内部错误"),
    //所有需要登录才能访问的API都可能会返回以下错误
    AUTH_INVALID_JWT(501,"JWT不合法"),
    AUTH_JWT_EXPIRED(502,"JWT过期"),

    //以下错误码提示可以自行修改
    //--------------------------------------------
    FIELD_NOT_VALID(503,"字段不合法"),
    //所有路径带id的API都可能返回此错误
    RESOURCE_ID_NOT_EXIST(504,"操作的资源id不存在"),
    RESOURCE_ID_OUT_SCOPE(505,"操作的资源id不是自己的对象"),
    FILE_NO_WRITE_PERMISSION(506,"目录文件夹没有写入的权限"),
    RESOURCE_FALSIFY(507, "信息签名不正确"),

    /***************************************************
     *    权限模块错误码
     **************************************************/
    BILL_TYPE_ERROR(600, "支付方式不存在"),
    BILL_ID_DO_NOT_EXIST(601, "支付单号不存在"),
    BILL_UNPAID(602, "待支付"),
    BILL_PAID(603, "已支付"),
    BILL_CANCEL(604, "已取消"),
    BILL_WITHDRAW(605, "已退款"),
    BILL_OUT_SCOPE(606, "订单不属于该需求"),

    /***************************************************
     *    权限模块错误码
     **************************************************/
    AUTH_INVALID_ACCOUNT(700, "用户名不存在或者密码错误"),
    AUTH_ID_NOT_EXIST(701,"登录用户id不存在"),
    AUTH_USER_FORBIDDEN(702,"用户被禁止登录"),
    AUTH_NEED_LOGIN(704, "需要先登录"),
    AUTH_NOT_ALLOW(705,"无权限访问"),
    USER_NAME_REGISTERED(731, "用户名已被注册"),
    USER_NAME_SAME(732, "新用户名不能与旧用户名相同"),
    EMAIL_REGISTERED(733, "邮箱已被注册"),
    EMAIL_SAME( 734,"新邮箱不能与旧邮箱相同"),
    MOBILE_REGISTERED(735,"电话已被注册"),
    MOBILE_SAME( 736,"新电话不能与旧电话相同"),
    PASSWORD_SAME(741,"新密码不能与旧密码相同"),
    PRIVILEGE_BIT_SAME(744,"权限位重复"),
    EMAIL_WRONG(745,"与系统预留的邮箱不一致"),
    MOBILE_WRONG(746,"与系统预留的电话不一致"),
    EMAIL_NOT_VERIFIED(748,"Email未确认"),
    MOBILE_NOT_VERIFIED(749,"电话号码未确认"),
    VERIFY_CODE_EXPIRE(750, "验证码不正确或已过期"),
    USER_STUDENT_NOT_VERIFY(760, "用户学生认证未通过"),


    /***************************************************
     *    订单模块错误码
     **************************************************/
    DEMAND_STATUS_FORBID(800,"需求状态禁止"),
    ORDER_STATUS_FORBID(810, "订单状态禁止"),
    FEEDBACK_STATUS_FORBID(820, "反馈状态禁止"),


    /***************************************************
     *    图片模块错误码
     **************************************************/
    IMG_EXIST(900,"图片已存在"),
    IMG_NOT_EXIST(901, "图片不存在");

    private int code;
    private String message;
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
