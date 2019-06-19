package com.example.kemal.seniorproject;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kemal.seniorproject.Settings.NotificationService;
import com.example.kemal.seniorproject.Settings.SessionManager;
import com.example.kemal.seniorproject.Settings.SocketIO;
import com.github.nkzawa.socketio.client.Socket;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private SessionManager sessionManager;

    private HomeFragment homeFragment;
    private AccountFragment accountFragment;
    private NotificationsFragment notificationsFragment;
    private OptionFragment optionFragment;
    private MessagesFragment messagesFragment;


    int pageNumber;

    public static Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        if (!sessionManager.checkLogin()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            startService(new Intent(MainActivity.this, NotificationService.class));


            SessionManager.init(MainActivity.this);
            mSocket = SocketIO.getConnection(this, SessionManager.myId, "listen in the foreground...");


            BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.main_bottom_nav);
            bottomNavigationViewEx.enableAnimation(false);
            bottomNavigationViewEx.enableItemShiftingMode(false);
            bottomNavigationViewEx.enableShiftingMode(false);
            bottomNavigationViewEx.setTextVisibility(false);
            bottomNavigationViewEx.setFitsSystemWindows(true);
            bottomNavigationListener(bottomNavigationViewEx);

            accountFragment = new AccountFragment();
            optionFragment = new OptionFragment();
            messagesFragment = new MessagesFragment();
            homeFragment = new HomeFragment();
            notificationsFragment = new NotificationsFragment();

            setFragment(homeFragment);

            try {

                String getFragment = getIntent().getStringExtra("fragment_name");

                if (getFragment.equals("Message")) {
                    pageNumber = getIntent().getIntExtra("pageNumber", 0);
                    bottomNavigationViewEx.setCurrentItem(1);
                }

            } catch (Exception ex) {

            }


        }
    }

    private void bottomNavigationListener(BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        setFragment(homeFragment);
                        break;
                    case R.id.action_message:
                        Bundle bundle = new Bundle();
                        bundle.putInt("page", pageNumber);
                        messagesFragment.setArguments(bundle);
                        setFragment(messagesFragment);
                        break;
                    case R.id.action_notif:
                        setFragment(notificationsFragment);
                        break;
                    case R.id.action_account:
                        setFragment(accountFragment);
                        break;

                    case R.id.action_option:
                        setFragment(optionFragment);
                        break;

                }

                return true;
            }
        });
    }

    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_content, fragment);
        fragmentTransaction.commit();


    }


}
