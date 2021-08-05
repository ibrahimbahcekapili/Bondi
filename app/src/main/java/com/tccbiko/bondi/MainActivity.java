package com.tccbiko.bondi;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tccbiko.bondi.contacts.ContactsActivity;
import com.tccbiko.bondi.databinding.ActivityMainBinding;
import com.tccbiko.bondi.fragements.ChatsFragment;
import com.tccbiko.bondi.fragements.GroupFragment;
import com.tccbiko.bondi.groupchat.GroupContactsActivity;
import com.tccbiko.bondi.settings.settings.ProfileScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;


    @Override
    public void onBackPressed() {
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        setUpWithViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setCurrentItem(0);
        FloatingActionButton bondi_button = findViewById(R.id.bondi_button);

        bondi_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    startActivity(new Intent(MainActivity.this, ContactsActivity.class));

            }
        });

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeFabICon(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          getPermissions();
        }


        setSupportActionBar(binding.toolbar);


    }

private void setUpWithViewPager (ViewPager viewPager){
MainActivity.SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
adapter.addFragment(new ChatsFragment(),"Mesajlar");
adapter.addFragment(new GroupFragment(),"Gruplar");
viewPager.setAdapter(adapter);

    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();





        switch (id){
            case R.id.search_button :  Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_LONG).show(); break;
            case R.id.more_button:  Toast.makeText(MainActivity.this, "More", Toast.LENGTH_LONG).show(); break;
            case R.id.soon:  startActivity(new Intent(this, GroupContactsActivity.class));break;
            case R.id.settings:  startActivity(new Intent(this, ProfileScreen.class));
        }

        return super.onOptionsItemSelected(item);
    }





private void status (String status){

    firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    firestore= FirebaseFirestore.getInstance();
        HashMap<String, Object> hashMap = new HashMap<>();
    hashMap.put("status", status);

    firestore.collection("Users").document(firebaseUser.getUid()).update(hashMap);
}
protected void onResume() {

status("Yakınlarda");
    super.onResume();
}
    protected void onPause() {

        status("");
        super.onPause();
    }


   @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissions(){
        requestPermissions(new String[] {Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS},1);
    }

    private void changeFabICon(final int index){
        binding.bondiButton.hide();



        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                switch (index){

                    case 0 :
                        binding.bondiButton.show();
                        binding.bondiButton.setImageDrawable(getDrawable(R.drawable.bondi_white));
                        break;
                    case 1 :

                        binding.bondiButton.show();
                        binding.bondiButton.setImageDrawable(getDrawable(R.drawable.bondi_white));
                        break;


                }

            }
        },400);

        performOnClick(index);

    }
    private void performOnClick(final int index) {

        binding.bondiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                if (index == 0) {
                    startActivity(new Intent(MainActivity.this, ContactsActivity.class));
                } else if (index == 1) {
                   startActivity(new Intent(MainActivity.this, GroupContactsActivity.class));


                }
            }
        });
    }
}