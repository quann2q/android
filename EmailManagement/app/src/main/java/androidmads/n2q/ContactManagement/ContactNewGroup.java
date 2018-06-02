package androidmads.n2q.ContactManagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidmads.n2q.Database.Database;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;

public class ContactNewGroup extends AppCompatActivity {
    Database database = new Database(this, "EmailManagement.sqlite", null, Utils.VERSION_SQL);
    ListView listViewContact;
    ArrayList<ContactClass> arrayListContact = new ArrayList<>();
    ArrayList<ContactClass> arrayList;
    ContactAdapter adapter;
    EditText edtContactNameGroup;
    TextView txtChinhSua;

    int nameIntent;
    String nameGroupOld = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout_new_group);
        edtContactNameGroup = (EditText) findViewById(R.id.edtContactNameGroup);
        listViewContact     = (ListView) findViewById(R.id.listViewContact);
        txtChinhSua         = (TextView) findViewById(R.id.txtChinhSua);

        Bundle bundle;
        Intent intentEdit = getIntent();
        nameIntent = intentEdit.getIntExtra("nameIntent", Utils.REQUEST_INTENT);
        if (nameIntent == Utils.NAME_CONTACT_EDIT){
            txtChinhSua.setText(Utils.CONTACT_EDIT);
            nameGroupOld = intentEdit.getStringExtra("nameGroup");
            edtContactNameGroup.setText(nameGroupOld);
            edtContactNameGroup.setSelection(edtContactNameGroup.getText().length());
            bundle = intentEdit.getBundleExtra("bundleArray");
            arrayList = (ArrayList<ContactClass>) bundle.getSerializable("arrayList");
            if (adapter ==  null){
                adapter = new ContactAdapter(this, R.layout.contact_group_row, arrayList);
            } else {
                adapter.addItemContact(arrayList);
            }
            listViewContact.setAdapter(adapter);
        }

        listViewContact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.removeItemContact(position);
                return true;
            }
        });

        findViewById(R.id.imgbtnContactNewGroupBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btnNewContactCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn1ContactAddMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactNewGroup.this, ContactNewActivity.class);
                startActivityForResult(intent, Utils.RESULT_INTENT2);
            }
        });

        findViewById(R.id.btnContactAddMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactNewGroup.this, ContactNewActivity.class);
                startActivityForResult(intent, Utils.RESULT_INTENT2);
            }
        });

        findViewById(R.id.btnNewContactOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameGroupNew = edtContactNameGroup.getText().toString();
                if (nameIntent == Utils.NAME_CONTACT_EDIT){
                    if (Utils.isNotEmpty(edtContactNameGroup) && arrayList != null && arrayList.size() > 0){
                        sqlDelete(nameGroupOld);
                        for (int i=0; i<arrayList.size(); i++){
                            String member = arrayList.get(i).getName();
                            String phone  = arrayList.get(i).getPhone();
                            sqlInsert(nameGroupNew, member, phone);
                        }
                        Toast.makeText(ContactNewGroup.this, "Sửa thành công",
                                                                        Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ContactNewGroup.this, "Nhập đầy đủ thông tin",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (Utils.isNotEmpty(edtContactNameGroup) && arrayListContact != null
                                                                && arrayListContact.size() > 0){
                        for (int i=0; i<arrayListContact.size(); i++){
                            String member = arrayListContact.get(i).getName();
                            String phone  = arrayListContact.get(i).getPhone();
                            sqlInsert(nameGroupNew, member, phone);
                        }
                        Toast.makeText(ContactNewGroup.this, "Đã tạo nhóm " + nameGroupNew,
                                                                        Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ContactNewGroup.this, "Nhập đầy đủ thông tin",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent intent = new Intent();
                setResult(Utils.RESULT_INTENT1, intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<ContactClass> arrayListTemps;
        if (requestCode == Utils.RESULT_INTENT2){
            if (data != null){
                Bundle extra = data.getBundleExtra("extra");
                arrayListTemps = (ArrayList<ContactClass>) extra.getSerializable("contact");
                if (arrayListTemps != null){
                    for (int i=0; i<arrayListTemps.size(); i++){
                        arrayListContact.add(arrayListTemps.get(i));
                    }
                }
                if (adapter ==  null){
                    adapter = new ContactAdapter(this, R.layout.contact_group_row, arrayListTemps);
                } else {
                    adapter.addItemContact(arrayListTemps);
                }
                listViewContact.setAdapter(adapter);
            }
        }
    }

    private void sqlInsert(String nameGroup, String member, String phoneNumber){
        database.queryDataSQL("INSERT INTO ContactGroup VALUES(null, '"
                + nameGroup +"','"+ member +"','"+ phoneNumber +"')");
    }

    private void sqlDelete(String nameGroup){
        database.queryDataSQL("DELETE FROM ContactGroup WHERE TenNhom = '" + nameGroup + "'");
    }

}
