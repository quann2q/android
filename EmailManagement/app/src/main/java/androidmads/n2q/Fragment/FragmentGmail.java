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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidmads.n2q.AlarmManagement.AlarmGmail;
import androidmads.n2q.Database.Database;
import androidmads.n2q.EmailManagement.EmailAdapter;
import androidmads.n2q.EmailManagement.EmailClass;
import androidmads.n2q.EmailManagement.EmailNewActivity;
import androidmads.n2q.EmailManagement.EmailShowDetails;
import androidmads.n2q.Helper.Utils;
import androidmads.n2q.R;

import static android.content.Context.ALARM_SERVICE;

public class FragmentGmail extends android.support.v4.app.Fragment {

    // Gmail
    static ArrayList<EmailClass> emailArrayList;
    static EmailAdapter emailAdapter;
    ListView listViewEmail;
    FloatingActionButton fabEmail;
    static Database database;
    Calendar calendar = Calendar.getInstance();

    int idGmail         = 0;
    String addressGmail = null;
    String titleGmail   = null;
    String contentGmail = null;
    String timeGmail    = null;
    String dateGmail    = null;
    int statusGmail     = 0;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_app_bar, container, false);

        // Gmail
        listViewEmail  = (ListView) view.findViewById(R.id.listViewEmail);
        emailArrayList = new ArrayList<>();
        emailAdapter   = new EmailAdapter(getContext(), R.layout.email_layout_row, emailArrayList);
        listViewEmail.setAdapter(emailAdapter);

        // Tạo database
        database = new Database(getContext(), "EmailManagement.sqlite", null, Utils.VERSION_SQL);

        // Tạo bảng
        database.queryDataSQL("CREATE TABLE IF NOT EXISTS Email (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DiaChi VARCHAR(100), " +
                "TieuDe VARCHAR(200), " +
                "NoiDung VARCHAR(1000), " +
                "Gio VARCHAR(20), " +
                "Ngay VARCHAR(20), " +
                "TrangThai INTEGER)");

        loadDatabaseEmail("SELECT * FROM Email");

        fabEmail = (FloatingActionButton) view.findViewById(R.id.fabEmail);
        fabEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EmailNewActivity.class);
                startActivity(intent);
            }
        });

        // Click vào item trên list Gmail
        listViewEmail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), EmailShowDetails.class);
                intent.putExtra("Ten", "");
                intent.putExtra("ID", emailArrayList.get(position).getId());
                intent.putExtra("DiaChi", emailArrayList.get(position).getEmailDiaChi());
                intent.putExtra("TieuDe", emailArrayList.get(position).getEmailTieuDe());
                intent.putExtra("NoiDung", emailArrayList.get(position).getEmailNoiDung());
                intent.putExtra("Gio", emailArrayList.get(position).getEmailGio());
                intent.putExtra("Ngay", emailArrayList.get(position).getEmailNgay());
                intent.putExtra("TrangThai", emailArrayList.get(position).getEmailTrangThai());
                startActivity(intent);
            }
        });

        listViewEmail.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int idEmail = emailArrayList.get(position).getId();
                commandDataEmail("DELETE FROM Email WHERE Id = " + idEmail);
                loadDatabaseEmail("SELECT * FROM Email");

                try {
                    setCalendarGmail(getContext());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        try {
            setCalendarGmail(getContext());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    // Load lại dữ liệu vào listViewEmail
    public static void loadDatabaseEmail(String commandSQL) {
        if (database != null){
            Cursor dataEmail = database.getDataSQL(commandSQL);
            emailArrayList.clear();
            while (dataEmail.moveToNext()) {
                int id         = dataEmail.getInt(0);
                String diaChi  = dataEmail.getString(1);
                String tieuDe  = dataEmail.getString(2);
                String noiDung = dataEmail.getString(3);
                String gio     = dataEmail.getString(4);
                String ngay    = dataEmail.getString(5);
                int trangThai  = dataEmail.getInt(6);
                emailArrayList.add(new EmailClass(id, diaChi, tieuDe, noiDung, gio, ngay, trangThai));
            }
            emailAdapter.notifyDataSetChanged();
        }
    }

    // Nhận lệnh truy vấn không trả về
    public static void commandDataEmail(String commandSQL) {
        database.queryDataSQL(commandSQL);
    }

    // Alarm
    public void setCalendarGmail(Context context) throws ParseException {
        loadDatabaseEmail("SELECT * FROM Email");
        int count = 0;
        int index = 0;
        int dayOfMonth;
        int month;
        int year;
        int hour;
        int minutes;

        if (emailArrayList != null){
            for (int i = 0; i < emailArrayList.size(); i++) {
                if (emailArrayList.get(i).getEmailTrangThai() == 0) {
                    count++;
                    index = i;
                }
            }
        }

        if (count == 1){
            Log.d("Vị trí Gmail min = " + index, "AAA");
            idGmail      = emailArrayList.get(index).getId();
            addressGmail = emailArrayList.get(index).getEmailDiaChi();
            titleGmail   = emailArrayList.get(index).getEmailTieuDe();
            contentGmail = emailArrayList.get(index).getEmailNoiDung();
            dateGmail    = emailArrayList.get(index).getEmailNgay();
            timeGmail    = emailArrayList.get(index).getEmailGio();
            statusGmail  = emailArrayList.get(index).getEmailTrangThai();

            String[] itemsDate = dateGmail.split("/");
            dayOfMonth = Integer.parseInt(itemsDate[0].trim());
            month      = Integer.parseInt(itemsDate[1].trim());
            year       = Integer.parseInt(itemsDate[2].trim());
            String[] itemsTime = timeGmail.split(":");
            hour       = Integer.parseInt(itemsTime[0].trim());
            minutes    = Integer.parseInt(itemsTime[1].trim());

            calendar.set(year, month-1, dayOfMonth, hour, minutes, 0);
            setAlarmGmail(context, calendar.getTimeInMillis(), idGmail, addressGmail, titleGmail, contentGmail,
                    dateGmail, timeGmail, statusGmail,true);
        } else if (count > 1){
            String dateTimeMin = "31/12/2118 23:59";
            for (int i = 0; i < emailArrayList.size(); i++) {
                if (emailArrayList.get(i).getEmailTrangThai() == 0) {
                    String dateTime = emailArrayList.get(i).getEmailNgay() + " "
                                    + emailArrayList.get(i).getEmailGio();
                    Date date1 = simpleDateFormat.parse(dateTimeMin);
                    Date date2 = simpleDateFormat.parse(dateTime);
                    if (date2.before(date1)){
                        dateTimeMin = dateTime;
                        index = i;
                    }
                }
            }

            Log.d("Vị trí Gmail min = " + index, "AAA");
            idGmail      = emailArrayList.get(index).getId();
            addressGmail = emailArrayList.get(index).getEmailDiaChi();
            titleGmail   = emailArrayList.get(index).getEmailTieuDe();
            contentGmail = emailArrayList.get(index).getEmailNoiDung();
            dateGmail    = emailArrayList.get(index).getEmailNgay();
            timeGmail    = emailArrayList.get(index).getEmailGio();
            statusGmail  = emailArrayList.get(index).getEmailTrangThai();

            String[] itemsDate = dateGmail.split("/");
            dayOfMonth = Integer.parseInt(itemsDate[0].trim());
            month      = Integer.parseInt(itemsDate[1].trim());
            year       = Integer.parseInt(itemsDate[2].trim());
            String[] itemsTime = timeGmail.split(":");
            hour       = Integer.parseInt(itemsTime[0].trim());
            minutes    = Integer.parseInt(itemsTime[1].trim());

            calendar.set(year, month-1, dayOfMonth, hour, minutes, 0);
            setAlarmGmail(context, calendar.getTimeInMillis(), idGmail, addressGmail, titleGmail, contentGmail,
                    dateGmail, timeGmail, statusGmail, true);
        } else{
            setAlarmGmail(context, calendar.getTimeInMillis(), idGmail, addressGmail, titleGmail, contentGmail,
                    dateGmail, timeGmail, statusGmail, false);
        }
    }

    private void setAlarmGmail(Context context, long timeInMillis, int id, String address, String title, String content,
                               String date, String time, int status, boolean setAlarm) {
        if (context != null){
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmGmail.class);
            intent.putExtra("id", id);
            intent.putExtra("address", address);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            intent.putExtra("status", status);

            PendingIntent pendingIntent = PendingIntent
                    .getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (setAlarm){
                alarmManager.setRepeating(AlarmManager.RTC, timeInMillis,
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                alarmManager.cancel(pendingIntent);
            }
        } else {
            Log.d("Context null", "AAA");
        }
    }

}
