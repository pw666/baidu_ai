package com.wh.service;

import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.contentcensor.EImgType;
import com.baidu.aip.util.Base64Util;
import com.wh.mobile.logger.service.KafkaLogger;
import com.wh.model.resp.ContentReviewEnum;
import com.wh.model.resp.ContentReviewResp;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaiduAiService implements BaiduAiServiceInterface {

    @Autowired
    AipContentCensor aipContentCensor;

    @Autowired
    KafkaLogger logger;

    private String loggerType = "BaiduAi";

    /**
     * 文本审核
     *
     * @param txt 不超过20000字节
     * @return
     */
    @Override
    public ContentReviewResp textReview(String txt) throws JSONException {
        JSONObject resp = aipContentCensor.antiSpam(txt, null);
        logger.info("文本审核，txt="+txt+"\n百度审核结果："+resp.toString(),loggerType);
        return filterTxtResult(resp);
    }

    @Override
    /**
     * 图片文件类型支持PNG、JPG、JPEG、BMP，图片大小不超过2M。
     */
    public ContentReviewResp imgReview(String url) throws JSONException {
        //TODO
        JSONObject resp = aipContentCensor.imageCensorUserDefined(url, EImgType.URL, null);
        logger.info("图片url审核，url="+url+"\n百度审核结果："+resp.toString(),loggerType);
        return filterImgResult(resp);
    }

    @Override
    /**
     * data 响应成功并且conclusion为疑似或不合规时才返回，响应失败或conclusion为合规是不返回。
     * 图片文件类型支持PNG、JPG、JPEG、BMP，图片大小不超过2M。
     */
    public ContentReviewResp imgReview(byte[] img_bytes) throws JSONException {
        JSONObject resp = aipContentCensor.imageCensorUserDefined(img_bytes, null);
        return filterImgResult(resp);
    }

    /**
     * 文本审核结果处理
     * spam 请求中是否包含违禁，0表示非违禁，1表示违禁，2表示建议人工复审
     * reject 审核未通过的类别列表与详情
     * review 待人工复审的类别列表与详情
     * pass 审核通过的类别列表与详情
     * 审核异常返回{
     * "error_code": 282000,
     * "error_msg": "internal error",
     * "log_id": 5284009342430354247
     * }
     *
     * @param resp
     * @return
     * @throws JSONException
     */
    private ContentReviewResp filterTxtResult(JSONObject resp) throws JSONException {
        if (resp.toString().contains("error_code")) {
            return new ContentReviewResp(ContentReviewEnum.FAIL, resp.toString());
        }
        JSONObject result_json = new JSONObject(resp.get("result").toString());
        int spam = (int) result_json.get("spam");
        switch (spam) {
            case 0:
                return new ContentReviewResp(ContentReviewEnum.PASS, resp.toString());
            case 1:
                return new ContentReviewResp(ContentReviewEnum.UNPASS, resp.toString());
            case 2:
                return new ContentReviewResp(ContentReviewEnum.MANUAL, resp.toString());
            default:
                break;

        }
        return null;
    }


    /**
     * 图片审核结果处理
     * conclusion 审核结果描述，成功才返回，失败不返回。可取值 1.合规, 2.不合规, 3.疑似, 4.审核失败
     * conclusionType 审核结果标识，成功才返回，失败不返回。可取值1:合规, 2:不合规, 3:疑似, 4:审核失败
     * data 审核项详细信息，响应成功并且conclusion为疑似或不合规时才返回，响应失败或conclusion为合规是不返回。
     *
     * @param resp
     * @return
     * @throws JSONException
     */
    private ContentReviewResp filterImgResult(JSONObject resp) throws JSONException {
        if (resp.toString().contains("error_code")) {
            return new ContentReviewResp(ContentReviewEnum.FAIL, resp.toString());
        }
        int conclusionType = (int) resp.get("conclusionType");
        switch (conclusionType) {
            case 1:
                return new ContentReviewResp(ContentReviewEnum.PASS, resp.toString());
            case 2:
                logger.info("图片byte审核，百度审核结果："+resp.toString(),loggerType);
                return new ContentReviewResp(ContentReviewEnum.UNPASS, resp.toString());
            case 3:
                return new ContentReviewResp(ContentReviewEnum.MANUAL, resp.toString());
            case 4:
                return new ContentReviewResp(ContentReviewEnum.FAIL, resp.toString());
        }
        return null;
    }
}
