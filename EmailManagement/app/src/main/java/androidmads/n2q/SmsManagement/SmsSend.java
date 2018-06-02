package androidmads.n2q.SmsManagement;

import android.telephony.SmsManager;
import android.util.Log;

import androidmads.n2q.Helper.Utils;

public class SmsSend {
    public void sendSMS(String phoneNumbers, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        String[] items = phoneNumbers.split(",");
        for (String item : items){
            String phoneNumber = SmsFormatPhone.formatPhoneSend(item.trim());
            if (!phoneNumber.equals("")){
                StringBuilder builderMessage = new StringBuilder(message);
                if (Utils.isStringAscii(message)){
                    while (builderMessage.length() > 160){
                        String mess = builderMessage.substring(0, 160);
                        smsManager.sendTextMessage(phoneNumber.trim(), null, mess, null, null);
                        builderMessage.delete(0, 160);
                    }
                } else {
                    while (builderMessage.length() > 70){
                        String mess = builderMessage.substring(0, 70);
                        smsManager.sendTextMessage(phoneNumber.trim(), null, mess, null, null);
                        builderMessage.delete(0, 70);
                    }
                }
                smsManager.sendTextMessage(phoneNumber.trim(), null,
                                                        builderMessage.toString(), null, null);
            }
        }
    }
}







