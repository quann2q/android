package androidmads.n2q.ContactManagement;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class ContactClass implements Serializable, Comparable<ContactClass> {
    private String name;
    private String phone;
    private boolean isCheck;

    public ContactClass() {
    }

    public ContactClass(String name, String phone){
        this.name = name;
        this.phone = phone;
    }

    public ContactClass(String name, String phone, boolean isCheck) {
        this.name = name;
        this.phone = phone;
        this.isCheck = isCheck;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    @Override
    public String toString() {
        return this.name +"\n"+ this.phone;
    }

    @Override
    public int compareTo(@NonNull ContactClass contactClass) {
        return this.getName().compareTo(contactClass.getName());
    }
}
