package Express.util;

public enum DemandType {

    EXPRESS((byte)0, "快递代取");

    private Byte code;
    private String message;

    DemandType(Byte code, String message){
        this.code = code;
        this.message = message;
    }
}
