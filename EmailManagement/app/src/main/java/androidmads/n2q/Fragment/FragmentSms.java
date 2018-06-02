package androidmads.n2q.Fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidmads.n2q.AlarmManagement.AlarmSms;
import androidmads.n2q.ContactManagement.ContactClass;
import androidmads.n2q.Database.Database;
import androidmads.n2q.Database.FileManager;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;
import androidmads.n2q.SmsManagement.SmsAdapter;
import androidmads.n2q.SmsManagement.SmsClass;
import androidmads.n2q.SmsManagement.SmsNewActivity;
import androidmads.n2q.SmsManagement.SmsShowDetails;

import static android.content.Context.ALARM_SERVICE;

public class FragmentSms extends android.support.v4.app.Fragment {

    // SMS
    static ArrayList<SmsClass> smsArrayList;
    static SmsAdapter smsAdapter;
    private Context context;
    ListView listViewSms;
    FloatingActionButton fabSms;
    static Database database;
    Calendar calendar = Calendar.getInstance();

    private int smsId;
    private String smsPhoneNumber;
    private String smsContent;
    private String smsTime;
    private String smsDate;
    private int smsStatus;

    FileManager fileManager;

    java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

    public FragmentSms(){
    }

    public FragmentSms(Context context){
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sms_app_bar, container, false);
        context = getContext();
        fileManager = new FileManager(context);
        if (!fileManager.FileExists(Utils.FILE_NAME_CONTACT)){
            fileManager.saveData(Utils.FILE_NAME_CONTACT, "");
        }

//        Toast.makeText(getContext(), fileManager.FileExists(getContext(), Utils.FILE_NAME_CONTACT) + "", Toast.LENGTH_SHORT).show();

        // SMS
        listViewSms  = (ListView) view.findViewById(R.id.listViewSMS);
        smsArrayList = new ArrayList<>();
        smsAdapter   = new SmsAdapter(getContext(), R.layout.sms_layout_row, smsArrayList);
        listViewSms.setAdapter(smsAdapter);

        // Tạo database
        database = new Database(getContext(), "EmailManagement.sqlite", null, Utils.VERSION_SQL);

        // Tạo bảng
        database.queryDataSQL("CREATE TABLE IF NOT EXISTS Sms (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "SoDienThoai VARCHAR(100), " +
                "NoiDung VARCHAR(250),  " +
                "Gio VARCHAR(20), " +
                "Ngay VARCHAR(20), " +
                "TrangThai INTEGER)");

        loadDatabaseSms("SELECT * FROM Sms");

        fabSms = (FloatingActionButton) view.findViewById(R.id.fabSMS);
        fabSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SmsNewActivity.class);
                startActivity(intent);
            }
        });

        listViewSms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), SmsShowDetails.class);
                intent.putExtra("Ten", "");
                intent.putExtra("ID", smsArrayList.get(position).getId());
                intent.putExtra("SmsTen", smsArrayList.get(position).getSmsTen());
                intent.putExtra("SoDienThoai", smsArrayList.get(position).getSmsSoDienThoai());
                intent.putExtra("NoiDung", smsArrayList.get(position).getSmsNoiDung());
                intent.putExtra("Gio", smsArrayList.get(position).getSmsGio());
                intent.putExtra("Ngay", smsArrayList.get(position).getSmsNgay());
                intent.putExtra("TrangThai", smsArrayList.get(position).getSmsTrangThai());
                startActivity(intent);
            }
        });

        listViewSms.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int idSms = smsArrayList.get(position).getId();
                commandDataSms("DELETE FROM Sms WHERE Id = " + idSms);
                loadDatabaseSms("SELECT * FROM Sms");

                try {
                    setCalendarSms(getContext());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        try {
            setCalendarSms(getContext());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    // Load lại dữ liệu vào listViewSms
    public void loadDatabaseSms(String commandSQL) {
        ArrayList<ContactClass> arrayListContact;
        arrayListContact = readDataContact(Utils.FILE_NAME_CONTACT);
        if (database != null){
            Cursor dataSms = database.getDataSQL(commandSQL);
            smsArrayList.clear();
            while (dataSms.moveToNext()) {
                int id = dataSms.getInt(0);
                String soDienThoai = dataSms.getString(1);
                String noiDung = dataSms.getString(2);
                String gio = dataSms.getString(3);
                String ngay = dataSms.getString(4);
                int trangThai = dataSms.getInt(5);
                for (int i = 0; i< arrayListContact.size(); i++){
                    if (arrayListContact.get(i).getPhone().equals(soDienThoai)){
                        smsArrayList.add(new SmsClass(id, arrayListContact.get(i).getName(),
                                soDienThoai, noiDung, gio, ngay, trangThai));
                        break;
                    } else {
                        if (i == arrayListContact.size()-1){
                            smsArrayList.add(new SmsClass(id, soDienThoai, noiDung, gio, ngay, trangThai));
                            break;
                        }
                    }
                }
            }
            smsAdapter.notifyDataSetChanged();
        }
    }

    public void loadDatabaseSms(Context context, String commandSQL) {
        ArrayList<ContactClass> arrayListContact;
        arrayListContact = readDataContact(context, Utils.FILE_NAME_CONTACT);
        if (database != null){
            Cursor dataSms = database.getDataSQL(commandSQL);
            smsArrayList.clear();
            while (dataSms.moveToNext()) {
                int id = dataSms.getInt(0);
                String soDienThoai = dataSms.getString(1);
                String noiDung = dataSms.getString(2);
                String gio = dataSms.getString(3);
                String ngay = dataSms.getString(4);
                int trangThai = dataSms.getInt(5);
                for (int i = 0; i< arrayListContact.size(); i++){
                    if (arrayListContact.get(i).getPhone().equals(soDienThoai)){
                        smsArrayList.add(new SmsClass(id, arrayListContact.get(i).getName(),
                                soDienThoai, noiDung, gio, ngay, trangThai));
                        break;
                    } else {
                        if (i == arrayListContact.size()-1){
                            smsArrayList.add(new SmsClass(id, soDienThoai, noiDung, gio, ngay, trangThai));
                            break;
                        }
                    }
                }
            }
            smsAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<ContactClass> readDataContact(Context context, String fileName){
        ArrayList<ContactClass> arrayListContact = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF8"));
            String line;
            while((line = br.readLine()) != null){
                arrayListContact.add(new ContactClass(line, br.readLine()));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        arrayListContact.add(new ContactClass("", ""));
        return arrayListContact;
    }

    public ArrayList<ContactClass> readDataContact(String fileName){
        ArrayList<ContactClass> arrayListContact = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF8"));
            String line;
            while((line = br.readLine()) != null){
                arrayListContact.add(new ContactClass(line, br.readLine()));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        arrayListContact.add(new ContactClass("", ""));
        return arrayListContact;
    }

    // Nhận lệnh truy vấn không trả về
    public static void commandDataSms(String commandSQL) {
        database.queryDataSQL(commandSQL);
    }

    public void setCalendarSms(Context context) throws ParseException {
        loadDatabaseSms(context, "SELECT * FROM Sms");
        int count = 0;
        int index = 0;
        int dayOfMonth;
        int month;
        int year;
        int hour;
        int minutes;
        if (smsArrayList != null){
            for (int i = 0; i < smsArrayList.size(); i++) {
                if (smsArrayList.get(i).getSmsTrangThai() == 0) {
                    count++;
                    index = i;
                }
            }
        }
        if (count == 1) {
            smsId = smsArrayList.get(index).getId();
            smsPhoneNumber = smsArrayList.get(index).getSmsSoDienThoai();
            smsContent = smsArrayList.get(index).getSmsNoiDung();
            smsTime = smsArrayList.get(index).getSmsGio();
            smsDate = smsArrayList.get(index).getSmsNgay();
            smsStatus = smsArrayList.get(index).getSmsTrangThai();

            String[] itemsDate = smsDate.split("/");
            dayOfMonth = Integer.parseInt(itemsDate[0].trim());
            month = Integer.parseInt(itemsDate[1].trim());
            year = Integer.parseInt(itemsDate[2].trim());
            String[] itemsTime = smsTime.split(":");
            hour = Integer.parseInt(itemsTime[0].trim());
            minutes = Integer.parseInt(itemsTime[1].trim());

            calendar.set(year, month - 1, dayOfMonth, hour, minutes, 0);

            setAlarmSms(context, calendar.getTimeInMillis(),
                    smsId, smsPhoneNumber, smsContent, smsTime, smsDate, smsStatus, true);

        } else if (count > 1) {
            String dateTimeMin = "31/12/2118 23:59";
            for (int i = 0; i < smsArrayList.size(); i++) {
                if (smsArrayList.get(i).getSmsTrangThai() == 0) {
                    String dateTime = smsArrayList.get(i).getSmsNgay() + " "
                                    + smsArrayList.get(i).getSmsGio();
                    Date date1 = simpleDateFormat.parse(dateTimeMin);
                    Date date2 = simpleDateFormat.parse(dateTime);
                    if (date2.before(date1)) {
                        dateTimeMin = dateTime;
                        index = i;
                    }
                }
            }
            Log.d("Vị trí Sms min = " + index, "SMSSS");
            smsId          = smsArrayList.get(index).getId();
            smsPhoneNumber = smsArrayList.get(index).getSmsSoDienThoai();
            smsContent     = smsArrayList.get(index).getSmsNoiDung();
            smsTime        = smsArrayList.get(index).getSmsGio();
            smsDate        = smsArrayList.get(index).getSmsNgay();
            smsStatus      = smsArrayList.get(index).getSmsTrangThai();

            String[] itemsDate = smsDate.split("/");
            dayOfMonth = Integer.parseInt(itemsDate[0].trim());
            month      = Integer.parseInt(itemsDate[1].trim());
            year       = Integer.parseInt(itemsDate[2].trim());
            String[] itemsTime = smsTime.split(":");
            hour       = Integer.parseInt(itemsTime[0].trim());
            minutes    = Integer.parseInt(itemsTime[1].trim());

            calendar.set(year, month - 1, dayOfMonth, hour, minutes, 0);
            setAlarmSms(context, calendar.getTimeInMillis(),
                    smsId, smsPhoneNumber, smsContent, smsTime, smsDate, smsStatus, true);
        } else {
            setAlarmSms(context, calendar.getTimeInMillis(),
                    smsId, smsPhoneNumber, smsContent, smsTime, smsDate, smsStatus, false);
        }
    }

    public void setAlarmSms(Context context, long timeInMillis, int id, String smsPhoneNumber, String smsContent,
                            String smsTime, String smsDate, int smsStatus, boolean setAlarm) {
        if (context != null){
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmSms.class);
            intent.putExtra("smsId", id);
            intent.putExtra("smsPhoneNumber", smsPhoneNumber);
            intent.putExtra("smsContent", smsContent);
            intent.putExtra("smsTime", smsTime);
            intent.putExtra("smsDate", smsDate);
            intent.putExtra("smsStatus", smsStatus);

            PendingIntent pendingIntent = PendingIntent
                    .getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (setAlarm){
                alarmManager.setRepeating(AlarmManager.RTC, timeInMillis,
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                alarmManager.cancel(pendingIntent);
            }
        } else {
            Log.d("Context null", "SMSSS");
        }
    }

}
