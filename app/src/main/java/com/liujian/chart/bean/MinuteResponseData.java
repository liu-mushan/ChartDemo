package com.liujian.chart.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author : liujian
 * @since : 2018/1/21
 */

public class MinuteResponseData implements Serializable {
    private int success;
    private String error_code;
    private String msg;
    List<CMinute> data;
    private Param param;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<CMinute> getData() {
        return data;
    }

    public void setData(List<CMinute> data) {
        this.data = data;
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }
}
