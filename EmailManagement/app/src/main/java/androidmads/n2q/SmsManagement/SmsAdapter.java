package androidmads.n2q.SmsManagement;

import android.content.Context;
import android.text.Layout;
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

public class SmsAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<SmsClass> smsList;

    public SmsAdapter(Context context, int layout, List<SmsClass> smsList) {
        this.context = context;
        this.layout  = layout;
        this.smsList = smsList;
    }

    @Override
    public int getCount() {
        return smsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout, null);

        // Ánh xạ view
        TextView txtSmsSoDienThoai = (TextView) convertView.findViewById(R.id.txtSMSSoDienThoai);
        TextView txtSmsNoiDung     = (TextView) convertView.findViewById(R.id.txtSMSNoiDung);
        TextView txtSmsGio         = (TextView) convertView.findViewById(R.id.txtSmsGio);
        TextView txtSmsNgay        = (TextView) convertView.findViewById(R.id.txtSmsNgay);
        ImageView imgViewSmsStatus = (ImageView) convertView.findViewById(R.id.imgViewSmsStatus);
        LinearLayout layout        = (LinearLayout) convertView.findViewById(R.id.layoutSMSStatus);

        // Gán giá trị
        SmsClass sms = smsList.get(position);
        if (sms.getSmsTen() != null){
            txtSmsSoDienThoai.setText(sms.getSmsTen());
        } else {
            txtSmsSoDienThoai.setText(sms.getSmsSoDienThoai());
        }
        txtSmsNoiDung    .setText(sms.getSmsNoiDung());
        txtSmsGio        .setText(sms.getSmsGio());
        txtSmsNgay       .setText(sms.getSmsNgay());

        int status = sms.getSmsTrangThai();
        if(status == Utils.STATUS_ALARM){
            imgViewSmsStatus.setImageResource(R.drawable.ic_clock_white_100);
            layout.setBackgroundResource(R.drawable.body_email_status_alarm);
        } else if(status == Utils.STATUS_SEND){
            imgViewSmsStatus.setImageResource(R.drawable.ic_checkmark_white_100);
            layout.setBackgroundResource(R.drawable.body_email_status_send);
        } else if (status == Utils.SMS_STATUS_RECEIVER){
            imgViewSmsStatus.setImageResource(R.drawable.ic_down_arrow_white_100);
            layout.setBackgroundResource(R.drawable.body_email_status_receiver);
        }

        return convertView;
    }
}
