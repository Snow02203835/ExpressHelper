package Express.model.bo;

import Core.util.ResponseCode;
import Express.model.vo.BillVo;

public class Bill {
    public static ResponseCode validateBillStatus(BillVo billVo, Long demandId){
        System.out.println("BillVo: " + billVo.toString() + ", demandId: " + demandId);
        return ResponseCode.BILL_PAID;
    }
}
