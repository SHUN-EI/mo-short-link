package com.mo.controller;

import com.alibaba.fastjson.JSONObject;
import com.mo.config.WechatPayConfig;
import com.wechat.pay.contrib.apache.httpclient.auth.ScheduledUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mo on 2022/3/8
 */
@Api(tags = "支付模块")
@RestController
@RequestMapping("/api/pay/v1")
@Slf4j
public class PayController {

    @Autowired
    private ScheduledUpdateCertificatesVerifier verifier;
    @Autowired
    private WechatPayConfig wechatPayConfig;


    @ApiOperation("微信支付回调")
    @RequestMapping("/wechatPayCallback")
    @ResponseBody
    public Map<String, String> wechatPayCallback(HttpServletRequest request, HttpServletResponse response) {

        //获取报文
        String body = getRequestBody(request);
        //随机串
        String nonceStr = request.getHeader("Wechatpay-Nonce");
        //微信传递过来的签名
        String signature = request.getHeader("Wechatpay-Signature");
        //证书序列号（微信平台）
        String serialNo = request.getHeader("Wechatpay-Serial");
        //时间戳
        String timestamp = request.getHeader("Wechatpay-Timestamp");

        /**
         * 构造签名串
         *   应答时间戳\n
         *   应答随机串\n
         *   应答报文主体\n
         */
        String signStr = Stream.of(timestamp, nonceStr, body).
                collect(Collectors.joining("\n", "", "\n"));

        Map<String, String> map = new HashMap<>(2);

        try {
            //验证签名（确保是微信传输过来的）
            Boolean result = verifiedSign(serialNo, signStr, signature);

            if (result) {
                //解密数据（AES对称解密出原始数据）
                String plainBody = decryptBody(body);
                log.info("解密后的明文:{}", plainBody);

                Map<String, String> paramsMap = convertWechatPayMsgToMap(plainBody);

                //处理业务逻辑 TODO

                //响应微信,通知应答, 支付成功
                map.put("code", "SUCCESS");
                map.put("message", "成功");
            }

        } catch (Exception e) {
            log.error("微信支付回调异常:{}", e);
        }

        return map;
    }

    /**
     * 把body里面返回的参数转换成map
     *
     * @param plainBody
     * @return
     */
    private Map<String, String> convertWechatPayMsgToMap(String plainBody) {

        Map<String, String> paramsMap = new HashMap<>(2);

        JSONObject jsonObject = JSONObject.parseObject(plainBody);

        //商户订单号
        paramsMap.put("out_trade_no",jsonObject.getString("out_trade_no"));
        //交易状态
        paramsMap.put("trade_state",jsonObject.getString("trade_state"));
        //附加数据
        paramsMap.put("account_no",jsonObject.getJSONObject("attach").getString("accountNo"));

        return paramsMap;
    }


    /**
     * 解密数据（AES对称解密出原始数据）
     *
     * @param body
     * @return
     * @throws UnsupportedEncodingException
     * @throws GeneralSecurityException
     */
    private String decryptBody(String body) throws UnsupportedEncodingException, GeneralSecurityException {

        AesUtil aesUtil = new AesUtil(wechatPayConfig.getApiV3Key().getBytes("utf-8"));

        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject resource = jsonObject.getJSONObject("resource");
        //密文, Base64编码后的密文
        String ciphertext = resource.getString("ciphertext");
        //附加数据包（可能为空）
        String associatedData = resource.getString("associated_data");
        //加密使用的随机串初始化向量）
        String nonce = resource.getString("nonce");

        return aesUtil.decryptToString(associatedData.getBytes("utf-8"),
                nonce.getBytes("utf-8"),
                ciphertext);
    }

    /**
     * 验证签名（确保是微信传输过来的）
     *
     * @param serialNo  微信平台-证书序列号
     * @param signStr   自己组装的签名串
     * @param signature 微信返回的签名
     * @return
     * @throws UnsupportedEncodingException
     */
    private Boolean verifiedSign(String serialNo, String signStr, String signature) throws UnsupportedEncodingException {

        Boolean result = verifier.verify(serialNo, signStr.getBytes("utf-8"), signature);
        return result;
    }

    /**
     * 获取报文
     * 读取请求中的数据
     *
     * @param request
     * @return
     */
    private String getRequestBody(HttpServletRequest request) {

        StringBuffer sb = new StringBuffer();

        try (ServletInputStream inputStream = request.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            log.error("读取数据流异常:{}", e);
        }

        return sb.toString();
    }

}
