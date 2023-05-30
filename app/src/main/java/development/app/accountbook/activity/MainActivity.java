package development.app.accountbook.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import development.app.accountbook.R;
import development.app.accountbook.databinding.ActivityMainBinding;
import development.app.accountbook.fragment.CalendarFragment;
import development.app.accountbook.fragment.CategorySettingFragment;
import development.app.accountbook.fragment.ListFragment;
import development.app.accountbook.fragment.MyPageFragment;
import development.app.accountbook.item.BackspaceHandler;
import development.app.accountbook.item.Singleton_Date;
import development.app.accountbook.viewmodel.UserViewModel;

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

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        drawerLayout = binding.drawerLayout;
        bsh = new BackspaceHandler(this);
        s_date = Singleton_Date.getInstance();
        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.setUserViewModel(this);

        // 상단 액션바 설정
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // 왼쪽 상단에 버튼 추가
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.image_menu); // 버튼 이미지 추가
        s_date.setDate(new SimpleDateFormat("yyyy년 MM월").format(new Date(System.currentTimeMillis()))); // 초기 타이틀 값 설정
        Objects.requireNonNull(getSupportActionBar()).setTitle(s_date.getDate());
        binding.navigationView.setItemIconTintList(null); // 네비게이션 아이콘 메뉴들 색상 그대로 표현하기 설정

        // 첫 화면 띄우기
        getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, f01, "f01").commit();

        // 네비게이션 드로어에 메뉴를 클릭했을 때 발생하는 이벤트
        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();
            changeFragment(menuItem.getItemId());
            return true;
        });

        // 네비게이션 드로어에 헤드에 로그아웃 이벤트 설정과 TextView 이름 설정
        binding.navigationView.getHeaderView(0).findViewById(R.id.logoutBtn).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
            builder.setTitle("안내").setMessage("로그아웃 하시겠습니까?");
            builder.setNegativeButton("아니오", ((dialogInterface, i) -> { }));
            builder.setPositiveButton("예", ((dialogInterface, i) -> {
                userViewModel.setAutoLogin(false);
                startActivity(new Intent(this, LoginActivity.class));
                finishAffinity();
            }));

            AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(dialogInterface -> {
                alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
            });
            alertDialog.show();
        });
        TextView userName = binding.navigationView.getHeaderView(0).findViewById(R.id.userName);
        userName.setText(userViewModel.getUserName());
    }


    // 선택에 따른 화면 전환
    @SuppressLint("NonConstantResourceId")
    private void changeFragment(int type) {
        switch (type) {
            case R.id.item_calendar:
                Objects.requireNonNull(getSupportActionBar()).setTitle(s_date.getDate()); // title 변경
                getSupportFragmentManager().beginTransaction().show(f01).commit();
                f01.calendarRefresh(); // 화면 갱신
                if(f02 != null) getSupportFragmentManager().beginTransaction().hide(f02).commit();
                if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                if(f04 != null) getSupportFragmentManager().beginTransaction().hide(f04).commit();
                break;
            case R.id.item_list:
                Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.menu02); // title 변경
                if(f02 == null) {
                    f02 = new ListFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.containerLayout, f02).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().show(f02).commit();
                    f02.listRefresh(); // 화면 갱신
                }
                if(f01 != null) getSupportFragmentManager().beginTransaction().hide(f01).commit();
                if(f03 != null) getSupportFragmentManager().beginTransaction().hide(f03).commit();
                if(f04 != null) getSupportFragmentManager().beginTransaction().hide(f04).commit();
                break;
            case R.id.item_myPage:
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


    // 상단에 메뉴 표시하기
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toollbar_menu, menu);
        return true;
    }


    // 메뉴 클릭 시 네비게이션 드로어 보여주기, 좌측 메뉴 클릭에 따른 이벤트 설정 > 좌측 메뉴만 설정 > 나머지는 calendarFragment
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
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