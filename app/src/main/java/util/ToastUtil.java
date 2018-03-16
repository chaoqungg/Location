package util;

import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import application.StartApplication;


/**
 * Created by QHT on 2017-02-27.
 */
public class ToastUtil {

    private static Toast toast;
    /**
     * 自定义Toast
     *
     * @param message
     */
    public static void showToastShort(CharSequence message) {
        if (toast == null) {
            toast = Toast.makeText(StartApplication.getInstance(),
                    message,Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
