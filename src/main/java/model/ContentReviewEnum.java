package com.wh.model.resp;

/**
 * Created by pan.wu on 2018/11/20.
 */
public enum ContentReviewEnum {
    PASS(0, "审核通过"),
    UNPASS(1, "审核不通过"),
    FAIL(3, "审核失败"),
    MANUAL(2, "人工复审");

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    int code;
    String desc;

    ContentReviewEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
