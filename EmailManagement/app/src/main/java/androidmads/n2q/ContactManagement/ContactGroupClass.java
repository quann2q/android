package androidmads.n2q.ContactManagement;

import android.support.annotation.NonNull;

public class ContactGroupClass implements Comparable<ContactGroupClass> {
    private String nameGroup;
    private int countMember;

    public ContactGroupClass(String nameGroup, int countMember) {
        this.nameGroup = nameGroup;
        this.countMember = countMember;
    }

    public String getNameGroup() {
        return nameGroup;
    }

    public int getCountMember() {
        return countMember;
    }

    @Override
    public int compareTo(@NonNull ContactGroupClass contactGroupClass) {
        return getNameGroup().compareTo(contactGroupClass.getNameGroup());
    }
}
