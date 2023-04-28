package com.example.accountbook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.accountbook.R;
import com.example.accountbook.databinding.ActivityMainBinding;
import com.example.accountbook.fragment.CalendarFragment;
import com.example.accountbook.fragment.CategorySettingFragment;
import com.example.accountbook.fragment.ListFragment;
import com.example.accountbook.fragment.MyPageFragment;
import com.example.accountbook.item.BackspaceHandler;
import com.example.accountbook.item.Singleton_Date;
import com.example.accountbook.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private BackspaceHandler bsh;
    private final CalendarFragment f01 = new CalendarFragment();
    private ListFragment f02 = null;
    private MyPageFragment f03 = null;
    private CategorySettingFragment f04 = null;
    private Singleton_Date s_date;
    private UserViewModel userViewModel;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        drawerLayout = binding.drawerLayout;
        bsh = new BackspaceHandler(this);
        s_date = Singleton_Date.getInstance();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setUserViewModel(this);

        // 상단 액션바 설정
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // 왼쪽 상단에 버튼 추가
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.image_menu); // 버튼 이미지 추가
        s_date.setDate(new SimpleDateFormat("yyyy년 MM월").format(new Date(System.currentTimeMillis()))); // 초기 타이틀 값 설정
        Objects.requireNonNull(getSupportActionBar()).setTitle(s_date.getDate());
        binding.navigationView.setItemIconTintList(null); // 네비게이션 아이콘 메뉴들 색상 그대로 표현하기 설정

        // 첫 화면 띄우기
        getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, f01).commit();

        // 네비게이션 드로어에 메뉴를 클릭했을 때 발생하는 이벤트
        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();
            changeFragment(menuItem.getItemId());
            return true;
        });

        // 네비게이션 드로어에 헤드에 로그아웃 이벤트 설정과 TextView 이름 설정
        binding.navigationView.getHeaderView(0).findViewById(R.id.logoutBtn).setOnClickListener(v -> {
            Toast.makeText(this, "로그아웃 클릭", Toast.LENGTH_SHORT).show();
        });
        TextView userName = binding.navigationView.getHeaderView(0).findViewById(R.id.userName);
        userName.setText(userViewModel.getUserName());
    }


    // 선택에 따른 화면 전환
    @SuppressLint("NonConstantResourceId")
    private void changeFragment(int type) {
        switch (type) {
            case R.id.item_calendar:
                showOptionMenu(true);
                Objects.requireNonNull(getSupportActionBar()).setTitle(s_date.getDate()); // title 변경
                getSupportFragmentManager().beginTransaction().show(f01).commit();
                if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                if(f04 != null) getSupportFragmentManager().beginTransaction().hide(f04).commit();
                break;
            case R.id.item_list:
                showOptionMenu(false);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.menu02); // title 변경
                findViewById(R.id.tool_menu01).setVisibility(View.GONE);
                if(f02 == null) {
                    f02 = new ListFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, f02).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().show(f02).commit();
                }
                if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                if(f04 != null) getSupportFragmentManager().beginTransaction().hide(f04).commit();
                break;
            case R.id.item_myPage:
                showOptionMenu(false);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.menu03); // title 변경
                if(f03 == null) {
                    f03 = new MyPageFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, f03).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().show(f03).commit();
                }
                if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                if(f04 != null) getSupportFragmentManager().beginTransaction().hide(f04).commit();
                break;
            case R.id.item_categorySettings:
                showOptionMenu(false);
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.menu04); // title 변경
                if(f04 == null) {
                    f04 = new CategorySettingFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, f04).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().show(f04).commit();
                }
                if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                break;
        }
    }


    // 좌측 상단에 메뉴(월마감, 계좌이체) 표시하기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toollbar_menu, menu);
        return true;
    }
    // 좌측 상단 메뉴를 화면 별 보이기/숨기기
    private void showOptionMenu(boolean isShow) {
        int visible;
        if(isShow) {
            visible = View.VISIBLE;
        } else {
            visible = View.GONE;
        }
        findViewById(R.id.tool_menu01).setVisibility(visible);
        findViewById(R.id.tool_menu02).setVisibility(visible);
    }


    // 메뉴 클릭 시 네비게이션 드로어 보여주기, 좌측 메뉴 클릭에 따른 이벤트 설정
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.tool_menu01:
                Toast.makeText(this, "월마감 클릭함", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tool_menu02:
                startActivity(new Intent(this, Popup_Transfer.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    // 뒤로가기 눌렀을 때 종료 막고 네비게이션 드로어가 열려 있으면 닫기
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            bsh.onBackPressed("'뒤로가기'를 한번 더 누르면 종료됩니다.");
        }
    }
}