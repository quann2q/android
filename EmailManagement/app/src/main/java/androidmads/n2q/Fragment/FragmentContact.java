package androidmads.n2q.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import androidmads.n2q.ContactManagement.ContactClass;
import androidmads.n2q.Database.Database;
import androidmads.n2q.Database.FileManager;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;
import androidmads.n2q.SmsManagement.SmsFormatPhone;

public class FragmentContact extends Fragment {
    Database database;
    ListView listViewContact;
    ArrayList<ContactClass> contactArrayList = new ArrayList<>();
    EditText edtContactSearch;
    FileManager fileManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_layout_bar, container, false);
        database = new Database(getContext(), "EmailManagement.sqlite", null, Utils.VERSION_SQL);
        fileManager = new FileManager(getContext());
        database.queryDataSQL("CREATE TABLE IF NOT EXISTS ContactGroup (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TenNhom VARCHAR(100), TenThanhVien VARCHAR(100), SoDienThoai VARCHAR(15))");

        // Contact
        listViewContact = (ListView) view.findViewById(R.id.listViewContact);
        listViewContact.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listViewContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                ContactClass contactClass = (ContactClass) listViewContact.getItemAtPosition(position);
                contactClass.setCheck(!currentCheck);
            }
        });

        loadContact();
        listViewContact.requestFocus();
        view.findViewById(R.id.btnContactCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                getActivity().setResult(Utils.RESULT_INTENT2, intent);
                intent.putExtra("phoneNumber", "");
                getActivity().finish();
            }
        });

        view.findViewById(R.id.btnContactOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray sparseBooleanArray = listViewContact.getCheckedItemPositions();
                StringBuilder phoneNumber = new StringBuilder();
                for (int i = 0; i < sparseBooleanArray.size(); i++) {
                    if (sparseBooleanArray.valueAt(i)) {
                        ContactClass contact = (ContactClass) listViewContact.getItemAtPosition(i);
                        String phone = contact.getPhone();
                        phoneNumber = phoneNumber.append(phone).append(", ");
                    }
                }
                String tempPhone = phoneNumber.toString();
                if (tempPhone.isEmpty()){
                    Toast.makeText(getContext(), "Chưa chọn số nào", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("phoneNumber", SmsFormatPhone.formatPhone(tempPhone));
                    getActivity().setResult(Utils.RESULT_INTENT2, intent);
                    getActivity().finish();
                }
            }
        });

        edtContactSearch = (EditText) view.findViewById(R.id.edtContactSearch);
        edtContactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactArrayList.clear();
                Cursor phones = getActivity().getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.NUMBER));
                    String key = edtContactSearch.getText().toString();
                    if (Utils.isContain(name, key)) {
                        contactArrayList.add(new ContactClass(name, phoneNumber, false));
                    }
                }
                phones.close();
                Collections.sort(contactArrayList);
                setListViewContact(contactArrayList, listViewContact);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(getContext(), permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }

    private void loadContact() {
        if (checkPermission(Manifest.permission.READ_CONTACTS)) {
            StringBuilder stringBuilder = new StringBuilder();
            Cursor phones = getActivity().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.NUMBER));
                stringBuilder.append(name).append("\n").append(SmsFormatPhone.formatPhone(phoneNumber)).append("\n");
                contactArrayList.add(new ContactClass(name, phoneNumber, false));
            }
            phones.close();

            fileManager.saveData(Utils.FILE_NAME_CONTACT, stringBuilder.toString());

            // Sắp xếp
            Collections.sort(contactArrayList);

            setListViewContact(contactArrayList, listViewContact);
        }
    }

    public void setListViewContact(ArrayList<ContactClass> contactArrayList, ListView listViewContact) {
        ArrayAdapter<ContactClass> arrayAdapter = new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_list_item_checked, contactArrayList);

        listViewContact.setAdapter(arrayAdapter);
        for (int i = 0; i < contactArrayList.size(); i++) {
            listViewContact.setItemChecked(i, contactArrayList.get(i).isCheck());
        }
    }

}
