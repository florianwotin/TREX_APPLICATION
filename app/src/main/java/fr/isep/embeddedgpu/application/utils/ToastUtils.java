package fr.isep.embeddedgpu.application.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class ToastUtils {
    public static Toast shortToast(Context context, String message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public static Toast shortToast(Context context, @StringRes int resId) {
        return Toast.makeText(context, resId, Toast.LENGTH_SHORT);
    }

    public static Toast longToast(Context context, String message) {
        return Toast.makeText(context, message, Toast.LENGTH_LONG);
    }

    public static Toast longToast(Context context, @StringRes int resId) {
        return Toast.makeText(context, resId, Toast.LENGTH_LONG);
    }
}
