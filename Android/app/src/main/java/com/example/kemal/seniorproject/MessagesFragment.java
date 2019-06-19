package com.example.kemal.seniorproject;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.kemal.seniorproject.Company.CompanyAddFragment;
import com.example.kemal.seniorproject.Company.CompanyMessagesFragment;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.User.UserMessageFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MessagesFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String myId;
    SessionManager sessionManager;

    public MessagesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        SessionManager.init(getActivity());

        myId = SessionManager.myId;

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.message_viewpager);

        setTabMenu();

        return view;
    }

    private void setTabMenu() {
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager);


        String name = SessionManager.name;
        String surname = SessionManager.surname;
        String companyId =SessionManager.companyId;
        String companyName =SessionManager.companyname;


        setViewPager(name + " " + surname, companyName);


    }

    private void setViewPager(String namesurname, String companyName) {
        TabPageAdapter adapter = new TabPageAdapter(getChildFragmentManager());

        adapter.addFragment(new UserMessageFragment(), namesurname);


        if (companyName.equals("") || companyName.equals("null")) {
            adapter.addFragment(new CompanyAddFragment(), "Bir şirket ekleyin");
        } else {
            adapter.addFragment(new CompanyMessagesFragment(), companyName);
        }
        viewPager.setAdapter(adapter);

        Bundle bundle = this.getArguments();
        try {
            int page = bundle.getInt("page");
            viewPager.setCurrentItem(page);
        } catch (Exception ex) {
        }


    }

    private class TabPageAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitle = new ArrayList<>();


        public TabPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }


        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitle.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }


    }


}
