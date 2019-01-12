package com.github.chenhaiyangs.gateway;

import com.github.chenhaiyangs.gateway.common.dto.CommonResponse;
import com.github.chenhaiyangs.gateway.common.util.FastJsonUtil;
import com.github.chenhaiyangs.gateway.wapper.ResponseWapper;
import lombok.Data;

/**
 * 网关返回结果
 * @author chenhaiyang
 */
@Data
public class GateWayResponse {
    /**
     * 网关是否通过
     */
    private boolean passed=true;
    /**
     * 网关错误码
     */
    private String errCode;
    /**
     * 网关错误信息
     */
    private String errMsg;
    /**
     * 网关执行返回的响应数据
     */
    private String body;
    /**
     * 响应包装类
     */
    private ResponseWapper responseWapper = new ResponseWapper();

    public GateWayResponse() {
    }
    public GateWayResponse(String errCode, String errMsg) {
        this.passed = false;
        this.errCode = errCode;
        this.errMsg = errMsg;

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setResCode(errCode);
        commonResponse.setResMessage(errMsg);

        this.body = FastJsonUtil.toJSONString(commonResponse);
    }

    public GateWayResponse(boolean passed, String body) {
        this.passed = passed;
        this.body = body;
    }
}
