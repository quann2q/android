package androidmads.n2q.EmailManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import androidmads.n2q.Fragment.FragmentGmail;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;

public class EmailShowDetails extends AppCompatActivity {

    TextView txtEmailTieuDe, txtEmailDiaChi, txtEmailNoiDung;
    ImageView imgViewEmailStatus;
    TextView txtEmailGio, txtEmailNgay;
    ImageButton imgbtnEdit, imgbtnDelete, imgbtnBack;
    int id;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_show_details);
        init();

        final Intent intent = getIntent();
        id = intent.getIntExtra("ID", Utils.REQUEST_INTENT);
        txtEmailDiaChi.setText(intent.getStringExtra("DiaChi"));
        txtEmailTieuDe.setText(intent.getStringExtra("TieuDe"));
        txtEmailNoiDung.setText(intent.getStringExtra("NoiDung"));
        txtEmailGio.setText(intent.getStringExtra("Gio"));
        txtEmailNgay.setText(intent.getStringExtra("Ngay"));

        if (intent.getStringExtra("Ten").equals("AlarmGmail")){
            imgbtnDelete.setVisibility(View.GONE);
        } else {
            imgbtnDelete.setVisibility(View.VISIBLE);
        }

        if(intent.getIntExtra("TrangThai", Utils.REQUEST_INTENT) == Utils.STATUS_ALARM){
            imgViewEmailStatus.setImageResource(R.drawable.ic_clock_white_100);
            imgbtnEdit.setVisibility(View.VISIBLE);
            linearLayout.setBackgroundResource(R.drawable.body_email_status_alarm);
        } else if(intent.getIntExtra("TrangThai", Utils.REQUEST_INTENT) == Utils.STATUS_SEND){
            imgViewEmailStatus.setImageResource(R.drawable.ic_checkmark_white_100);
            imgbtnEdit.setVisibility(View.GONE);
            linearLayout.setBackgroundResource(R.drawable.body_email_status_send);
        }

        // Sửa
        imgbtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(EmailShowDetails.this, EmailNewActivity.class);
                intent1.putExtra("EmailEdit", Utils.NAME_EMAIL_SHOW_DETAILS);
                intent1.putExtra("ID", intent.getIntExtra("ID", Utils.REQUEST_INTENT));
                intent1.putExtra("DiaChi", intent.getStringExtra("DiaChi"));
                intent1.putExtra("TieuDe", intent.getStringExtra("TieuDe"));
                intent1.putExtra("NoiDung", intent.getStringExtra("NoiDung"));
                intent1.putExtra("Gio", intent.getStringExtra("Gio"));
                intent1.putExtra("Ngay", intent.getStringExtra("Ngay"));
                intent1.putExtra("TrangThai", intent.getIntExtra("TrangThai", Utils.REQUEST_INTENT));

                startActivity(intent1);
                finish();
            }
        });

        imgbtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentGmail.commandDataEmail("DELETE FROM Email WHERE Id = " + id);
                FragmentGmail.loadDatabaseEmail("SELECT * FROM Email");

                FragmentGmail fragmentGmail = new FragmentGmail();
                try {
                    fragmentGmail.setCalendarGmail(EmailShowDetails.this);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Toast.makeText(EmailShowDetails.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        imgbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void init(){
        txtEmailTieuDe     = (TextView) findViewById(R.id.txtEmailTieuDe);
        txtEmailDiaChi     = (TextView) findViewById(R.id.txtEmailDiaChi);
        txtEmailNoiDung    = (TextView) findViewById(R.id.txtEmailNoiDung);
        imgViewEmailStatus = (ImageView) findViewById(R.id.imgViewEmailStatus);
        txtEmailGio        = (TextView) findViewById(R.id.txtEmailGio);
        txtEmailNgay       = (TextView) findViewById(R.id.txtEmailNgay);
        imgbtnEdit         = (ImageButton) findViewById(R.id.imgbtnEdit);
        imgbtnDelete       = (ImageButton) findViewById(R.id.imgbtnDelete);
        imgbtnBack         = (ImageButton) findViewById(R.id.imgbtnBack);
        linearLayout       = (LinearLayout) findViewById(R.id.layoutEmailStatus);
    }

}
