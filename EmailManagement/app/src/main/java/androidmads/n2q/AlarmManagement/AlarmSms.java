package androidmads.n2q.AlarmManagement;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import java.text.ParseException;

import androidmads.n2q.Database.Database;
import androidmads.n2q.Fragment.FragmentSms;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;
import androidmads.n2q.SmsManagement.SmsSend;
import androidmads.n2q.SmsManagement.SmsShowDetails;

public class AlarmSms extends BroadcastReceiver {
    Database database;
    int smsId;
    String smsPhoneNumber;
    String smsContent;
    String smsTime;
    String smsDate;
    int smsStatus;

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        database = new Database(context, "EmailManagement.sqlite", null, Utils.VERSION_SQL);

        smsId          = intent.getIntExtra("smsId", 0);
        smsPhoneNumber = intent.getStringExtra("smsPhoneNumber");
        smsContent     = intent.getStringExtra("smsContent");
        smsTime        = intent.getStringExtra("smsTime");
        smsDate        = intent.getStringExtra("smsDate");
        smsStatus      = intent.getIntExtra("smsStatus", 0);

        SmsSend smsSend = new SmsSend();
        smsSend.sendSMS(smsPhoneNumber, smsContent);

        setNotificationSmsV2(context, smsId, smsPhoneNumber,
                smsContent, smsTime, smsDate, Utils.STATUS_SEND);

        sqlUpdateSms(Utils.STATUS_SEND, smsId);

        FragmentSms fragmentSms = new FragmentSms();
        try {
            fragmentSms.setCalendarSms(context);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        fragmentSms.loadDatabaseSms(context, "SELECT * FROM Sms");
    }

    public void sqlUpdateSms(int status ,int id){
        database.queryDataSQL("UPDATE Sms SET TrangThai = '" + status + "' " +
                "WHERE Id = '" + id + "'");
    }

    public void setNotificationSms(Context context, String phoneNumber, String content){
        Intent intent = new Intent();
        intent.setAction("SETACTION");
        intent.putExtra("SETACTION", 0);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(phoneNumber)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_app, context.getString(R.string.sended),
                        pendingIntent)
                .setSmallIcon(R.drawable.ic_app)
                .setLargeIcon(BitmapFactory
                        .decodeResource(context.getResources(), R.drawable.ic_app));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(22, mBuilder.build());
    }

    public void setNotificationSmsV2(Context context, int id, String phoneNumber,
                                     String content, String time, String date, int smsStatus){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(phoneNumber);
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_app);
        builder.setLargeIcon(BitmapFactory
                .decodeResource(context.getResources(), R.drawable.ic_app));
        builder.setTicker("SMS");
        builder.setAutoCancel(true);

        Intent intent  = new Intent(context, SmsShowDetails.class);
        intent.putExtra("Ten", "AlarmSms");
        intent.putExtra("ID", id);
        intent.putExtra("SoDienThoai", phoneNumber);
        intent.putExtra("NoiDung", content);
        intent.putExtra("Gio", time);
        intent.putExtra("Ngay", date);
        intent.putExtra("TrangThai", smsStatus);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(SmsShowDetails.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.notify(Utils.NOTIFICATION, notification);
    }

}
