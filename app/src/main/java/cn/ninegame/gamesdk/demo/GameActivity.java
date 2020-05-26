package cn.ninegame.gamesdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.gundam.sdk.shell.even.*;
import cn.gundam.sdk.shell.param.*;
import cn.gundam.sdk.shell.exception.*;
import cn.uc.gamesdk.*;
import cn.gundam.sdk.shell.open.*;
import cn.uc.gamesdk.demo.R;

/**
 * 游戏主程序。包含了对UCGameSDK以下接口的调用：<br>
 * <p/>
 * 1 初始化<br>
 * 2 登录<br>
 * 3 个人中心<br>
 *
 * @author chenzh
 */
public class GameActivity extends Activity {

    private Handler handler;

    //    @ViewById(R.id.btnLogin)
    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.btnEnterPay)
    Button btnPay;

    @BindView(R.id.btnLogout)
    Button btnLogout;

    @BindView(R.id.btnRole)
    Button btnSubmit;

    public void onCreate(Bundle b) {
        //GLog.d("GameActivity", "----------onCreate---------");
        super.onCreate(b);
        this.setContentView(R.layout.splashscreen);//设置启动画面
        ButterKnife.bind(this);

        ucNetworkAndInitUCGameSDK();
        handler = new Handler(Looper.getMainLooper());

        UCGameSdk.defaultSdk().registerSDKEventReceiver(receiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ucNetworkAndInitUCGameSDK();
    }

    public void ucNetworkAndInitUCGameSDK() {
        //!!!在调用SDK初始化前进行网络检查
        //当前没有拥有网络
        ucSdkInit();//执行UCGameSDK初始化
    }

    private void ucSdkInit() {
        ParamInfo gameParamInfo = new ParamInfo();

        gameParamInfo.setGameId(UCSdkConfig.gameId);

        gameParamInfo.setOrientation(UCOrientation.PORTRAIT);

        SDKParams sdkParams = new SDKParams();
        sdkParams.put(SDKParamKey.GAME_PARAMS, gameParamInfo);



        try {
            UCGameSdk.defaultSdk().initSdk(this, sdkParams);
        } catch (AliLackActivityException e) {
            e.printStackTrace();
        }
    }


    private void startGame() {
        btnLogin.setVisibility(View.VISIBLE);
        btnPay.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.VISIBLE);

        ucSdkLogin();
    }

    protected void onDestroy() {
        //GLog.d("GameActivity", "----------onDestroy---------");
        UCGameSdk.defaultSdk().unregisterSDKEventReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }

    @OnClick(R.id.btnLogin)
    void ucSdkLogin() {
        try {
            UCGameSdk.defaultSdk().login(this, null);
        } catch (AliLackActivityException e) {
            e.printStackTrace();
        } catch (AliNotInitException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnEnterPay)
    @UiThread
    void ucSdkPay() {
        Intent intent = new Intent(GameActivity.this, GamePayActivity.class);
        GameActivity.this.startActivity(intent);
    }

    @OnClick(R.id.btnLogout)
    void ucSdkLogout() {
        try {
            UCGameSdk.defaultSdk().logout(this, null);
        } catch (AliLackActivityException e) {
            e.printStackTrace();
        } catch (AliNotInitException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnRole)
    void ucSdkRole() {
        Intent intent = new Intent(GameActivity.this, ApiGameDataActivity.class);
        GameActivity.this.startActivity(intent);
    }

    public void onBackPressed() {
        try {
            UCGameSdk.defaultSdk().exit(this, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    SDKEventReceiver receiver = new SDKEventReceiver() {
        @Subscribe(event = SDKEventKey.ON_INIT_SUCC)
        private void onInitSucc() {
            //初始化成功
            handler.post(new Runnable() {

                @Override
                public void run() {
                    startGame();
                }
            });
        }

        @Subscribe(event = SDKEventKey.ON_INIT_FAILED)
        private void onInitFailed(String data) {
            //初始化失败
            Toast.makeText(GameActivity.this, "init failed", Toast.LENGTH_SHORT).show();
            ucNetworkAndInitUCGameSDK();
        }

        @Subscribe(event = SDKEventKey.ON_LOGIN_SUCC)
        private void onLoginSucc(String sid) {
            Toast.makeText(GameActivity.this, "login succ,sid=" + sid, Toast.LENGTH_SHORT).show();
//            final GameActivity me = GameActivity.this;
//            AccountInfo.instance().setSid(sid);
        }

        @Subscribe(event = SDKEventKey.ON_LOGIN_FAILED)
        private void onLoginFailed(String desc) {
            Toast.makeText(GameActivity.this, desc, Toast.LENGTH_SHORT).show();
//            printMsg(desc);
        }

        @Subscribe(event = SDKEventKey.ON_LOGOUT_SUCC)
        private void onLogoutSucc() {
            Toast.makeText(GameActivity.this, "logout succ", Toast.LENGTH_SHORT).show();
//            AccountInfo.instance().setSid("");
            ucSdkLogin();
        }

        @Subscribe(event = SDKEventKey.ON_LOGOUT_FAILED)
        private void onLogoutFailed() {
            Toast.makeText(GameActivity.this, "logout failed", Toast.LENGTH_SHORT).show();
//            printMsg("注销失败");
        }

        @Subscribe(event = SDKEventKey.ON_EXIT_SUCC)
        private void onExit(String desc) {
            Toast.makeText(GameActivity.this, desc, Toast.LENGTH_SHORT).show();

            GameActivity.this.finish();

            // 退出程序
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
//            printMsg(desc);
        }

        @Subscribe(event = SDKEventKey.ON_EXIT_CANCELED)
        private void onExitCanceled(String desc) {
            Toast.makeText(GameActivity.this, desc, Toast.LENGTH_SHORT).show();
        }

    };
}