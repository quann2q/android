package androidmads.n2q.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import androidmads.n2q.Fragment.FragmentContact;
import androidmads.n2q.Fragment.FragmentContactGroup;

public class ViewPagerAdapterContact extends FragmentPagerAdapter {
    public ViewPagerAdapterContact(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return new FragmentContact();
            case 1 :
                return new FragmentContactGroup();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return "Danh bạ";
            case 1 :
                return "Nhóm";
            default:
                return null;
        }
    }
}
