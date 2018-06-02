package androidmads.n2q.SmsManagement;

public class SmsFormatPhone {

    // "098 abc 76  543 21  ,,,  ,, +  8  41234 5   6789" -> "0987654321, 0123456789"
    public static String formatPhone(String phone){
        phone = phone.trim();
        int length = phone.length();
        for (int i=0; i<length; i++){
            if ((phone.charAt(i) < 48 || phone.charAt(i) > 57) && phone.charAt(i) != 44){
                char c = phone.charAt(i);
                phone = phone.replace(c + "", " ");
            }
        }

        while (phone.contains(" ")){
            phone = phone.replace(" ", "");
        }

        while (phone.contains(",,")){
            phone = phone.replace(",,", ",");
        }

        while (phone.contains("84")){
            phone = phone.replace("84", "0");
        }

        StringBuilder stringBuilder = new StringBuilder(phone);
        if (stringBuilder.charAt(0) == ','){
            stringBuilder.delete(0, 1);
        }

        if (!stringBuilder.toString().equals("")
                                    && stringBuilder.charAt(stringBuilder.length()-1) == ','){
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
        }

        int i=0;
        while (i < stringBuilder.length()){
            if (stringBuilder.charAt(i) == ','){
                stringBuilder.insert(i+1, " ");
            }
            i++;
        }
        phone = stringBuilder.toString();

        return phone;
    }

    // "0987654321" -> "098 765 43 21"  | "01234567890" -> "0123 456 7890"
    public static String formatPhoneSend(String phone){
        int length = phone.length();
        StringBuilder stringBuilder = new StringBuilder(phone);
        if (length == 10){
            stringBuilder.insert(3, " ");
            stringBuilder.insert(7, " ");
            stringBuilder.insert(10, " ");

            phone = stringBuilder.toString();
            return phone;
        } else if (length == 11){
            stringBuilder.insert(4, " ");
            stringBuilder.insert(8, " ");
            phone = stringBuilder.toString();

            return phone;
        } else if (length > 2) {
            stringBuilder.insert(2, " ");
            phone = stringBuilder.toString();

            return phone;
        }
        return phone;
    }

}
