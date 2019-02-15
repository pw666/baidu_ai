package com.wh.model.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by pan.wu on 2018/11/22.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentReviewResp {
    int reviewCode;
    String reviewDesc;
    String reviewDetail;

    public ContentReviewResp(ContentReviewEnum contentReviewEnum, String result) {
        this.setReviewCode(contentReviewEnum.getCode());
        this.setReviewDesc(contentReviewEnum.getDesc());
        this.setReviewDetail(result);
    }
}
