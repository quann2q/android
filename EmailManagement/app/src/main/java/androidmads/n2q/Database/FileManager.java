package androidmads.n2q.Database;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import androidmads.n2q.ContactManagement.ContactClass;

public class FileManager {
    Context context;

    public FileManager() {
    }

    public FileManager(Context context) {
        this.context = context;
    }

    public boolean FileExists(String fname) {
        File file = context.getFileStreamPath(fname);
        return file.exists();
    }

    public void saveData(String FILE_NAME, String value) {
        try {
            FileOutputStream fileOutputStream
                    = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(value.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readData(String fileName){
        StringBuilder builder = new StringBuilder();
        try {
            FileInputStream in = context.openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while((line = br.readLine()) != null){
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public ArrayList<ContactClass> readDataContact(String fileName){
        ArrayList<ContactClass> arrayList = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line;
            while((line = br.readLine()) != null){
                arrayList.add(new ContactClass(line, br.readLine()));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }
}
