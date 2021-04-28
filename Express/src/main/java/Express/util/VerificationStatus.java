package Express.util;

public enum VerificationStatus {

    UNHANDLED((byte)0), //未受理
    PASS((byte)1), //已认证
    FAILED((byte)2); //未通过

    private Byte code;

    VerificationStatus(Byte code){
        this.code = code;
    }

    public Byte getCode() {
        return code;
    }
}
