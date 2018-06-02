package androidmads.n2q.SmsManagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import androidmads.n2q.Fragment.FragmentSms;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;

public class SmsShowDetails extends Activity {
    int id, statusSms;
    String name, phoneNumber, content, time, date;
    TextView txtSmsSoDienThoai, txtSmsNoiDung;
    TextView txtSmsGio, txtSmsNgay;
    ImageView imgViewSmsStatus;
    ImageButton imgbtnEdit, imgbtnDelete, imgbtnBack, imgbtnReply;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_show_details);
        init();

        Intent intent = getIntent();
        id          = intent.getIntExtra("ID", Utils.REQUEST_INTENT);
        name        = intent.getStringExtra("SmsTen");
        phoneNumber = intent.getStringExtra("SoDienThoai");
        content     = intent.getStringExtra("NoiDung");
        time        = intent.getStringExtra("Gio");
        date        = intent.getStringExtra("Ngay");
        statusSms   = intent.getIntExtra("TrangThai", Utils.REQUEST_INTENT);

        if (name != null){
            txtSmsSoDienThoai.setText(name + "\n" + phoneNumber);
        } else {
            txtSmsSoDienThoai.setText(phoneNumber);
        }
        txtSmsNoiDung.setText(content);
        txtSmsGio.setText(time);
        txtSmsNgay.setText(date);
        if (intent.getStringExtra("Ten").equals("AlarmSms")){
            imgbtnDelete.setVisibility(View.GONE);
        } else {
            imgbtnDelete.setVisibility(View.VISIBLE);
        }

        if(statusSms == Utils.STATUS_ALARM){
            imgViewSmsStatus.setImageResource(R.drawable.ic_clock_white_100);
            imgbtnEdit.setVisibility(View.VISIBLE);
            imgbtnEdit.setBackgroundResource(R.drawable.ic_edit_white_100);
            linearLayout.setBackgroundResource(R.drawable.body_email_status_alarm);
        } else if(statusSms == Utils.STATUS_SEND){
            imgViewSmsStatus.setImageResource(R.drawable.ic_checkmark_white_100);
            imgbtnEdit.setVisibility(View.VISIBLE);
            imgbtnEdit.setBackgroundResource(R.drawable.ic_edit_white_100);
            linearLayout.setBackgroundResource(R.drawable.body_email_status_send);
        } else if (statusSms == Utils.SMS_STATUS_RECEIVER){
            imgbtnReply.setVisibility(View.VISIBLE);
            imgViewSmsStatus.setImageResource(R.drawable.ic_down_arrow_white_100);
            imgbtnEdit.setVisibility(View.VISIBLE);
            imgbtnEdit.setBackgroundResource(R.drawable.ic_forward_white_100);
            linearLayout.setBackgroundResource(R.drawable.body_email_status_receiver);
        }

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgbtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentSms fragmentSms = new FragmentSms();
                FragmentSms.commandDataSms("DELETE FROM Sms WHERE Id = " + id);
                fragmentSms.loadDatabaseSms(SmsShowDetails.this, "SELECT * FROM Sms");
                Toast.makeText(SmsShowDetails.this, "Đã xóa", Toast.LENGTH_SHORT).show();

                try {
                    fragmentSms.setCalendarSms(SmsShowDetails.this);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                finish();
            }
        });

        imgbtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SmsShowDetails.this, SmsNewActivity.class);
                intent1.putExtra("SmsEdit", Utils.NAME_SMS_SHOW_DETAILS);
                intent1.putExtra("ID", id);
                intent1.putExtra("SoDienThoai", phoneNumber);
                intent1.putExtra("NoiDung", content);
                intent1.putExtra("Gio", time);
                intent1.putExtra("Ngay", date);
                intent1.putExtra("TrangThai", statusSms);

                startActivity(intent1);
                finish();
            }
        });

        imgbtnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SmsShowDetails.this, SmsNewActivity.class);
                intent1.putExtra("SmsEdit", Utils.NAME_SMS_SHOW_DETAILS_REPLY);
                if (Utils.isNumber(phoneNumber)){
                    intent1.putExtra("SoDienThoai", phoneNumber);
                } else {
                    intent1.putExtra("SoDienThoai", "");
                }
                startActivity(intent1);
                finish();
            }
        });
    }

    private void init(){
        txtSmsSoDienThoai = (TextView) findViewById(R.id.txtSmsSoDienThoai);
        txtSmsNoiDung     = (TextView) findViewById(R.id.txtSmsNoiDung);
        imgViewSmsStatus  = (ImageView) findViewById(R.id.imgViewSmsStatus);
        txtSmsGio         = (TextView) findViewById(R.id.txtSmsGio);
        txtSmsNgay        = (TextView) findViewById(R.id.txtSmsNgay);
        imgbtnEdit        = (ImageButton) findViewById(R.id.imgbtnEdit);
        imgbtnDelete      = (ImageButton) findViewById(R.id.imgbtnDelete);
        imgbtnBack        = (ImageButton) findViewById(R.id.imgbtnBack);
        imgbtnReply       = (ImageButton) findViewById(R.id.imgbtnReply);
        linearLayout      = (LinearLayout) findViewById(R.id.layoutSmsStatus);
    }

}
