package com.hikvision.lg.api;

import android.app.Activity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>类说明<p>
 *
 * @author ligang14  2018/11/28
 * @version V1.0
 * @name BindViewTools
 */
public class BindViewTools {
    public static void bind(Activity activity) {
        Class clazz = activity.getClass();
        try {
            Class bindViewClass = Class.forName(clazz.getName() + "_ViewBinding");
            Method method = bindViewClass.getMethod("bind", activity.getClass());
            method.invoke(bindViewClass.newInstance(), activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
