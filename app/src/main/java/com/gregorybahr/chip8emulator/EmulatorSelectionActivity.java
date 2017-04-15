package com.gregorybahr.chip8emulator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.gregorybahr.chip8emulator.emulators.emulatorbase.Rom;

public class EmulatorSelectionActivity extends AppCompatActivity implements RomListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emulator_selection);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new RomFragmentPagerAdapter(getSupportFragmentManager(), EmulatorSelectionActivity.this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_slider);
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public void onRomSelected(Rom rom) {
        Intent i = null;
        switch (rom.getEmulatorType()) {
            case CHIP8:
                i = new Intent(EmulatorSelectionActivity.this, Chip8EmulatorActivity.class);
                break;
            case NES:
                i = new Intent(EmulatorSelectionActivity.this, NesEmulatorActivity.class);
                break;
        }
        i.putExtra("rom", rom);
        startActivity(i);
    }
}
