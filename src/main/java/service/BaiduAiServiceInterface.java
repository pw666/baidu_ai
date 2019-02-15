package com.wh.service;

import com.wh.model.resp.ContentReviewResp;
import org.json.JSONException;

public interface BaiduAiServiceInterface {
    ContentReviewResp textReview(String txt) throws JSONException;

    ContentReviewResp imgReview(String url) throws JSONException;

    ContentReviewResp imgReview(byte[] img_bytes) throws JSONException;
}
