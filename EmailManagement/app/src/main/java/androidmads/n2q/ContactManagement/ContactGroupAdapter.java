package androidmads.n2q.ContactManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import androidmads.n2q.R;

public class ContactGroupAdapter extends BaseAdapter{
    private Context context;
    private int layout;
    private List<ContactGroupClass> contactGroupClassList;

    public ContactGroupAdapter(Context context, int layout,
                               List<ContactGroupClass> contactGroupClassList) {
        this.context = context;
        this.layout = layout;
        this.contactGroupClassList = contactGroupClassList;
    }

    @Override
    public int getCount() {
        return contactGroupClassList.size();
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

        TextView txtContactName = (TextView) convertView.findViewById(R.id.txtContactName);
        TextView txtContactPhone = (TextView) convertView.findViewById(R.id.txtContactPhone);

        ContactGroupClass contactGroupClass = contactGroupClassList.get(position);
        txtContactName.setText(contactGroupClass.getNameGroup());
        txtContactPhone.setText(contactGroupClass.getCountMember() + " thành viên");

        return convertView;
    }
}
