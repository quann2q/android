package androidmads.n2q.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import androidmads.n2q.Fragment.FragmentGmail;
import androidmads.n2q.Fragment.FragmentSms;

public class ViewPagerAdapterMain extends FragmentPagerAdapter{
    public ViewPagerAdapterMain(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return new FragmentGmail();
            case 1 :
                return new FragmentSms();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
//        return 3;
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return "Gmail";
            case 1 :
                return "SMS";
//            case 2 :
//                return "Chat";
            default:
                return null;
        }
    }
}
