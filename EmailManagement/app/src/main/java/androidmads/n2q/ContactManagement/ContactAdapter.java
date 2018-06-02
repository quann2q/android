package androidmads.n2q.ContactManagement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidmads.n2q.R;

public class ContactAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<ContactClass> contactClassList;

    public ContactAdapter(Context context, int layout, List<ContactClass> contactClassList) {
        this.context = context;
        this.layout = layout;
        this.contactClassList = contactClassList;
    }

    public void addItemContact(ArrayList<ContactClass> contact) {
        for (int i = 0; i < contact.size(); i++) {
            contactClassList.add(contact.get(i));
        }
        notifyDataSetChanged();
    }

    public void removeItemContact(int index) {
        contactClassList.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contactClassList.size();
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

        ContactClass contactClass = contactClassList.get(position);
        txtContactName.setText(contactClass.getName());
        txtContactPhone.setText(contactClass.getPhone());

        return convertView;
    }
}

