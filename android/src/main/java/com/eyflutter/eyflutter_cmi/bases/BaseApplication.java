package com.eyflutter.eyflutter_cmi.bases;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDex;

import com.cloud.eyutils.events.Action2;
import com.cloud.eyutils.launchs.LauncherState;
import com.cloud.eyutils.launchs.utils.ActivityUtils;
import com.cloud.eyutils.logs.CrashHandler;
import com.eyflutter.eyflutter_cmi.events.OnActivityCycleStatusCall;
import com.eyflutter.eyflutter_cmi.events.OnApplicationLifecycle;

import io.flutter.app.FlutterApplication;

/**
 * Author lijinghuan
 * Email:ljh0576123@163.com
 * CreateTime:2019/3/6
 * Description:应用程序基础类;相关生命周期可通过实现
 * 和设置
 * Modifier:
 * ModifyContent:
 */
public abstract class BaseApplication extends FlutterApplication implements OnApplicationLifecycle {

    //当前处于前台的activity数量
    private int countActivity = 0;
    //应用是否处于后台
    private boolean isAppOnBackground = false;
    //应用堆栈前一状态,1-前台;2-后台;
    private int prevAppStackStatus = 0;
    //应用堆栈当前状态,1-前台;2-后台;
    private int currAppStackStatus = 0;
    //生命周期回调监听
    private OnApplicationLifecycle onApplicationLifecycle;
    private static BaseApplication mapplication;

    /**
     * 设置生命周期回调监听
     *
     * @param onApplicationLifecycle 生命周期回调监听
     */
    public void setOnApplicationLifecycle(OnApplicationLifecycle onApplicationLifecycle) {
        this.onApplicationLifecycle = onApplicationLifecycle;
    }

    /**
     * 获取应用堆栈前一状态
     *
     * @return 1-前台;2-后台;
     */
    public int getPrevAppStackStatus() {
        return prevAppStackStatus;
    }

    /**
     * 获取应用堆栈当前状态
     *
     * @return 1-前台;2-后台;
     */
    public int getCurrAppStackStatus() {
        return currAppStackStatus;
    }

    /**
     * 获取当前activity计数
     *
     * @return
     */
    public int getCountActivity() {
        return countActivity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mapplication = this;
        LauncherState launcherState = LauncherState.getInstance();
        launcherState.onLauncher(this);

        this.setOnApplicationLifecycle(this);
        registerActivityLifecycle();
        //最后初始化全局异常日志处理
        //便于记录日志记录与拦截
        CrashHandler crashHandler = new CrashHandler() {
            @Override
            protected void onLogIntercept(Throwable throwable) {
                if (onApplicationLifecycle != null) {
                    onApplicationLifecycle.onLogIntercept(throwable);
                }
            }
        };
        crashHandler.init(this);
        if (onApplicationLifecycle != null) {
            onApplicationLifecycle.onApplicationCreated();
        }
    }

    public static BaseApplication getInstance() {
        return mapplication;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isAppOnBackground = true;
            //app stack status
            prevAppStackStatus = currAppStackStatus;
            currAppStackStatus = 2;
            //call into back
            if (onApplicationLifecycle != null) {
                onApplicationLifecycle.onAppSiwtchToBack();
            }
        }
    }

    private void registerActivityLifecycle() {
        final Action2<Activity, Integer> activitySwitchStatueRunnable = new Action2<Activity, Integer>() {
            @Override
            public void call(Activity activity, Integer status) {
                if (!(activity instanceof OnActivityCycleStatusCall)) {
                    return;
                }
                OnActivityCycleStatusCall cycleStatusCall = (OnActivityCycleStatusCall) activity;
                //1-前台;2-退至后台;3-已稍毁;
                cycleStatusCall.onCurrCycleStatus(status);
            }
        };

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //回调生命周期
                if (onApplicationLifecycle != null) {
                    onApplicationLifecycle.onActivityCreated(activity, savedInstanceState);
                }
                //记录活动名称
                ActivityUtils.getInstance().put(activity.getClass().getName());
                LauncherState.getInstance().onActivityCreated(activity, savedInstanceState);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                //前后切换
                countActivity++;
                if (countActivity > 0) {
                    isAppOnBackground = false;
                    //app stack status
                    prevAppStackStatus = currAppStackStatus;
                    currAppStackStatus = 1;
                    //call into front
                    if (onApplicationLifecycle != null) {
                        onApplicationLifecycle.onAppSiwtchToFront();
                    }
                }
                if (onApplicationLifecycle != null) {
                    onApplicationLifecycle.onActivityStarted(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                //设置当前页面切换至后台状态
                //1-前台;2-退至后台;3-已稍毁;
                activitySwitchStatueRunnable.call(activity, 1);
                if (onApplicationLifecycle != null) {
                    onApplicationLifecycle.onActivityResumed(activity);
                }
                LauncherState.getInstance().onActivityResumed(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (onApplicationLifecycle != null) {
                    onApplicationLifecycle.onActivityPaused(activity);
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                //前后台切换
                countActivity--;
                if (countActivity == 0) {
                    isAppOnBackground = true;
                    //app stack status
                    prevAppStackStatus = currAppStackStatus;
                    currAppStackStatus = 2;
                    //call into back
                    if (onApplicationLifecycle != null) {
                        onApplicationLifecycle.onAppSiwtchToBack();
                    }
                }
                //设置当前页面切换至后台状态
                //1-前台;2-退至后台;3-已稍毁;
                activitySwitchStatueRunnable.call(activity, 2);

                if (onApplicationLifecycle != null) {
                    onApplicationLifecycle.onActivityStopped(activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                if (onApplicationLifecycle != null) {
                    onApplicationLifecycle.onActivitySaveInstanceState(activity, outState);
                }
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //设置当前页面切换至后台状态
                //1-前台;2-退至后台;3-已稍毁;
                activitySwitchStatueRunnable.call(activity, 3);
                if (onApplicationLifecycle != null) {
                    onApplicationLifecycle.onActivityDestroyed(activity);
                }
                //移除活动名称
                ActivityUtils.getInstance().remove(activity.getClass().getName());
                //移出activity队列
                LauncherState.getInstance().onActivityDestroyed(activity);
            }
        });
    }

    /**
     * @return true在后台运行, false在前台
     */
    public boolean isAppOnBackground() {
        if (!isAppOnBackground) {
            if (countActivity <= 0) {
                isAppOnBackground = true;
            }
        }
        return isAppOnBackground;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //created call
    }

    @Override
    public void onActivityStarted(Activity activity) {
        //started call
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //resumed call
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //paused call
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //stopped call
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        //save instance state call
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //destoryed call
    }
}
