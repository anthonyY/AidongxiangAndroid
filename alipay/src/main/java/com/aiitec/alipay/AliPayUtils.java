package com.aiitec.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AliPayUtils {

    private static final int SDK_PAY_FLAG = 0x01;

    public static void pay(final Activity activity, final String payInfo, final PayCallBack callback) {
        if (TextUtils.isEmpty(payInfo)) {
            if (callback != null) {
                callback.onFail();
            }
            return;
        }
        final Handler mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SDK_PAY_FLAG: {
                        PayResult payResult = new PayResult((String) msg.obj);
                        Log.e("aiitec", "-----" + msg.obj.toString());
                        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//                    String resultInfo = payResult.getResult();
                        String resultStatus = payResult.getResultStatus();
                        if (callback != null) {
                            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                            if (TextUtils.equals(resultStatus, "9000")) {
                                callback.onSuccess();
                            }
                            // 判断resultStatus 为非“9000”则代表可能支付失败
                            // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                            else if (TextUtils.equals(resultStatus, "6001")) {//取消
                                callback.onCancel();
                            } else if (TextUtils.equals(resultStatus, "6002")) {//网络异常
                                callback.onNetErr();
                            } else {
                                callback.onFail();
                            }
                        }
                        break;
                    }
//                case SDK_CHECK_FLAG: {
//                    Toast.makeText(activity, "检查结果为：" + msg.obj,
//                            Toast.LENGTH_SHORT).show();
//                    break;
//                }
                    default:
                        break;
                }
            }

            ;
        };
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
//                alipay.payV2(payInfo, true);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, false);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }

    public static void pay(final Activity activity, String title, String content, double total, String orderSn, final PayCallBack callback) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        // 订单
        String orderInfo = getOrderInfo(title, content, orderSn, decimalFormat.format(total));

        // 对订单做RSA 签名
        String sign = SignUtils.sign(orderInfo, Keys.PRIVATE);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 完整的符合支付宝参数规范的订单信息
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";

        pay(activity, payInfo, callback);
    }

    /**
     * create the order info. 创建订单信息
     */
    public static String getOrderInfo(String subject, String body, String orderSn, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + Keys.DEFAULT_PARTNER + "\"";
        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + Keys.DEFAULT_SELLER + "\"";
        // 商户网站唯一订单号    //弃用支付宝默认订单号getOutTradeNo()
        orderInfo += "&out_trade_no=" + "\"" + orderSn + "\"";
        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";
        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";
        // 商品金额
        if (AliConfig.DEVELOPER_MODE) {
            orderInfo += "&total_fee=" + "\"" + "0.01" + "\"";
        } else {
            orderInfo += "&total_fee=" + "\"" + price + "\"";
        }
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + Keys.ALIPAY_CALLBACK_URL + "\"";
        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";
        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";
        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     * 支付宝默认订单号， 已过时， 用协议返回的订单号
     */
    @Deprecated
    public static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
                Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    public interface PayCallBack {
        /**
         * 支付成功
         */
        void onSuccess();
//        /**支付结果确认中*/
//        void onInRecognition();

        /**
         * 支付失败
         */
        void onFail();

        void onCancel();

        void onNetErr();
    }

}
