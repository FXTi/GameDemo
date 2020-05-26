package cn.ninegame.gamesdk.demo;

import android.app.Application;

import cn.uc.gamesdk.SDKHelper;

/**
 * 如果游戏不存在自定义的Application类，可以忽略这个类的代码，详见接入文档第4章-接入规范
 */
public class DemoApplication extends Application {
    /**
     * 如果游戏自定义的Application类中没有覆盖onCreate方法，则可以忽略此方法类代码
     */
    @Override
    public void onCreate() {
        //首先调用SDK辅助方法
        if(!SDKHelper.isBackground(this)){
            //调用super方法
            super.onCreate();
            //表示当前为SDK所属的子进程，不应该再执行其他游戏相关的初始化
            return ;
        }
        //原来的实现代码
    }
}
