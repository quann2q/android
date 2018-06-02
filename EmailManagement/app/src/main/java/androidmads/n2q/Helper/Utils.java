package androidmads.n2q.Helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;

import java.text.Normalizer;

public class Utils {

    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    public static final int RESULT_INTENT1 = 10;
    public static final int RESULT_INTENT2 = 9;
    public static final int STATUS_ALARM = 0;
    public static final int STATUS_SEND = 1;
    public static final int SMS_STATUS_RECEIVER = 2;
    public static final int REQUEST_INTENT = 999;
    public static final int NAME_CONTACT_EDIT = 111;
    public static final int VERSION_SQL = 1;
    public static final int NOTIFICATION = 1000;
    public static final int NAME_EMAIL_SHOW_DETAILS = 1002;
    public static final int NAME_SMS_SHOW_DETAILS = 1003;
    public static final int NAME_SMS_SHOW_DETAILS_REPLY = 1004;
    public static final String CONTACT_EDIT = "Chỉnh sửa";
    public static final String FILE_NAME_GOOGLE = "account.com";
    public static final String FILE_NAME_CONTACT = "contact.com";

    public static boolean isNotEmpty(EditText editText) {
        return editText.getText().toString().trim().length() > 0;
    }

    @NonNull
    public static String getString(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static boolean checkPermission(Context context, String permission) {
        if (isMarshmallow()) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public static boolean isMarshmallow() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static String lengthString(String string){
        return String.valueOf(string.length());
    }

    // Kiểm tra string có là số hay không
    public static boolean isNumber(String string){
        int length = string.length();
        if (length > 0){
            for (int i=0; i<length; i++){
                if ((string.charAt(i) < '0' || string.charAt(i) > '9')
                        && string.charAt(i) != '+'){
                    return false;
                }
            }
        }
        return true;
    }

    // Kiểm tra string có là bảng mã ASCII
    public static boolean isStringAscii(String string){
        int length = string.length();
        for (int i=0; i<length; i++){
            if (Character.UnicodeBlock.of(string.charAt(i)) != Character.UnicodeBlock.BASIC_LATIN){
                return false;
            }
        }
        return true;
    }

    // Chuyển về không dấu
    public static String convertString(String string){
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return string;
    }

    // Tìm kiếm không phân biệt chữ hoa, chữ thường
    public static boolean isContain(String origin, String key){
        String originTemp = origin;
        if (Utils.isStringAscii(key)){
            origin = convertString(origin);
        } else {
            origin = originTemp;
        }
        if (origin.contains(key)){
            return true;
        } else {
            for(int i=0; i<key.length(); i++){
                String k = key.charAt(i) + "";
                for (int j = 0; j<origin.length(); j++){
                    String o = origin.charAt(j) + "";
                    if (k.equalsIgnoreCase(o)){
                        int index = origin.indexOf(o);
                        origin = origin.substring(index);
                        break;
                    }
                    if (j == origin.length()-1){
                        return false;
                    }
                }
            }
            return true;
        }
    }

}
