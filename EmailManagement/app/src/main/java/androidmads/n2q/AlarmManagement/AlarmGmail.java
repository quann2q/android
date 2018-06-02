package androidmads.n2q.AlarmManagement;

import android.Manifest;
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
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;

import java.text.ParseException;
import java.util.Arrays;

import androidmads.n2q.Database.Database;
import androidmads.n2q.Database.FileManager;
import androidmads.n2q.EmailManagement.EmailNewActivity;
import androidmads.n2q.EmailManagement.EmailShowDetails;
import androidmads.n2q.Fragment.FragmentGmail;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;

public class AlarmGmail extends BroadcastReceiver {
    Context context;
    private String accountNameGmail;
    GoogleAccountCredential mCredential;
    private static final String[] SCOPES = {
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_COMPOSE,
            GmailScopes.GMAIL_INSERT,
            GmailScopes.GMAIL_MODIFY,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.MAIL_GOOGLE_COM
    };

    Database database;

    int idGmail = 0;
    String addressGmail = null;
    String titleGmail = null;
    String contentGmail = null;
    String dateGmail = null;
    String timeGmail = null;
    int statusGmail = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        EmailNewActivity email = new EmailNewActivity();
        database = new Database(context, "EmailManagement.sqlite", null, Utils.VERSION_SQL);

        idGmail      = intent.getIntExtra("id", Utils.REQUEST_INTENT);
        addressGmail = intent.getStringExtra("address");
        titleGmail   = intent.getStringExtra("title");
        contentGmail = intent.getStringExtra("content");
        dateGmail    = intent.getStringExtra("date");
        timeGmail    = intent.getStringExtra("time");
        statusGmail  = intent.getIntExtra("status", Utils.REQUEST_INTENT);

        FileManager fileManager = new FileManager(context);
        accountNameGmail = fileManager.readData(Utils.FILE_NAME_GOOGLE);

        mCredential = GoogleAccountCredential.usingOAuth2(
                context, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        }
        if (mCredential.getSelectedAccountName() == null) {
            chooseAccount(accountNameGmail);
        }

        // Gửi email
        email.new MakeRequestTask(context, mCredential, addressGmail, titleGmail, contentGmail).execute();

        // Notification
        setNotificationEmailV2(context, idGmail, addressGmail, titleGmail, contentGmail,
                                        dateGmail, timeGmail, 1);

        sqlUpdateEmail(Utils.STATUS_SEND, idGmail);

        // setAlarm
        FragmentGmail fragmentGmail = new FragmentGmail();
        try {
            fragmentGmail.setCalendarGmail(context);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FragmentGmail.loadDatabaseEmail("SELECT * FROM Email");
    }

    private void sqlUpdateEmail(int status, int id) {
        database.queryDataSQL("UPDATE Email SET TrangThai = '" + status + "' WHERE Id = '" + id + "'");
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    public void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            Toast.makeText(context, "Google Play Services không khả dụng", Toast.LENGTH_SHORT).show();
        }
    }

    public void chooseAccount(String accountNameGmail) {
        if (Utils.checkPermission(context, Manifest.permission.GET_ACCOUNTS)) {
            if (accountNameGmail != null) {
                mCredential.setSelectedAccountName(accountNameGmail);
            } else {
                Toast.makeText(context, "Không lấy được địa chỉ người gửi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setNotificationEmail(String address, String content){
        Intent intent = new Intent();
        intent.setAction("SETACTION");
        intent.putExtra("SETACTION", 0);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_app)
                .setLargeIcon(BitmapFactory
                              .decodeResource(context.getResources(), R.drawable.ic_app))
                .setTicker("Gmail")
                .setContentTitle(address)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_app, context.getString(R.string.sended),
                        pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Utils.NOTIFICATION, mBuilder.build());
    }

    public void setNotificationEmailV2(Context context, int idGmail, String addressGmail,
                                       String titleGmail, String contentGmail,
                                       String dateGmail, String timeGmail, int statusGmail){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(addressGmail);
        builder.setContentText(titleGmail);
        builder.setSmallIcon(R.drawable.ic_app);
        builder.setLargeIcon(BitmapFactory
                .decodeResource(context.getResources(), R.drawable.ic_app));
        builder.setTicker("Gmail");
        builder.setAutoCancel(true);

        Intent intent  = new Intent(context, EmailShowDetails.class);
        intent.putExtra("Ten", "AlarmGmail");
        intent.putExtra("ID", idGmail);
        intent.putExtra("DiaChi", addressGmail);
        intent.putExtra("TieuDe", titleGmail);
        intent.putExtra("NoiDung", contentGmail);
        intent.putExtra("Gio", timeGmail);
        intent.putExtra("Ngay", dateGmail);
        intent.putExtra("TrangThai", statusGmail);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(EmailShowDetails.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        manager.notify(Utils.NOTIFICATION, notification);
    }

}
