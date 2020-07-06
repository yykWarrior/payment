package com.pay.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.pay.config.AlipayConfig;
import org.apache.ibatis.annotations.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @version v1.0
 * @ClassName: PayController
 * @Description: TODO
 * @Author: yyk
 * @Date: 2020/6/2 15:21
 */
@RestController
@RequestMapping("pay")
public class PayController {

    @Autowired
    AlipayClient alipayClient;

    /**
     * @Author yyk
     * @Description //TODO 支付成功后的异步回调函数
     * @Date 2020/6/3 0:13
     * @Param [request, modelMap]
     * @return java.lang.String
     **/
    @RequestMapping("pay/return")
    public String reCall(HttpServletRequest request){
        // 回调请求中获取支付宝参数
        String sign = request.getParameter("sign");
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_status = request.getParameter("trade_status");
        String total_amount = request.getParameter("total_amount");
        String subject = request.getParameter("subject");
        String call_back_content = request.getQueryString();


        //AlipaySignature.rsaCheck();
        // 通过支付宝的paramsMap进行签名验证，2.0版本的接口将paramsMap参数去掉了，导致同步请求没法验签
        if(sign!=null){
            // 验签成功
            System.out.println("验签成功");

        }

        return "调用succsee";
    }


    /**
     * @Author yyk
     * @Description //TODO 请求支付接口
     * @Date 2020/6/3 0:06
     * @Param [request]
     * @return java.lang.String
     **/
    @RequestMapping("alipay/submit")
    public String alipay(HttpServletRequest request){

        String form="";
        //获取一个支付宝客户端
        AlipayTradePagePayRequest alipayTradePagePayRequest = new AlipayTradePagePayRequest();
        //把回调函数也放入里面
        alipayTradePagePayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayTradePagePayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        Map<String,Object> map = new HashMap<>();
        //商户网站唯一订单号
        map.put("out_trade_no","1A4566776576");
        //销售产品码，商家和支付宝签约的产品码
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        //订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
        map.put("total_amount",0.01);
        //商品的标题/交易标题/订单标题/订单关键字等。
        map.put("subject","尚硅谷感光徕卡Pro300瞎命名系列手机");
        String param = JSON.toJSONString(map);

        alipayTradePagePayRequest.setBizContent(param);
        System.out.println("公共参数："+alipayTradePagePayRequest.getBizContent());
        try {
             form = alipayClient.execute(alipayTradePagePayRequest).getBody();
            System.out.println(form);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return form;
    }



    /**
     * @Author yyk
     * @Description //TODO 请求app支付接口
     * @Date 2020/6/3 0:36
     * @Param []
     * @return java.lang.String
     **/
    @RequestMapping("/app")
    public String appAlipay(HttpServletRequest request){
        //订单名称，必填
        String subject = "课程付款";
        //商品描述，可空
        String body = "云河美术课程报名";
        // 订单金额
        Double orderPrice = 0.01;

        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest aliPayRequest = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(body);
        model.setSubject(subject);
        model.setOutTradeNo("q23er56gg");
        model.setTimeoutExpress("30m");
        model.setTotalAmount(String.valueOf(orderPrice));// 单位：元
        model.setProductCode("QUICK_MSECURITY_PAY");
        aliPayRequest.setBizModel(model);
        aliPayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        aliPayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(aliPayRequest);
            String body1 = response.getBody();
            /*Map map = new HashMap();
            map.put("orderStr", response.getBody());
            map.put("total_fee", 0.001);
            map.put("order_no", "q23er56gg");
            //map.put("seller_id", AlipayConfig.SELLERID);*/
            return body1;
        } catch (AlipayApiException e) {
            //e.printStackTrace();
            return null;
        }
    }




}
