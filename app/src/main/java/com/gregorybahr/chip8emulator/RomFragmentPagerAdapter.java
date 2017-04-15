package com.gregorybahr.chip8emulator;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gregorybahr.chip8emulator.emulators.EmulatorType;

/**
 * Created by greg on 4/15/2017.
 */

public class RomFragmentPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private String[] tabTitles = new String[] {"Chip8", "NES"};
    private Context context;

    public RomFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RomListFragment.newInstance(EmulatorType.CHIP8);
            case 1:
                return RomListFragment.newInstance(EmulatorType.NES);
        }
        throw new IllegalArgumentException("Illegal EmulatorType Selected.");
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
