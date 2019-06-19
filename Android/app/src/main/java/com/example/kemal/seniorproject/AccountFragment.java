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

import com.example.kemal.seniorproject.Company.CompanyAddFragment;
import com.example.kemal.seniorproject.Company.CompanyFragment;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.Settings.TabPageAdapter;
import com.example.kemal.seniorproject.User.ProfileFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private String myId;



    public AccountFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);


        SessionManager.init(getActivity());

        myId = SessionManager.myId;

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.account_viewpager);

        setTabMenu();

        return view;
    }

    private void setTabMenu() {
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(viewPager);


        String name = SessionManager.name;
        String surname = SessionManager.surname;
        String companyName = SessionManager.companyname;


        setViewPager(name + " " + surname, companyName);


    }

    @Override
    public void onResume() {
        super.onResume();
        String name = SessionManager.name;
        String surname = SessionManager.surname;

        tabLayout.getTabAt(0).setText(name + " " + surname);


    }

    private void setViewPager(String namesurname, String companyName) {
        TabPageAdapter adapter = new TabPageAdapter(getChildFragmentManager());

        adapter.addFragment(new ProfileFragment(), namesurname);

        if (companyName.equals("") || companyName.equals("null")) {
            adapter.addFragment(new CompanyAddFragment(), "Bir şirket ekleyin");
        } else {
            adapter.addFragment(new CompanyFragment(), companyName);
        }
        viewPager.setAdapter(adapter);


    }


}


