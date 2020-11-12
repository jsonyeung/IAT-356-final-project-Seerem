package com.example.seeremapp.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.seeremapp.fragment.PlaceholderFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class WorksitePagerAdapter extends FragmentPagerAdapter {

  private static final String[] TAB_TITLES = {"Worksite", "Online", "Settings"};
  private final Context mContext;

  public WorksitePagerAdapter(Context context, FragmentManager fm) {
    super(fm);
    mContext = context;
  }

  @Override
  public Fragment getItem(int position) {
    // getItem is called to instantiate the fragment for the given page.
    // Return a PlaceholderFragment (defined as a static inner class below).
    return PlaceholderFragment.newInstance(position + 1);
  }

  @Nullable
  @Override
  public CharSequence getPageTitle(int position) {
    return TAB_TITLES[position];
  }

  @Override
  public int getCount() {
    return 3;
  }
}