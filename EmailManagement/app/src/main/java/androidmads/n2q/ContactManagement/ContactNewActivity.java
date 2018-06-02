package androidmads.n2q.ContactManagement;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import androidmads.n2q.Database.FileManager;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;
import androidmads.n2q.SmsManagement.SmsFormatPhone;

public class ContactNewActivity extends AppCompatActivity{
    ListView listViewContact;
    ArrayList<ContactClass> contactArrayList = new ArrayList<>();
    EditText edtContactSearch;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout_bar);

        listViewContact = (ListView) findViewById(R.id.listViewContact);
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
        findViewById(R.id.btnContactCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnContactOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<ContactClass> arrayListContact = new ArrayList<>();
                SparseBooleanArray sparseBooleanArray = listViewContact.getCheckedItemPositions();
                for (int i = 0; i < sparseBooleanArray.size(); i++) {
                    if (sparseBooleanArray.valueAt(i)) {
                        ContactClass contact = (ContactClass) listViewContact.getItemAtPosition(i);
                        String phone = contact.getPhone();
                        phone = SmsFormatPhone.formatPhone(phone);
                        String name = contact.getName();
                        ContactClass contactClass = new ContactClass(name, phone);
                        arrayListContact.add(contactClass);
                    }
                }
                Bundle extra = new Bundle();
                extra.putSerializable("contact", arrayListContact);
                Intent intent = new Intent();
                intent.putExtra("extra", extra);
                setResult(Utils.RESULT_INTENT2, intent);
                finish();
            }
        });

        edtContactSearch = (EditText) findViewById(R.id.edtContactSearch);
        edtContactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactArrayList.clear();
                Cursor phones = getContentResolver()
                        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                while (phones.moveToNext()) {
                    String name = phones.getString(phones.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.NUMBER));
                    String key = edtContactSearch.getText().toString();
                    if (Utils.isContain(name, key)){
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

    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(this, permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }

    private void loadContact(){
        if(checkPermission(Manifest.permission.READ_CONTACTS)){

            Cursor phones = getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract
                        .CommonDataKinds.Phone.NUMBER));
                contactArrayList.add(new ContactClass(name, phoneNumber, false));
            }
            phones.close();

            // Sắp xếp
            Collections.sort(contactArrayList);

            setListViewContact(contactArrayList, listViewContact);
        }
    }

    public void setListViewContact(ArrayList<ContactClass> contactArrayList, ListView listViewContact){
        ArrayAdapter<ContactClass> arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, contactArrayList);

        listViewContact.setAdapter(arrayAdapter);
        for(int i=0;i< contactArrayList.size(); i++ )  {
            listViewContact.setItemChecked(i, contactArrayList.get(i).isCheck());
        }
    }
}
