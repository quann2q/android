package androidmads.n2q.EmailManagement;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import androidmads.n2q.Database.Database;
import androidmads.n2q.Fragment.FragmentGmail;
import androidmads.n2q.R;
import androidmads.n2q.Helper.InternetDetector;
import androidmads.n2q.Helper.Utils;

@TargetApi(Build.VERSION_CODES.N)
public class EmailNewActivity extends AppCompatActivity {
    ImageButton sendFabButton, imgViewBack;
    EditText edtDiaChi, edtTieuDe, edtNoiDung, edtAttachmentData, edtFromAddress;
    TextView txtSelectDate;
    TextView txtSelectTime;
    View layoutSchedule;
    ImageView imgViewPic;
    Database database = new Database(this, "EmailManagement.sqlite", null, Utils.VERSION_SQL);
    int id;
    int countEmailId = 0;
    int nameActivity;

    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    Calendar calendar           = Calendar.getInstance();
    SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm");
    SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");

    public GoogleAccountCredential mCredential;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_COMPOSE,
            GmailScopes.GMAIL_INSERT,
            GmailScopes.GMAIL_MODIFY,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.MAIL_GOOGLE_COM
    };
    private InternetDetector internetDetector;
    private final int SELECT_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_new_activity);

        init();

        Intent intent = getIntent();
        nameActivity = intent.getIntExtra("EmailEdit", Utils.REQUEST_INTENT);
        id = intent.getIntExtra("ID", Utils.REQUEST_INTENT);
        edtDiaChi.setText(intent.getStringExtra("DiaChi"));
        edtDiaChi.setSelection(edtDiaChi.getText().length());
        edtTieuDe.setText(intent.getStringExtra("TieuDe"));
        edtNoiDung.setText(intent.getStringExtra("NoiDung"));
        if (intent.getIntExtra("TrangThai", Utils.REQUEST_INTENT) == Utils.STATUS_ALARM) {
            layoutSchedule.setVisibility(View.VISIBLE);
            txtSelectTime.setText(intent.getStringExtra("Gio"));
            txtSelectDate.setText(intent.getStringExtra("Ngay"));
        } else if (intent.getIntExtra("TrangThai", Utils.REQUEST_INTENT) == Utils.STATUS_SEND) {
            layoutSchedule.setVisibility(View.GONE);
        }
        if (nameActivity == Utils.NAME_EMAIL_SHOW_DETAILS) {
            Cursor count = database.getDataSQL("SELECT * FROM Email WHERE Id = " + id + "");
            while (count.moveToNext()) {
                countEmailId++;
            }
        }

        if (layoutSchedule.getVisibility() == View.GONE) {
            sendFabButton.setBackgroundResource(R.drawable.ic_send_white_100);
        } else if (layoutSchedule.getVisibility() == View.VISIBLE) {
            sendFabButton.setBackgroundResource(R.drawable.ic_checkmark_white_100);
        }

        findViewById(R.id.attachment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                } else {
                    ActivityCompat.requestPermissions(EmailNewActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_PHOTO);
                }
            }
        });

        findViewById(R.id.changeAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(mCredential.newChooseAccountIntent(), Utils.REQUEST_ACCOUNT_PICKER);
            }
        });

        findViewById(R.id.imgbtnSchedule).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                txtSelectTime.setText(" " + simpleTime.format(calendar.getTime()) + " ");
                txtSelectDate.setText(" " + simpleDate.format(calendar.getTime()) + " ");

                if (layoutSchedule.getVisibility() == View.GONE) {
                    layoutSchedule.setVisibility(View.VISIBLE);
                    sendFabButton.setBackgroundResource(R.drawable.ic_checkmark_white_100);
                } else {
                    layoutSchedule.setVisibility(View.GONE);
                    sendFabButton.setBackgroundResource(R.drawable.ic_send_white_100);
                }
            }
        });

        txtSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTime();
            }
        });

        txtSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });

        sendFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResultsFromApi(
                        edtDiaChi.getText().toString(),
                        edtTieuDe.getText().toString(),
                        edtNoiDung.getText().toString(),
                        txtSelectTime.getText().toString(),
                        txtSelectDate.getText().toString()
                );
            }
        });

        imgViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    } // End onCreate();

    private void selectTime() {
        int gio = calendar.get(Calendar.HOUR_OF_DAY);
        int phut = calendar.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(0, 0, 0, hourOfDay, minute);
                txtSelectTime.setText(" " + simpleTime.format(calendar.getTime()) + " ");
            }
        }, gio, phut + 1, true);
        timePickerDialog.show();
    }

    private void selectDate() {
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                txtSelectDate.setText(" " + simpleDate.format(calendar.getTime()) + " ");
            }
        }, nam, thang, ngay);
        datePickerDialog.show();
    }

    private void init() {
        txtSelectDate     = (TextView) findViewById(R.id.txtEmailSelectDate);
        txtSelectTime     = (TextView) findViewById(R.id.txtEmailSelectTime);
        layoutSchedule    = findViewById(R.id.layoutEmailSchedule);
        edtFromAddress    = (EditText) findViewById(R.id.edt_from_address);
        imgViewBack       = (ImageButton) findViewById(R.id.imgbtnBack);
        imgViewPic        = (ImageView) findViewById(R.id.imgViewPic);
        sendFabButton     = (ImageButton) findViewById(R.id.fab);
        edtDiaChi         = (EditText) findViewById(R.id.to_address);
        edtTieuDe         = (EditText) findViewById(R.id.subject);
        edtNoiDung        = (EditText) findViewById(R.id.body);
        edtAttachmentData = (EditText) findViewById(R.id.attachmentData);

        // Khởi tạo kiểm tra Internet
        internetDetector = new InternetDetector(getApplicationContext());

        // Khởi tạo GoogleAccountCredential sử dụng OAuth2
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    private void showMessage(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public void getResultsFromApi(String address, String title,
                                  String content, String time, String date) {
        SimpleDateFormat simpleTime = new SimpleDateFormat("HH:mm");
        SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy");
        if (address.length() > 1){
            address = EmailFormatAddress.formatAddress(address);
        }
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!internetDetector.checkMobileInternetConn()) {
            Toast.makeText(this, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();
        } else if (address.trim().isEmpty() && title.trim().isEmpty() && content.trim().isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else if (layoutSchedule.getVisibility() == View.GONE) {
            String timeNow = simpleTime.format(calendar.getTime());
            String dateNow = simpleDate.format(calendar.getTime());
            new MakeRequestTask(getApplication(), mCredential, address, title, content).execute();
            if (countEmailId > 0){
                sqlUpdateEmail(address, title, content, timeNow, dateNow, Utils.STATUS_SEND, id);
            } else {
                sqlInsertEmail(address, title, content, timeNow, dateNow, Utils.STATUS_SEND);
            }
            FragmentGmail.loadDatabaseEmail("SELECT * FROM Email");
            finish();

        } else if (layoutSchedule.getVisibility() == View.VISIBLE) {
            if (countEmailId > 0) {
                sqlUpdateEmail(address, title, content, time, date, Utils.STATUS_ALARM, id);
                Toast.makeText(this, "Đã cập nhật danh sách chờ", Toast.LENGTH_SHORT).show();
            } else {
                sqlInsertEmail(address, title, content, time, date, Utils.STATUS_ALARM);
                Toast.makeText(this, "Đã thêm vào danh sách chờ", Toast.LENGTH_SHORT).show();
            }

            // setAlarmGmail
            FragmentGmail fragmentGmail = new FragmentGmail();
            try {
                fragmentGmail.setCalendarGmail(this);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            FragmentGmail.loadDatabaseEmail("SELECT * FROM Email");
            finish();
        }
    }

    private void sqlUpdateEmail(String address, String title, String content,
                                String time, String date, int status, int id) {
        database.queryDataSQL("UPDATE Email SET " +
                "DiaChi = '" + address + "', " +
                "TieuDe = '" + title + "', " +
                "NoiDung = '" + content + "', " +
                "Gio = '" + time + "', " +
                "Ngay = '" + date + "', " +
                "TrangThai = " + status + " " +
                "WHERE Id = '" + id + "'");
    }

    private void sqlInsertEmail(String address, String title, String content,
                                String time, String date, int status) {
        database.queryDataSQL("INSERT INTO Email VALUES(null, '" +
                address + "', '" +
                title + "', '" +
                content + "', '" +
                time + "', '" +
                date + "', " +
                status + ")");
    }

    // Phương thức kiểm tra Google Play Service khả dụng
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    // Phương thức hiện thông tin nếu Google Play Service không khả dụng
    public void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    // Phương thức báo lỗi Google Play Services
    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                EmailNewActivity.this,
                connectionStatusCode,
                Utils.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    // Chọn tài khoản
    public void chooseAccount() {
        if (Utils.checkPermission(getApplicationContext(), Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
            } else {
                // Khởi tạo dialog khi người dùng nhấn vào chọn tài khoản
                startActivityForResult(mCredential.newChooseAccountIntent(), Utils.REQUEST_ACCOUNT_PICKER);
            }
        } else {
            ActivityCompat.requestPermissions(EmailNewActivity.this,
                    new String[]{Manifest.permission.GET_ACCOUNTS}, Utils.REQUEST_PERMISSION_GET_ACCOUNTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Utils.REQUEST_PERMISSION_GET_ACCOUNTS:
                chooseAccount();
                break;
            case SELECT_PHOTO:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Utils.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    showMessage(sendFabButton, "Vui lòng cập nhật Google Play Service " +
                            "cho thiết bị của bạn và mở lại ứng dụng");
                }
                break;
            case Utils.REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                    }
                }
                break;
        }
    }

    // Gửi Email sử dụng GMail OAuth2
    public class MakeRequestTask extends AsyncTask<Void, Void, String> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;
        private Context context;
        private String address;
        private String title;
        private String content;
        GoogleAccountCredential credential;

        public MakeRequestTask(Context context, GoogleAccountCredential credential,
                               String address, String title, String content) {
            this.context = context;
            this.address = address;
            this.title   = title;
            this.content = content;
            this.credential = credential;

            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, this.credential)
                    .setApplicationName("Email Management")
                    .build();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private String getDataFromApi() throws IOException {
            // Lấy giá trị Địa chỉ người nhận, Địa chỉ người gửi, Tiêu đề, Nội dung
            String user = "me";
            String from = credential.getSelectedAccountName();
            MimeMessage mimeMessage;
            String response = "";
            try {
                String[] items = address.split(",");
                for (String addressTo : items) {
                    mimeMessage = createEmail(addressTo.trim(), from, title.trim(), content.trim());
                    response    = sendMessage(mService, user, mimeMessage);
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return response;
        }

        // Phương thức gửi email
        private String sendMessage(Gmail service, String userId, MimeMessage email)
                                            throws MessagingException, IOException {
            Message message = createMessageWithEmail(email);
            message = service.users().messages().send(userId, message).execute();
            return message.getId();
        }

        // Phương thức tạo email Params
        private MimeMessage createEmail(String to, String from, String subject, String bodyText)
                                                                throws MessagingException {
            Properties props         = new Properties();
            Session session          = Session.getDefaultInstance(props, null);
            MimeMessage email        = new MimeMessage(session);
            InternetAddress tAddress = new InternetAddress(to);
            InternetAddress fAddress = new InternetAddress(from);

            email.setFrom(fAddress);
            email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
            email.setSubject(subject);

            // Tạo đối tượng Multipart và thêm các đối tượng MimeBodyPart vào đối tượng
            Multipart multipart = new MimeMultipart();

            // Thêm văn bản
            email.setText(bodyText);

            BodyPart textBody = new MimeBodyPart();
            textBody.setText(bodyText);
            multipart.addBodyPart(textBody);

            //Đặt multipart cho email
            email.setContent(multipart);
            return email;
        }

        private Message createMessageWithEmail(MimeMessage email)
                throws MessagingException, IOException {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            email.writeTo(bytes);
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
            Message message = new Message();
            message.setRaw(encodedEmail);
            return message;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(context, "Đang gửi...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(String output) {
            if (output == null || output.length() == 0) {
                Toast.makeText(context, "Không có kết quả", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Gửi thành công", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Utils.REQUEST_AUTHORIZATION);
                } else {
                    Toast.makeText(context, "Có lỗi xảy ra, vui lòng kiểm tra lại",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Đã hủy", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

