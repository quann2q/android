package androidmads.n2q.EmailManagement;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;

public class EmailAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<EmailClass> emailList;

    public EmailAdapter(Context context, int layout, List<EmailClass> emailList) {
        this.context = context;
        this.layout = layout;
        this.emailList = emailList;
    }

    @Override
    public int getCount() {
        return emailList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout, null);

        // Ánh xạ view
        TextView txtEmailDiaChi      = (TextView) convertView.findViewById(R.id.txtEmailDiaChi);
        TextView txtEmailTieuDe      = (TextView) convertView.findViewById(R.id.txtEmailTieuDe);
        TextView txtEmailNoiDung     = (TextView) convertView.findViewById(R.id.txtEmailNoiDung);
        TextView txtEmailGio         = (TextView) convertView.findViewById(R.id.txtEmailGio);
        TextView txtEmailNgay        = (TextView) convertView.findViewById(R.id.txtEmailNgay);
        ImageView imgViewEmailStatus = (ImageView) convertView.findViewById(R.id.imgViewEmailStatus);
        LinearLayout layout          = (LinearLayout) convertView.findViewById(R.id.layoutEmailStatus);

        // Gán giá trị
        EmailClass email = emailList.get(position);
        txtEmailDiaChi .setText(email.getEmailDiaChi());
        txtEmailTieuDe .setText(email.getEmailTieuDe());
        txtEmailNoiDung.setText(email.getEmailNoiDung());
        txtEmailGio    .setText(email.getEmailGio());
        txtEmailNgay   .setText(email.getEmailNgay());

        int status = email.getEmailTrangThai();
        if(status == Utils.STATUS_ALARM){
            imgViewEmailStatus.setImageResource(R.drawable.ic_clock_white_100);
            layout.setBackgroundResource(R.drawable.body_email_status_alarm);
        } else if(status == Utils.STATUS_SEND){
            imgViewEmailStatus.setImageResource(R.drawable.ic_checkmark_white_100);
            layout.setBackgroundResource(R.drawable.body_email_status_send);
        }

        return convertView;
    }
}
