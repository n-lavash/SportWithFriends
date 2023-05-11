package com.example.sportwithfriends.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sportwithfriends.R;
import com.example.sportwithfriends.constants.TypeFragment;
import com.example.sportwithfriends.screens.fragments.exercise.ExerciseFragment;
import com.example.sportwithfriends.screens.fragments.friends.FriendsFragment;
import com.example.sportwithfriends.screens.fragments.message.ChatsFragment;
import com.example.sportwithfriends.screens.fragments.news.NewsFragment;
import com.example.sportwithfriends.screens.fragments.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


// активити с нижним меню + всеми основными функциями приложения
// если пользователь не зарегистрирован, будет показываться другое активити

public class MainScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private BottomNavigationView bottomNavigationViewMainMenu;

    private Fragment fragmentOld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mAuth = FirebaseAuth.getInstance();
        bottomNavigationViewMainMenu = findViewById(R.id.bottomNavigationViewMainMenu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemNotifications:
                Intent intentNotification = new Intent(MainScreenActivity.this, NotificationsActivity.class);
                startActivity(intentNotification);
                return true;
            case R.id.itemSearch:
                Intent intentSearch = new Intent(MainScreenActivity.this, SearchActivity.class);
                startActivity(intentSearch);
                return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Fragment fragment = NewsFragment.newInstance();
        String typeFragment = TypeFragment.NEWS;

        Intent intentFragment = getIntent();
        if (intentFragment != null && intentFragment.hasExtra(TypeFragment.FRAGMENT_TYPE)) {
            typeFragment = intentFragment.getStringExtra(TypeFragment.FRAGMENT_TYPE);
            fragment = setFragment(fragment, typeFragment);
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            loadFragment(fragment, typeFragment);
            setSelectedMenuItem();
        } else {
            Intent intent = new Intent(MainScreenActivity.this, RegisteredActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private Fragment setFragment(Fragment fragment, String typeFragment) {
        switch (typeFragment) {
            case TypeFragment.EXERCISES:
                bottomNavigationViewMainMenu.getMenu().findItem(R.id.itemExercise).setChecked(true);
                return ExerciseFragment.newInstance();
            case TypeFragment.PROFILE:
                bottomNavigationViewMainMenu.getMenu().findItem(R.id.itemProfile).setChecked(true);
                return ProfileFragment.newInstance();
            case TypeFragment.FRIENDS:
                bottomNavigationViewMainMenu.getMenu().findItem(R.id.itemFriends).setChecked(true);
                return FriendsFragment.newInstance();
            case TypeFragment.CHATS:
                bottomNavigationViewMainMenu.getMenu().findItem(R.id.itemMessage).setChecked(true);
                return ChatsFragment.newInstance();
            default:
                Log.d("MyLog", "error load fragment\ntype fragment: " + typeFragment + "\nset fragment: " + fragment.getClass());
                return NewsFragment.newInstance();
        }
    }

    private void setSelectedMenuItem() {
        bottomNavigationViewMainMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemFriends:
                        loadFragment(FriendsFragment.newInstance(), TypeFragment.FRIENDS);
                        break;
                    case R.id.itemExercise:
                        loadFragment(ExerciseFragment.newInstance(), TypeFragment.EXERCISES);
                        break;
                    case R.id.itemNews:
                        loadFragment(NewsFragment.newInstance(), TypeFragment.NEWS);
                        break;
                    case R.id.itemMessage:
                        loadFragment(ChatsFragment.newInstance(), TypeFragment.CHATS);
                        break;
                    case R.id.itemProfile:
                        loadFragment(ProfileFragment.newInstance(), TypeFragment.PROFILE);
                        break;
                }
                return true;
            }
        });
    }

    private void loadFragment(Fragment fragment, String typeFragment) {
        fragmentOld = getFragmentOld();

        switch (typeFragment) {
            case TypeFragment.EXERCISES:
                setActionBar("Тренировки");
                break;
            case TypeFragment.FRIENDS:
                setActionBar("Мои друзья");
                break;
            case TypeFragment.NEWS:
                setActionBar("Новости");
                break;
            case TypeFragment.CHATS:
                setActionBar("Сообщения");
                break;
            case TypeFragment.PROFILE:
                setActionBar("Мой профиль");
                break;
        }


        if (fragmentOld == null) {
            setFragmentItem(fragment);
        }

        if (fragmentOld != null && !fragment.getClass().equals(fragmentOld.getClass())) {
            setFragmentItem(fragment);
        }
    }

    private void setFragmentItem(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayoutContent, fragment);
        ft.commit();
    }

    private void setActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(title);
    }

    private Fragment getFragmentOld() {
        return getSupportFragmentManager().findFragmentById(R.id.frameLayoutContent);
    }
}