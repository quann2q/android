package androidmads.n2q.SmsManagement;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import androidmads.n2q.Database.Database;
import androidmads.n2q.Fragment.FragmentSms;
import androidmads.n2q.Helper.Utils;

@TargetApi(Build.VERSION_CODES.N)
public class SmsReceiver extends BroadcastReceiver {
    Database database;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm");
    SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
    @Override
    public void onReceive(Context context, Intent intent) {
        database = new Database(context, "EmailManagement.sqlite", null, Utils.VERSION_SQL);
        String time, date;
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String phoneNumber = "";
        String message = "";
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null){
                throw new AssertionError();
            } else {
                msgs = new SmsMessage[pdus.length];
                for (int i=0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    phoneNumber = "" + msgs[i].getOriginatingAddress();
                    message += msgs[i].getMessageBody();
                }
                time = simpleTime.format(calendar.getTime());
                date = simpleDate.format(calendar.getTime());
                if (Utils.isNumber(phoneNumber)){
                    phoneNumber = SmsFormatPhone.formatPhone(phoneNumber);
                }
                sqlInsertSms(phoneNumber, message, time, date, Utils.SMS_STATUS_RECEIVER);
                FragmentSms fragmentSms = new FragmentSms(context);
                fragmentSms.loadDatabaseSms("SELECT * FROM Sms");
            }
        }
    }

    public void sqlInsertSms(String phoneNumbers, String message,
                             String time, String date, int status){
        database.queryDataSQL("INSERT INTO Sms VALUES(null, '" + phoneNumbers
                + "', '" + message + "', '" + time + "', '" + date + "', '" + status + "')");
    }

}