package androidmads.n2q.ContactManagement;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidmads.n2q.Database.Database;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;
import androidmads.n2q.SmsManagement.SmsFormatPhone;

public class ContactGroupDetails extends AppCompatActivity{
    TextView txtContactDetailNameGroup;
    ListView listViewContactDetailGroup;
    Button btnContactDetalsCancel, btnContactDetailsOk;
    ImageButton imgbtnContactGroupEdit;
    Database database = new Database(this, "EmailManagement.sqlite", null, Utils.VERSION_SQL);
    ArrayList<ContactClass> contactArrayList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_group_details);
        init();

        Intent intent = getIntent();
        final String name = intent.getStringExtra("nameGroup");
        txtContactDetailNameGroup.setText(name);
        listViewContactDetailGroup.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        loadContactDetail(name);
        listViewContactDetailGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                ContactClass contactClass =
                        (ContactClass) listViewContactDetailGroup.getItemAtPosition(position);
                contactClass.setCheck(!currentCheck);
            }
        });

        listViewContactDetailGroup.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = contactArrayList.get(position).getPhone();
                String nameMember = contactArrayList.get(position).getName();
                sqlDelete("DELETE FROM ContactGroup WHERE SoDienThoai = '"+ phone +"'");
                Toast.makeText(ContactGroupDetails.this, "Đã xóa " + nameMember, Toast.LENGTH_SHORT).show();
                loadContactDetail(name);
                Intent intent1 = new Intent();
                setResult(Utils.RESULT_INTENT2, intent1);
                return true;
            }
        });

        btnContactDetailsOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray sparseBooleanArray = listViewContactDetailGroup.getCheckedItemPositions();
                StringBuilder phoneNumber = new StringBuilder();
                for (int i = 0; i < sparseBooleanArray.size(); i++) {
                    if (sparseBooleanArray.valueAt(i)) {
                        ContactClass contact = (ContactClass) listViewContactDetailGroup.getItemAtPosition(i);
                        String phone = contact.getPhone();
                        phoneNumber = phoneNumber.append(phone).append(", ");
                    }
                }
                String tempPhone = phoneNumber.toString();
                if (tempPhone.isEmpty()){
                    Toast.makeText(ContactGroupDetails.this,
                            "Chưa chọn thành viên nào", Toast.LENGTH_SHORT).show();
                } else {
                    String phone = SmsFormatPhone.formatPhone(tempPhone);
                    Intent intent1 = new Intent();
                    intent1.putExtra("DetailsPhone", phone);
                    setResult(Utils.RESULT_INTENT2, intent1);
                    finish();
                }
            }
        });

        btnContactDetalsCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imgbtnContactGroupEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameGroup = txtContactDetailNameGroup.getText().toString();
                ArrayList<ContactClass> arrayList  = new ArrayList<>();
                String commandSQL = "SELECT * FROM ContactGroup WHERE TenNhom = '"+ nameGroup +"'";
                Cursor dataContact = database.getDataSQL(commandSQL);
                while (dataContact.moveToNext()){
                    String nameMember = dataContact.getString(2);
                    String phoneMember = dataContact.getString(3);
                    arrayList.add(new ContactClass(nameMember, phoneMember, false));
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("arrayList", arrayList);
                Intent intent1 = new Intent(ContactGroupDetails.this, ContactNewGroup.class);
                intent1.putExtra("nameIntent", Utils.NAME_CONTACT_EDIT);
                intent1.putExtra("nameGroup", nameGroup);
                intent1.putExtra("bundleArray", bundle);
                startActivityForResult(intent1, Utils.RESULT_INTENT1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Utils.RESULT_INTENT1){
            Intent intent = new Intent();
            setResult(Utils.RESULT_INTENT1, intent);
            finish();
        }
    }

    private void init(){
        txtContactDetailNameGroup  = (TextView) findViewById(R.id.txtContactDetailNameGroup);
        listViewContactDetailGroup = (ListView) findViewById(R.id.listViewContactDetailGroup);
        btnContactDetalsCancel     = (Button) findViewById(R.id.btnContactDetalsCancel);
        btnContactDetailsOk        = (Button) findViewById(R.id.btnContactDetailsOk);
        imgbtnContactGroupEdit     = (ImageButton) findViewById(R.id.imgbtnContactGroupEdit);
    }

    private void loadContactDetail(String nameGroup){
        String commandSQL = "SELECT * FROM ContactGroup WHERE TenNhom = '"+ nameGroup +"'";
        Cursor dataContact = database.getDataSQL(commandSQL);
        contactArrayList.clear();
        while (dataContact.moveToNext()){
            String nameMember = dataContact.getString(2);
            String phoneMember = dataContact.getString(3);
            contactArrayList.add(new ContactClass(nameMember, phoneMember, false));
        }
        ArrayAdapter<ContactClass> arrayAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_checked, contactArrayList);
        listViewContactDetailGroup.setAdapter(arrayAdapter);
        for(int i=0; i<contactArrayList.size(); i++)  {
            listViewContactDetailGroup.setItemChecked(i, contactArrayList.get(i).isCheck());
        }
    }

    private void sqlDelete(String sql){
        database.queryDataSQL(sql);
    }

}
