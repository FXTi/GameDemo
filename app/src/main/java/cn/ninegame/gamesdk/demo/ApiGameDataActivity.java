package cn.ninegame.gamesdk.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.uc.gamesdk.UCGameSdk;
import cn.gundam.sdk.shell.param.*;
import cn.gundam.sdk.shell.exception.*;
import cn.uc.gamesdk.demo.R;

/**
 * 游戏数据提交范例
 * 角色创建、升级、退出重新登录，均需调用此接口
 */
public class ApiGameDataActivity extends Activity {

//    private JSONObject joGameData;

    @BindView(R.id.zoneIdEdit2)
    EditText zoneId;

    @BindView(R.id.zoneNameEdit2)
    EditText serverId;

    @BindView(R.id.roleIdEdit2)
    EditText roleId;

    @BindView(R.id.roleNameEdit2)
    EditText roleName;

    @BindView(R.id.roleLevelEdit2)
    EditText roleLevel;

    long time = System.currentTimeMillis();
    String nowTimeStamp = String.valueOf(time / 1000);


    protected void onCreate(Bundle instanced) {
        super.onCreate(instanced);
        setContentView(R.layout.submit_role);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.submit)
    void submit() {
        SDKParams sdkParams = new SDKParams();
        sdkParams.put(SDKParamKey.STRING_ROLE_ID, roleId.getText().toString());
        sdkParams.put(SDKParamKey.STRING_ROLE_NAME, roleName.getText().toString());
        sdkParams.put(SDKParamKey.LONG_ROLE_LEVEL, Long.valueOf(roleLevel.getText().toString()));       /**
         *角色创建时间，需要传递当前服务器时间，不可传递设备本地时间，值生成后保持唯一，且角色创建、升级、退出重登三个时机均需传递此参数
         **/
        sdkParams.put(SDKParamKey.LONG_ROLE_CTIME,Long.valueOf(nowTimeStamp));
        sdkParams.put(SDKParamKey.STRING_ZONE_ID, zoneId.getText().toString());
        sdkParams.put(SDKParamKey.STRING_ZONE_NAME, serverId.getText().toString());

        try {
            UCGameSdk.defaultSdk().submitRoleData(this, sdkParams);
            Toast.makeText(ApiGameDataActivity.this,"submitData = "+sdkParams, Toast.LENGTH_SHORT).show();
        } catch (AliNotInitException e) {
            e.printStackTrace();
        } catch (AliLackActivityException e) {
            e.printStackTrace();
        }
    }
}
