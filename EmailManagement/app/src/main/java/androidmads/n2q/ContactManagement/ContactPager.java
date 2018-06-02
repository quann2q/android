package androidmads.n2q.ContactManagement;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import androidmads.n2q.Adapter.ViewPagerAdapterContact;
import androidmads.n2q.R;

public class ContactPager extends AppCompatActivity {
    TabLayout tabLayoutContact;
    ViewPager viewPagerContact;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_layout_pager);
        init();
        ViewPagerAdapterContact viewPagerAdapterContact =
                new ViewPagerAdapterContact(getSupportFragmentManager());
        viewPagerContact.setAdapter(viewPagerAdapterContact);
        viewPagerAdapterContact.notifyDataSetChanged();
        tabLayoutContact.setupWithViewPager(viewPagerContact);
    }

    private void init(){
        tabLayoutContact = (TabLayout) findViewById(R.id.tabLayoutContact);
        viewPagerContact = (ViewPager) findViewById(R.id.viewPagerContact);
    }
}
