package androidmads.n2q.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import androidmads.n2q.ContactManagement.ContactGroupAdapter;
import androidmads.n2q.ContactManagement.ContactGroupClass;
import androidmads.n2q.ContactManagement.ContactGroupDetails;
import androidmads.n2q.ContactManagement.ContactNewGroup;
import androidmads.n2q.Database.Database;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;

public class FragmentContactGroup extends Fragment {
    Database database;
    ArrayList<ContactGroupClass> contactArrayList = new ArrayList<>();
    ListView listViewContactGroup;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_layout_group, container, false);
        listViewContactGroup = (ListView) view.findViewById(R.id.listViewContactGroup);
        database = new Database(getContext(), "EmailManagement.sqlite", null, Utils.VERSION_SQL);
        loadContactGroup();

        listViewContactGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nameGroup = contactArrayList.get(position).getNameGroup();
                Intent intent = new Intent(getContext(), ContactGroupDetails.class);
                intent.putExtra("nameGroup", nameGroup);
                startActivityForResult(intent, Utils.RESULT_INTENT2);
            }
        });

        listViewContactGroup.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String name = contactArrayList.get(position).getNameGroup();
                sqlDelete("DELETE FROM ContactGroup WHERE TenNhom = '" + name + "'");
                loadContactGroup();
                Toast.makeText(getContext(), "Đã xóa nhóm " + name, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        view.findViewById(R.id.btnAddContactGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ContactNewGroup.class);
                startActivityForResult(intent, Utils.RESULT_INTENT1);
            }
        });

        view.findViewById(R.id.btnContactCancelGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case Utils.RESULT_INTENT1:
                loadContactGroup();
                break;
            case Utils.RESULT_INTENT2:{
                String phone = data.getStringExtra("DetailsPhone");
                Intent intent = new Intent();
                intent.putExtra("phoneNumber", phone);
                getActivity().setResult(Utils.RESULT_INTENT2, intent);
                getActivity().finish();
                break;
            }
        }
//        if (resultCode == Utils.RESULT_INTENT2){
//            String phone = data.getStringExtra("DetailsPhone");
//            Intent intent = new Intent();
//            intent.putExtra("phoneNumber", phone);
//            getActivity().setResult(Utils.RESULT_INTENT2, intent);
//            getActivity().finish();
//        }
//        if (resultCode == Utils.RESULT_INTENT1){
//            loadContactGroup();
//        }
    }

    public void loadContactGroup(){
        int count = 0;
        if (database != null){
            contactArrayList.clear();
            String commandSQL1 = "SELECT TenNhom FROM ContactGroup GROUP BY TenNhom";
            Cursor dataContact1 = database.getDataSQL(commandSQL1);
            while (dataContact1.moveToNext()) {
                String name = dataContact1.getString(0);
                String commandSQL2 = "SELECT * FROM ContactGroup WHERE TenNhom = '" + name + "'";
                Cursor dataContact2 = database.getDataSQL(commandSQL2);
                while (dataContact2.moveToNext()){
                    count++;
                }
                contactArrayList.add(new ContactGroupClass(name, count));
                count = 0;
            }

            ContactGroupAdapter arrayAdapter = new ContactGroupAdapter(getContext(), R.layout.contact_group_row, contactArrayList);
            listViewContactGroup.setAdapter(arrayAdapter);
        }
    }

    private void sqlDelete(String commandSQL){
        database.queryDataSQL(commandSQL);
    }

}
