package com.example.seeremapp.adapter;

import android.content.Context;

import com.example.seeremapp.fragment.WorksiteDashboardFragment;
import com.example.seeremapp.fragment.WorksiteDashboardSettingsFragment;
import com.example.seeremapp.fragment.WorksiteDashboardUsersFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class WorksitePagerAdapter extends FragmentPagerAdapter {

  private static final String[] TAB_TITLES = {"Details", "Users", "Other"};
  private final Context mContext;
  private int wid;

  public WorksitePagerAdapter(Context context, FragmentManager fm, int id) {
    super(fm);
    mContext = context;
    wid = id;
  }

  @Override
  public Fragment getItem(int position) {
    // getItem is called to instantiate the fragment for the given page.
    // Return a PlaceholderFragment (defined as a static inner class below).
    Fragment fragment = null;
    switch (position) {
      case 0:
        fragment = WorksiteDashboardFragment.newInstance(wid);
        break;
      case 1:
        fragment = WorksiteDashboardUsersFragment.newInstance(wid);
        break;
      case 2:
        fragment = WorksiteDashboardSettingsFragment.newInstance(wid);
        break;
    }

    return fragment;
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