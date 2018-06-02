package androidmads.n2q.SmsManagement;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;

import androidmads.n2q.ContactManagement.ContactPager;
import androidmads.n2q.Database.Database;
import androidmads.n2q.Fragment.FragmentSms;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;

@TargetApi(Build.VERSION_CODES.N)
public class SmsNewActivity extends Activity {
    Database database = new Database(this, "EmailManagement.sqlite", null, Utils.VERSION_SQL);
    int countIdSms = 0;
    int statusSms = 0;
    int id;
    int nameActivity;
    TextView txtLengthContent;
    EditText edtSmsSoDienThoai, edtSmsNoiDung;
    ImageButton imgbtnSmsSend, imgbtnSmsSchedule, imgbtnBack, imgbtnContact;
    View layoutSmsSchedule;
    TextView txtSelectDate;
    TextView txtSelectTime;

    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleGio = new SimpleDateFormat("HH:mm");
    SimpleDateFormat simpleNgay = new SimpleDateFormat("dd/MM/yyyy");

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    final int READ_CONTACTS_PERMISSION_REQUEST_CODE = 222;
    FragmentSms fragmentSms = new FragmentSms(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_new_activity);
        init();

        final Intent intent = getIntent();
        id = intent.getIntExtra("ID", Utils.REQUEST_INTENT);
        nameActivity = intent.getIntExtra("SmsEdit", Utils.REQUEST_INTENT);
        if (nameActivity == Utils.NAME_SMS_SHOW_DETAILS_REPLY){
            edtSmsSoDienThoai.setText(intent.getStringExtra("SoDienThoai"));
            edtSmsNoiDung.requestFocus();
            layoutSmsSchedule.setVisibility(View.GONE);
        } else if (nameActivity == Utils.NAME_SMS_SHOW_DETAILS) {
            Cursor count = database.getDataSQL("SELECT * FROM Sms WHERE Id = " + id + "");
            while(count.moveToNext()){
                countIdSms++;
            }
            edtSmsNoiDung.setText(intent.getStringExtra("NoiDung"));
            statusSms = intent.getIntExtra("TrangThai", Utils.REQUEST_INTENT);
            if (statusSms == Utils.STATUS_ALARM){
                edtSmsSoDienThoai.setText(intent.getStringExtra("SoDienThoai"));
                edtSmsSoDienThoai.setSelection(edtSmsSoDienThoai.getText().length());
                layoutSmsSchedule.setVisibility(View.VISIBLE);
                txtSelectTime.setText(intent.getStringExtra("Gio"));
                txtSelectDate.setText(intent.getStringExtra("Ngay"));
            } else if (statusSms == Utils.STATUS_SEND){
                edtSmsSoDienThoai.setText(intent.getStringExtra("SoDienThoai"));
                edtSmsSoDienThoai.setSelection(edtSmsSoDienThoai.getText().length());
                layoutSmsSchedule.setVisibility(View.GONE);
            } else if (statusSms == Utils.SMS_STATUS_RECEIVER){
                layoutSmsSchedule.setVisibility(View.GONE);
            }
        }

        if (layoutSmsSchedule.getVisibility() == View.GONE){
            imgbtnSmsSend.setBackgroundResource(R.drawable.ic_send_white_100);
        } else if (layoutSmsSchedule.getVisibility() == View.VISIBLE){
            imgbtnSmsSend.setBackgroundResource(R.drawable.ic_checkmark_white_100);
        }

        String phone = intent.getStringExtra("DetailsPhone");
        if (phone != null){
            if (Utils.isNotEmpty(edtSmsSoDienThoai)){
                edtSmsSoDienThoai.setText(edtSmsSoDienThoai.getText() + phone);
            } else {
                edtSmsSoDienThoai.setText(phone);
            }
        }

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(!checkPermission(Manifest.permission.SEND_SMS)) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        imgbtnSmsSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendSms();
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

        imgbtnSmsSchedule.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                txtSelectTime.setText(" " + simpleGio.format(calendar.getTime()) + " ");
                txtSelectDate.setText(" " + simpleNgay.format(calendar.getTime()) + " ");

                if(layoutSmsSchedule.getVisibility() == View.GONE){
                    layoutSmsSchedule.setVisibility(View.VISIBLE);
                    imgbtnSmsSend.setBackgroundResource(R.drawable.ic_checkmark_white_100);
                } else {
                    layoutSmsSchedule.setVisibility(View.GONE);
                    imgbtnSmsSend.setBackgroundResource(R.drawable.ic_send_white_100);
                }
            }
        });

        imgbtnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkPermission(Manifest.permission.READ_CONTACTS)) {
                    ActivityCompat.requestPermissions(SmsNewActivity.this, new String[] {Manifest.permission.READ_CONTACTS},
                            READ_CONTACTS_PERMISSION_REQUEST_CODE);
                } else {
                    Intent intentPager = new Intent(SmsNewActivity.this, ContactPager.class);
                    startActivityForResult(intentPager, Utils.RESULT_INTENT2);
                }
            }
        });

        edtSmsSoDienThoai.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                while (edtSmsSoDienThoai.getText().toString().contains("  ")){
                    edtSmsSoDienThoai.setText(edtSmsSoDienThoai.getText().toString().replace("  ", " "));
                    edtSmsSoDienThoai.setSelection(edtSmsSoDienThoai.getText().length());
                }
                while (edtSmsSoDienThoai.getText().toString().contains(",,")){
                    edtSmsSoDienThoai.setText(edtSmsSoDienThoai.getText().toString().replace(",,", ","));
                    edtSmsSoDienThoai.setSelection(edtSmsSoDienThoai.getText().length());
                }
                while (edtSmsSoDienThoai.getText().toString().contains(", ,")){
                    edtSmsSoDienThoai.setText(edtSmsSoDienThoai.getText().toString().replace(", ,", ", "));
                    edtSmsSoDienThoai.setSelection(edtSmsSoDienThoai.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edtSmsNoiDung.addTextChangedListener(new TextWatcher() {
            int countLength = 0;
            int length = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtLengthContent.setVisibility(View.GONE);
                length = edtSmsNoiDung.getText().length();
                if (Utils.isStringAscii(edtSmsNoiDung.getText().toString())){
                    if (length >= 130){
                        txtLengthContent.setVisibility(View.VISIBLE);
                    }
                    countLength = ((length-1) / 160) + 1;
                } else {
                    if (length >= 40){
                        txtLengthContent.setVisibility(View.VISIBLE);
                    }
                    countLength = ((length-1) / 70) + 1;
                }

                String show = String.valueOf(length) + "/" + String.valueOf(countLength);
                txtLengthContent.setText(show);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.RESULT_INTENT2 && data != null){
            String phoneNumber = data.getStringExtra("phoneNumber");
            if (!edtSmsSoDienThoai.getText().toString().isEmpty()
                    && !phoneNumber.isEmpty()){
                edtSmsSoDienThoai.setText(edtSmsSoDienThoai.getText() + ", " + phoneNumber);
                edtSmsSoDienThoai.setSelection(edtSmsSoDienThoai.getText().length());
            } else {
                edtSmsSoDienThoai.setText(edtSmsSoDienThoai.getText() + phoneNumber);
                edtSmsSoDienThoai.setSelection(edtSmsSoDienThoai.getText().length());
            }
        }
    }

    public void sendSms(){
        String time, date;
        String phoneNumbers = edtSmsSoDienThoai.getText().toString();
        if (phoneNumbers.length() > 0){
            phoneNumbers = SmsFormatPhone.formatPhone(phoneNumbers);
        }
        String message = edtSmsNoiDung.getText().toString();

        if (!phoneNumbers.isEmpty() && !message.isEmpty() &&
                layoutSmsSchedule.getVisibility() == View.GONE){
            time = simpleGio.format(calendar.getTime());
            date = simpleNgay.format(calendar.getTime());
            if(checkPermission(Manifest.permission.SEND_SMS)){
                SmsSend smsSend = new SmsSend();
                smsSend.sendSMS(phoneNumbers, message);
                if (countIdSms > 0){
                    if (statusSms == Utils.SMS_STATUS_RECEIVER){
                        sqlInsertSms(phoneNumbers, message, time, date, Utils.STATUS_SEND);
                    } else {
                        sqlUpdateSms(phoneNumbers, message, time, date, Utils.STATUS_SEND, id);
                    }
                    finish();
                } else {
                    sqlInsertSms(phoneNumbers, message, time, date, Utils.STATUS_SEND);
                }
                Toast.makeText(SmsNewActivity.this, "Gửi thành công", Toast.LENGTH_SHORT).show();
                edtSmsNoiDung.setText("");
                fragmentSms.loadDatabaseSms("SELECT * FROM Sms");
            } else {
                Toast.makeText(SmsNewActivity.this,
                        "Xin quyền thất bại", Toast.LENGTH_SHORT).show();
            }
        } else if (!phoneNumbers.isEmpty() && !message.isEmpty() &&
                layoutSmsSchedule.getVisibility() == View.VISIBLE){
            time = txtSelectTime.getText().toString();
            date = txtSelectDate.getText().toString();
            if (countIdSms > 0){
                if (statusSms == Utils.SMS_STATUS_RECEIVER){
                    sqlInsertSms(phoneNumbers, message, time, date, Utils.STATUS_ALARM);
                    Toast.makeText(SmsNewActivity.this, "Đã thêm vào danh sách chờ", Toast.LENGTH_SHORT).show();
                } else {
                    sqlUpdateSms(phoneNumbers, message, time, date, Utils.STATUS_ALARM, id);
                    Toast.makeText(SmsNewActivity.this, "Đã cập nhật danh sách chờ", Toast.LENGTH_SHORT).show();
                }
            } else {
                sqlInsertSms(phoneNumbers, message, time, date, Utils.STATUS_ALARM);
                Toast.makeText(SmsNewActivity.this, "Đã thêm vào danh sách chờ", Toast.LENGTH_SHORT).show();
            }

            // setAlarmSms
            FragmentSms fragmentSms = new FragmentSms();
            try {
                fragmentSms.setCalendarSms(this);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            fragmentSms.loadDatabaseSms(this, "SELECT * FROM Sms");
            finish();
        } else {
            Toast.makeText(SmsNewActivity.this,
                    "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }

    public void sqlUpdateSms(String phoneNumbers, String message,
                              String time, String date, int status ,int id){
        database.queryDataSQL("UPDATE Sms SET " +
                "SoDienThoai = '" + phoneNumbers + "', " +
                "NoiDung = '" + message + "', " +
                "Gio = '" + time + "', " +
                "Ngay = '" + date + "', " +
                "TrangThai = '" + status + "' " +
                "WHERE Id = '" + id + "'");
    }

    public void sqlInsertSms(String phoneNumbers, String message,
                              String time, String date, int status){
        database.queryDataSQL("INSERT INTO Sms VALUES(null, '" + phoneNumbers
                + "', '" + message + "', '" + time + "', '" + date + "', '" + status + "')");
    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SEND_SMS_PERMISSION_REQUEST_CODE: {
                if(grantResults.length > 0 &&
                        (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    imgbtnSmsSend.setEnabled(true);
                }
                break;
            }
        }
    }

    private void init(){
        txtLengthContent  = (TextView) findViewById(R.id.txtLengthContent);
        edtSmsSoDienThoai = (EditText) findViewById(R.id.edtSmsTo);
        edtSmsNoiDung     = (EditText) findViewById(R.id.edtSmsNoiDung);
        imgbtnSmsSend     = (ImageButton) findViewById(R.id.imgbtnSmsSend);
        imgbtnSmsSchedule = (ImageButton) findViewById(R.id.imgbtnSmsSchedule);
        imgbtnBack        = (ImageButton) findViewById(R.id.imgbtnBack);
        txtSelectTime     = (TextView) findViewById(R.id.txtSmsSelectTime);
        txtSelectDate     = (TextView) findViewById(R.id.txtSmsSelectDate);
        layoutSmsSchedule = findViewById(R.id.layoutSmsSchedule);
        imgbtnContact     = (ImageButton) findViewById(R.id.imgbtnContact);
    }

    private void selectTime(){
        int gio = calendar.get(Calendar.HOUR_OF_DAY);
        int phut = calendar.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(0, 0, 0, hourOfDay, minute);
                txtSelectTime.setText(" " + simpleGio.format(calendar.getTime()) + " ");
            }
        },gio, phut, true);

        timePickerDialog.show();
    }

    private void selectDate(){
        int ngay = calendar.get(Calendar.DATE);
        int thang = calendar.get(Calendar.MONTH);
        int nam = calendar.get(Calendar.YEAR);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                txtSelectDate.setText(" " + simpleNgay.format(calendar.getTime()) + " ");
            }
        },nam, thang, ngay);
        datePickerDialog.show();
    }

}
