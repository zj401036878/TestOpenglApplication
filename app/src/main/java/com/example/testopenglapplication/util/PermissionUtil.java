package com.example.testopenglapplication.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DELL on 2018/7/09.
 */

public class PermissionUtil {

    /**
     * 检测权限
     *
     * @param activity
     * @param permissions //请求的权限组
     * @param requestCode //请求码
     */
    public static void checkPermission(final Activity activity, final String[] permissions, final int requestCode, PermissionInterface permissionInterface) {
        //小于23 就什么都不做
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        List<String> deniedPermissions = findDeniedPermissions(activity, permissions);
        if (deniedPermissions != null && deniedPermissions.size() > 0) {
            //大于0,表示有权限没申请
            PermissionUtil.requestContactsPermissions(activity, deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
        } else {
            //已拥有权限
            if (permissionInterface != null) {
                permissionInterface.success();
            }
        }
    }

    /**
     * 请求权限
     */
    public static void requestContactsPermissions(final Activity activity, final String[] permissions, final int requestCode) {
        //默认是false,但是只要请求过一次权限就会为true,除非点了不再询问才会重新变为false
        if (shouldShowPermissions(activity, permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        } else {
            // 无需向用户界面提示，直接请求权限,如果用户点了不再询问,即使调用请求权限也不会出现请求权限的对话框
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    /**
     * 判断请求权限是否成功
     *
     * @param grantResults
     * @return
     */
    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    //回调接口
    public interface PermissionInterface {
        void success();
    }

    /**
     * 找到没有授权的权限
     *
     * @param activity
     * @param permission
     * @return
     */
    public static List<String> findDeniedPermissions(Activity activity, String... permission) {
        //存储没有授权的权限
        List<String> denyPermissions = new ArrayList<>();
        for (String value : permission) {
            if (ContextCompat.checkSelfPermission(activity, value) != PackageManager.PERMISSION_GRANTED) {
                //没有权限 就添加
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    /**
     * 检测这些权限中是否有 没有授权需要提示的
     */
    public static boolean shouldShowPermissions(Activity activity, String[] permission) {
        for (String value : permission) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    value)) {
                return true;
            }
        }
        return false;
    }

}
