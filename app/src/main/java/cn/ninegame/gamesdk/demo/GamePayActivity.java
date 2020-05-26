package cn.ninegame.gamesdk.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ninegame.gamesdk.demo.util.MD5Util;
import cn.uc.gamesdk.UCGameSdk;
import cn.gundam.sdk.shell.even.SDKEventKey;
import cn.gundam.sdk.shell.even.SDKEventReceiver;
import cn.gundam.sdk.shell.even.Subscribe;
import cn.gundam.sdk.shell.open.OrderInfo;
import cn.gundam.sdk.shell.param.SDKParamKey;
import cn.gundam.sdk.shell.param.SDKParams;
import cn.uc.gamesdk.demo.R;

/**
 * 充值界面
 *
 * @author
 */
public class GamePayActivity extends Activity {
    private static final String TAG = "alisdk";

//    @BindView(R.id.editTextPayRoleId)
//    EditText mRoleId;
//
//    @BindView(R.id.editTextPayGrade)
//    EditText mGrade;
//
//    @BindView(R.id.editTextPayRoleName)
//    EditText mRoleName;
//
//    @BindView(R.id.editTextPayServerId)
//    EditText mServerId;

    @BindView(R.id.editTextPayCustomInfo)
    EditText mCustomInfo;

    @BindView(R.id.editTextAmount)
    EditText mAmount;

    @BindView(R.id.editTextNotifyUrl)
    EditText mCallbackUrl;

    @BindView(R.id.editTextAccountId)
    EditText mAccountId;

//    @BindView(R.id.editTextPayProdName)
//    EditText mProdName;

    @BindView(R.id.textViewPayOutputs)
    TextView mOutput;

    @BindView(R.id.editTextPayOrderId)
    EditText mOrderId;

    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        this.setContentView(R.layout.paycfg);

        ButterKnife.bind(this);
//        mAccountId.setVisibility(View.INVISIBLE);
        UCGameSdk.defaultSdk().registerSDKEventReceiver(payReceiver);
        reset();
    }

    protected void onDestroy() {
        super.onDestroy();
        UCGameSdk.defaultSdk().unregisterSDKEventReceiver(payReceiver);
    }

    @OnClick({R.id.buttonCharge})
    void charge() {
        final String accountId = mAccountId.getText().toString();
        if (!TextUtils.isEmpty(accountId)) {

            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put(SDKParamKey.CALLBACK_INFO, mCustomInfo.getText().toString());
            //paramMap.put(SDKParamKey.SERVER_ID, mServerId.getText().toString());
            //paramMap.put(SDKParamKey.ROLE_ID, mRoleId.getText().toString());
            //paramMap.put(SDKParamKey.ROLE_NAME, mRoleName.getText().toString());
            //paramMap.put(SDKParamKey.GRADE, mGrade.getText().toString());
            paramMap.put(SDKParamKey.NOTIFY_URL, mCallbackUrl.getText().toString());
            paramMap.put(SDKParamKey.AMOUNT, mAmount.getText().toString());
            paramMap.put(SDKParamKey.CP_ORDER_ID, mOrderId.getText().toString());
            paramMap.put(SDKParamKey.ACCOUNT_ID, accountId);
            paramMap.put(SDKParamKey.SIGN_TYPE, "MD5");

            SDKParams sdkParams = new SDKParams();

            Map<String, Object> map = new HashMap<String, Object>();
            map.putAll(paramMap);
            sdkParams.putAll(map);

            String sign = sign(paramMap, UCSdkConfig.sign_key);
            sdkParams.put(SDKParamKey.SIGN, sign);

            try {
                UCGameSdk.defaultSdk().pay(this, sdkParams);
            } catch (Exception e) {
                e.printStackTrace();
                addOutputResult("charge failed - Exception: " + e.toString() + "\n");
            }
        }
    }

    @OnClick({R.id.buttonReset})
    @UiThread
    void reset() {
        //mRoleId.setText("AAA");
        //mGrade.setText("BBB");
        //mRoleName.setText("CCC");
        //mServerId.setText("0");
        //mProdName.setText("EEE");

        mCustomInfo.setText("DDD");
        mAmount.setText("2.33");
        mCallbackUrl.setText("http://pay.uctest2.ucweb.com:8039/result.jsp");
        mAccountId.setText("123456");
        mAccountId.setText(requestAccountId());//FIXME 需要根据实际登录的账号修改对应的ACCOUNT_ID,需要向SDK服务器获取当前的accountId后填入
        refreshOrderId();
    }

    /**
     * 向SDK服务器获取当前登录账号的AccountId
     *
     * @return
     */
    private String requestAccountId() {
        //TODO
        return "106563120";
    }

    /**
     * 输出结果到界面
     *
     * @param msg
     */
    private void addOutputResult(String msg) {
        msg = ">>" + msg;
        msg = msg + this.mOutput.getText();
        this.mOutput.setText(msg);
    }

    SDKEventReceiver payReceiver = new SDKEventReceiver() {
        @Subscribe(event = SDKEventKey.ON_CREATE_ORDER_SUCC)
        private void onCreateOrderSucc(OrderInfo orderInfo) {
            dumpOrderInfo(orderInfo);
            if (orderInfo != null) {
                String txt = orderInfo.getOrderAmount() + "," + orderInfo.getOrderId() + "," + orderInfo.getPayWay();
                addOutputResult("下单成功: " + txt + "\n");
            }
            Log.i(TAG, "pay succ");
        }

        @Subscribe(event = SDKEventKey.ON_PAY_USER_EXIT)
        private void onPayUserExit(OrderInfo orderInfo) {
            dumpOrderInfo(orderInfo);
            if (orderInfo != null) {
                String txt = orderInfo.getOrderAmount() + "," + orderInfo.getOrderId() + "," + orderInfo.getPayWay();
                addOutputResult("充值失败: " + txt + "\n");
            }
            Log.i(TAG, "pay fail");
        }
    };

    private void dumpOrderInfo(OrderInfo orderInfo) {
        if (orderInfo != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("'orderId':'%s'", orderInfo.getOrderId()));
            sb.append(String.format("'orderAmount':'%s'", orderInfo.getOrderAmount()));
            sb.append(String.format("'payWay':'%s'", orderInfo.getPayWay()));
            sb.append(String.format("'payWayName':'%s'", orderInfo.getPayWayName()));

            Log.i(TAG, "callback orderInfo = " + sb);
        }
    }

    private String refreshOrderId() {
        String date = formatDate(System.currentTimeMillis(), "yyyyMMddHHmmss");
        mOrderId.setText(date);
        return date;
    }

    private String formatDate(long time, String format) {
        SimpleDateFormat dateformat = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
        dateformat.applyPattern(format);
        return dateformat.format(time);
    }

    /**
     * 签名工具方法
     *
     * @param reqMap
     * @return
     */
    private static String sign(Map<String, String> reqMap, String signKey) {
        TreeMap<String, String> signMap = new TreeMap<String, String>(reqMap);
        StringBuilder stringBuilder = new StringBuilder(1024);
        for (Map.Entry<String, String> entry : signMap.entrySet()) {
            if ("sign".equals(entry.getKey()) || "signType".equals(entry.getKey())) {
                continue;
            }
            if (entry.getValue() != null) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        stringBuilder.append(signKey);
        String signSrc = stringBuilder.toString().replaceAll("&", "");//剔除参数中含有的'&'符号
        return MD5Util.md5(signSrc).toLowerCase();
    }


}
