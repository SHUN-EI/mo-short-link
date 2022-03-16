package com.mo.biz;

import com.alibaba.fastjson.JSONObject;
import com.mo.ShopApplication;
import com.mo.config.PayBeanConfig;
import com.mo.config.WechatPayApiConfig;
import com.mo.config.WechatPayConfig;
import com.mo.enums.OrderCodeEnum;
import com.mo.utils.OrderCodeGenerateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.asn1.crmf.PKIPublicationInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * Created by mo on 2022/3/4
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShopApplication.class)
@Slf4j
public class WechatPayTest {

    @Autowired
    private PayBeanConfig payBeanConfig;
    @Autowired
    private OrderCodeGenerateUtil orderCodeGenerateUtil;
    @Autowired
    private WechatPayConfig wechatPayConfig;
    @Autowired
    private CloseableHttpClient wechatPayClient;


    /**
     * Native订单-查询退款状态
     */
    @Test
    public void testNativeRefundQuery() {
        //退款单号
        String refundNo = "TK220305000000005478";

        String url = String.format(WechatPayApiConfig.NATIVE_REFUND_QUERY, refundNo);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");

        try (CloseableHttpResponse response = wechatPayClient.execute(httpGet)) {

            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());

            log.info("查询订单退款状态 响应码:{},响应体:{}",statusCode,responseStr);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Native订单-申请退款
     */
    @Test
    public void testNativeRefundOrder() {

        //订单号
        String outTradeNo = "XD220316000000024422";
        //退款单号
        String refundNo = orderCodeGenerateUtil.generateOrderCode(OrderCodeEnum.TK);

        //请求body参数
        JSONObject refundObj = new JSONObject();

        refundObj.put("out_trade_no", outTradeNo);
        //退款单编号，商户系统内部的退款单号，商户系统内部唯一，
        // 只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔
        refundObj.put("out_refund_no", refundNo);
        refundObj.put("reason", "商品已售完");
        refundObj.put("notify_url", wechatPayConfig.getCallbackUrl());

        JSONObject amountObj = new JSONObject();
        //退款金额
        amountObj.put("refund", 100);
        //实际支付的总金额
        amountObj.put("total", 100);
        amountObj.put("currency", "CNY");
        refundObj.put("amount", amountObj);

        String body = refundObj.toJSONString();
        log.info("请求参数:{}", body);

        StringEntity entity = new StringEntity(body, "utf-8");
        entity.setContentType("application/json");
        HttpPost httpPost = new HttpPost(WechatPayApiConfig.NATIVE_REFUND_ORDER);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = wechatPayClient.execute(httpPost)) {

            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());

            log.info("申请订单退款响应码:{},响应体:{}", statusCode, responseStr);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Native订单-关闭订单
     */
    @Test
    public void testNativeCloseOrder() {
        //订单号
        String outTradeNo = "XD220305000000000798";

        JSONObject payObj = new JSONObject();
        payObj.put("mchid", wechatPayConfig.getMchId());

        // 处理请求body参数
        String body = payObj.toJSONString();
        log.info("请求参数:{}", body);

        //将请求参数设置到请求对象中
        StringEntity entity = new StringEntity(body, "utf-8");
        entity.setContentType("application/json");

        String url = String.format(WechatPayApiConfig.NATIVE_CLOSE_ORDER, outTradeNo);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = wechatPayClient.execute(httpPost);) {

            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            log.info("关闭订单响应码:{},无响应体", statusCode);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Native订单状态查询, 根据商户订单号查询
     * 1.testWechatPayNativeOrder 先生成订单号
     * 2.testWechatPayNativeQuery 再查询订单状态
     */
    @Test
    public void testWechatPayNativeQuery() {
        //订单号
        String outTradeNo = "XD220305000000000798";
        String url = String.format(WechatPayApiConfig.NATIVE_QUERY, outTradeNo, wechatPayConfig.getMchId());

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");

        try (CloseableHttpResponse response = wechatPayClient.execute(httpGet);) {
            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());
            log.info("native查询订单状态响应码:{},响应体:{}", statusCode, responseStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 测试 Native下单，相关参数验证
     */
    @Test
    public void testWechatPayNativeOrder() {

        //订单号
        String outTradeNo = orderCodeGenerateUtil.generateOrderCode(OrderCodeEnum.XD);
        //过期时间  RFC 3339格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        //支付订单过期时间
        String expireTime = sdf.format(new Date(System.currentTimeMillis() + 6000 * 60 * 1000));

        //构建下单请求对象
        JSONObject payObj = new JSONObject();
        payObj.put("mchid", wechatPayConfig.getMchId());
        payObj.put("out_trade_no", outTradeNo);
        payObj.put("appid", wechatPayConfig.getWxPayAppid());
        payObj.put("description", "微信支付-native下单测试");
        payObj.put("notify_url", wechatPayConfig.getCallbackUrl());

        //订单总金额，单位为分。
        JSONObject amountObj = new JSONObject();
        amountObj.put("total", 100);
        amountObj.put("currency", "CNY");
        payObj.put("amount", amountObj);

        //附属参数，可以用在回调携带
        payObj.put("attach", "{\"accountNo\":" + 888 + "}");

        // 处理请求body参数
        String body = payObj.toJSONString();
        log.info("请求参数:{}", body);

        StringEntity entity = new StringEntity(body, "utf-8");
        entity.setContentType("application/json");

        //调用统一下单API-Native下单
        HttpPost httpPost = new HttpPost(WechatPayApiConfig.NATIVE_ORDER);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(entity);

        //httpClient自动进行参数签名
        try (CloseableHttpResponse response = wechatPayClient.execute(httpPost);) {
            //响应码
            int statusCode = response.getStatusLine().getStatusCode();
            //响应体
            String responseStr = EntityUtils.toString(response.getEntity());
            log.info("native下单响应码:{},响应体:{}", statusCode, responseStr);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 测试读取微信支付私钥
     *
     * @throws IOException
     */
    @Test
    public void testLoadPrivateKey() throws IOException {

        PrivateKey privateKey = payBeanConfig.getPrivateKey();
        log.info("微信支付的私钥:{}", privateKey.toString());
        log.info("微信支付的私钥的算法:{}", privateKey.getAlgorithm());
    }

}
