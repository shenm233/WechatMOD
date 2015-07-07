package dg.shenm233.wechatmod.hooks.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.TreeSet;

import chrisrenke.drawerarrowdrawable.DrawerArrowDrawable;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import dg.shenm233.wechatmod.Common;
import dg.shenm233.wechatmod.ObfuscationHelper;
import dg.shenm233.wechatmod.R;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.getStaticObjectField;
import static dg.shenm233.wechatmod.BuildConfig.DEBUG;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Classes;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Fields;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Methods;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Res;


public class LauncherUI {
    //save tabview instance for getting unread message
    Object tabView;

    public void init(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //remove tabview :P
        findAndHookMethod(MM_Classes.LauncherUI, MM_Methods.createTabView, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ViewGroup customViewPager = (ViewGroup) getObjectField(param.thisObject, MM_Fields.customViewPager);
                tabView = getObjectField(param.thisObject, MM_Fields.tabView);
                ((ViewGroup) customViewPager.getParent()).removeView((View) tabView);
                if (DEBUG) ObfuscationHelper.getRawXml(MM_Res.main_tab, Common.MM_Context);
                addNavigationDrawer((Activity) param.thisObject);
                callMethod(customViewPager, "setCanSlide", false);
            }
        });

        findAndHookMethod(MM_Classes.LauncherUI, "dispatchKeyEvent", KeyEvent.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (mDrawer != null && ((KeyEvent) param.args[0]).getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                        drawerLayout.closeDrawers();
                        param.setResult(true);
                    }
                }
            }
        });

        findAndHookMethod(MM_Classes.LauncherUI, MM_Methods.initActionBar, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                initNewActionBar((Activity) param.thisObject);
            }
        });
    }

    DrawerLayout drawerLayout;
    View mDrawer;
    ListView mDrawerList;
    DrawerListAdapter drawerListAdapter;
    DrawerArrowDrawable drawerArrowDrawable;

    private void initNewActionBar(Activity activity) throws Throwable {
        Object actionBar = getObjectField(activity, MM_Fields.actionBar);
        View actionBarView = (View) callMethod(actionBar, "getCustomView");

        //add DrawerArrowDrawable to ActionBar
        ViewGroup newActionBarView = (ViewGroup) View.inflate(Common.MOD_Context, R.layout.actionbar_container, null);
        ImageView iv = (ImageView) newActionBarView.findViewById(R.id.drawer_indicator);

        //create DrawerArrowDrawable
        drawerArrowDrawable = new DrawerArrowDrawable((Resources) callMethod(activity, "getResources"));
        drawerArrowDrawable.setStrokeColor(Common.MOD_RES.getColor(R.color.drawer_indicator_color));
        iv.setImageDrawable(drawerArrowDrawable);

        //remove original view,and then add again
        ((ViewGroup) actionBarView.getParent()).removeView(actionBarView);
        newActionBarView.addView(actionBarView);
        callMethod(actionBar, "setCustomView", newActionBarView);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });
    }

    private void addNavigationDrawer(Activity activity) throws Throwable {
        drawerLayout = new DrawerLayout(activity);
        drawerLayout.setFitsSystemWindows(true);
        drawerLayout.setFocusable(true);
        drawerLayout.setFocusableInTouchMode(true);

        //Create Drawer
        mDrawer = View.inflate(Common.MOD_Context, R.layout.drawer, null);
        DrawerLayout.LayoutParams lp =
                new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.START;
        mDrawer.setLayoutParams(lp);

        //Drawer List
        mDrawerList = (ListView) mDrawer.findViewById(R.id.drawer_list);
        drawerListAdapter = new DrawerListAdapter(Common.MOD_Context);
        mDrawerList.setAdapter(drawerListAdapter);
        mDrawerList.setOnItemClickListener(drawerListAdapter);
        initDrawerList(drawerListAdapter);
        mDrawerList.setItemsCanFocus(true);

        //remove orginal frameLayout that including customViewPager,tabView...
        View main_tab = (View) getObjectField(activity, MM_Fields.main_tab);
        ((ViewGroup) main_tab.getParent()).removeView(main_tab);
        drawerLayout.addView(main_tab);
        drawerLayout.addView(mDrawer);

        //go go go
        activity.addContentView(drawerLayout,
                new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT));
        //don't use activity.addContentView(drawerLayout);  because it causes activity exit.

        ImageView user_avatar = (ImageView) mDrawer.findViewById(R.id.user_avatar);
        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragments.callMMFragmentFeature(3, "more_tab_setting_personal_info");
            }
        });

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    drawerArrowDrawable.setFlip(true);
                } else if (slideOffset <= .005) {
                    drawerArrowDrawable.setFlip(false);
                }

                drawerArrowDrawable.setParameter(slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                refreshDrawerInfo();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void initDrawerList(DrawerListAdapter drawerListAdapter) {
        //chatting
        drawerListAdapter.addItem(R.drawable.main_chat, R.string.main_chat);
        //contact
        drawerListAdapter.addItem(R.drawable.main_contact, R.string.main_contact);
        //Discovery
        drawerListAdapter.addSectionHeaderItem(R.string.main_addcontact);

        //
        drawerListAdapter.addItem(R.drawable.sns_moments, R.string.sns_moments);
        drawerListAdapter.addItem(R.drawable.sns_scan, R.string.sns_scan);
        drawerListAdapter.addItem(R.drawable.sns_shake, R.string.sns_shake);
        drawerListAdapter.addItem(R.drawable.sns_people_nearby, R.string.sns_people_nearby);
        drawerListAdapter.addItem(R.drawable.sns_drift_bottle, R.string.sns_drift_bottle);
        drawerListAdapter.addItem(R.drawable.sns_shopping, R.string.sns_shopping);
        drawerListAdapter.addItem(R.drawable.sns_games, R.string.sns_games);

        //Me
        drawerListAdapter.addSectionHeaderItem(R.string.main_more);

        //
        drawerListAdapter.addItem(R.drawable.me_posts, R.string.me_posts);
        drawerListAdapter.addItem(R.drawable.me_favorites, R.string.me_favorites);
        drawerListAdapter.addItem(R.drawable.me_wallet, R.string.me_wallet);
        drawerListAdapter.addItem(R.drawable.me_settings, R.string.me_settings);
    }

    private void refreshDrawerInfo() {
        //background image
        ImageView bg_image = (ImageView) mDrawer.findViewById(R.id.bg_image);
        bg_image.setImageDrawable(Common.MOD_RES.getDrawable(R.drawable.bg_test));

        //avatar image
        ImageView user_avatar = (ImageView) mDrawer.findViewById(R.id.user_avatar);
//        user_avatar.setImageDrawable(Common.MOD_RES.getDrawable(R.drawable.avatar_test));
        setAvatar(user_avatar);

        //username,wechat name
        TextView username = (TextView) mDrawer.findViewById(R.id.username);
        CharSequence str = getNickname();
        if (str != null)
            username.setText(str);
        username.append("\n" + Common.MOD_RES.getText(R.string.username) + getUsername());

        try {
            //set Unread message
            int i;
            i = (int) callMethod(tabView, "getMainTabUnread");
            drawerListAdapter.setMainChattingUnread(i);
            i = (int) callMethod(tabView, "getContactTabUnread");
            drawerListAdapter.setContactUnread(i);
            Object object = getStaticObjectField(MM_Classes.WTFClazz, MM_Fields.moments_jj);
            i = object != null ? (int) callMethod(object, MM_Methods.getMomentsUnreadCount) : 0;
            drawerListAdapter.setMomentsUnread(i);
            if (i == 0) {
                boolean showPoint = (boolean) callMethod(tabView, "getShowFriendPoint");
                drawerListAdapter.setMomentsPoint(showPoint);
            }
            i = (int) callMethod(callStaticMethod(MM_Classes.NewFriendMessage, MM_Methods.getShakeVerifyMessage), MM_Methods.getVerifyMessageCount);
            drawerListAdapter.setShakeUnread(i);
            i = (int) callMethod(callStaticMethod(MM_Classes.NewFriendMessage, MM_Methods.getLBSVerifyMessage), MM_Methods.getVerifyMessageCount);
            drawerListAdapter.setNearbyPeopleUnread(i);
            i = (int) callStaticMethod(MM_Classes.Bottle, MM_Methods.getBottleUnreadCount);
            drawerListAdapter.setDriftBottleUnread(i);
        } catch (Throwable l) {
            if (DEBUG) XposedBridge.log(l);
        }
    }

    private CharSequence getNickname() {
        Object object = callStaticMethod(MM_Classes.AccountStorage, MM_Methods.getAccStg);
        Object obj = callMethod(callMethod(object, MM_Methods.getUserInfoFromDB), "get", 4, null);
        if (obj != null && ((String) obj).length() > 0)
            return (CharSequence) callStaticMethod(MM_Classes.UserNickName, MM_Methods.getNickname, Common.MM_Context, (CharSequence) obj);
        else
            return null;
    }

    private String getUsername() {
        String str = (String) callStaticMethod(MM_Classes.UserInfo, MM_Methods.getUsername);
        if (str == null)
            str = (String) callStaticMethod(MM_Classes.UserInfo, MM_Methods.getOrigUsername);
        return str;
    }

    private void setAvatar(ImageView imageView) {
        String str = (String) callStaticMethod(MM_Classes.UserInfo, MM_Methods.getOrigUsername);
        callStaticMethod(MM_Classes.Avatar, MM_Methods.setAvatarByOrigUsername, imageView, str);
    }

    private class DrawerListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        private Context mContext;
        private ArrayList<DrawerListItem> mDrawerListItems = new ArrayList<DrawerListItem>();
        private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

        private final int TYPE_ITEM = 0;
        private final int TYPE_SEPARATOR = 1;

        public DrawerListAdapter(Context context) {
            mContext = context;
        }

        public void addItem(int IconResid, int TextResid) {
            mDrawerListItems.add(new DrawerListItem(IconResid, TextResid));
            notifyDataSetChanged();
        }

        public void addSectionHeaderItem(int TextResid) {
            mDrawerListItems.add(new DrawerListItem(TextResid));
            sectionHeader.add(mDrawerListItems.size() - 1);
            notifyDataSetChanged();
        }

        private class ViewHolder {
            ImageView icon;
            TextView text;
            TextView unread;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DrawerListItem drawerListItem = mDrawerListItems.get(position);
            int ItemType = getItemViewType(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                switch (ItemType) {
                    case TYPE_SEPARATOR:
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.header, parent, false);
                        convertView.setEnabled(false);
                        viewHolder.text = (TextView) convertView.findViewById(R.id.header_text);
                        break;
                    default:
                    case TYPE_ITEM:
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.drawer_list_item, parent, false);
                        viewHolder.text = (TextView) convertView.findViewById(R.id.drawerlist_text);
                        viewHolder.icon = (ImageView) convertView.findViewById(R.id.drawerlist_icon);
                        viewHolder.unread = (TextView) convertView.findViewById(R.id.drwerlist_unread);
                        break;
                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.text.setText(Common.MOD_RES.getText(drawerListItem.TEXT_ID));
            if (ItemType == TYPE_ITEM) {
                viewHolder.icon.setImageDrawable(Common.MOD_RES.getDrawable(drawerListItem.ICON_ID));
                viewHolder.unread.setText(drawerListItem.unread);
            }

            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return mDrawerListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mDrawerListItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DrawerListItem drawerListItem = mDrawerListItems.get(position);
            int text_id = drawerListItem.TEXT_ID;
            callMMFeature(text_id);
        }

        /*set unread message*/
        public void setMainChattingUnread(int count) {
            String text;
            if (count > 0) text = Integer.toString(count);
            else text = "";
            mDrawerListItems.get(0).unread = text;
            notifyDataSetChanged();
        }

        public void setContactUnread(int count) {
            String text;
            if (count > 0) text = Integer.toString(count);
            else text = "";
            mDrawerListItems.get(1).unread = text;
            notifyDataSetChanged();
        }

        public void setMomentsUnread(int count) {
            String text;
            if (count > 0) text = Integer.toString(count);
            else text = "";
            mDrawerListItems.get(3).unread = text;
            notifyDataSetChanged();
        }

        public void setMomentsPoint(boolean show) {
            String text;
            if (show) text = "new";
            else text = "";
            mDrawerListItems.get(3).unread = text;
            notifyDataSetChanged();
        }

        public void setNearbyPeopleUnread(int count) {
            String text;
            if (count > 0) text = Integer.toString(count);
            else text = "";
            mDrawerListItems.get(5).unread = text;
            notifyDataSetChanged();
        }

        public void setShakeUnread(int count) {
            String text;
            if (count > 0) text = Integer.toString(count);
            else text = "";
            mDrawerListItems.get(6).unread = text;
            notifyDataSetChanged();
        }

        public void setDriftBottleUnread(int count) {
            String text;
            if (count > 0) text = Integer.toString(count);
            else text = "";
            mDrawerListItems.get(7).unread = text;
            notifyDataSetChanged();
        }
    }

    private void callMMFeature(int StrResid) {
        drawerLayout.closeDrawers();
        switch (StrResid) {
            case R.string.main_chat:
                MainFragments.switchMMFragment(0);
                break;
            case R.string.main_contact:
                MainFragments.switchMMFragment(1);
                break;
            case R.string.main_addcontact:
                MainFragments.switchMMFragment(2);
                break;
            case R.string.main_more:
                MainFragments.switchMMFragment(3);
                break;
            case R.string.sns_moments:
                MainFragments.callMMFragmentFeature(2, "album_dyna_photo_ui_title");
                break;
            case R.string.sns_scan:
                MainFragments.callMMFragmentFeature(2, "find_friends_by_qrcode");
                break;
            case R.string.sns_shake:
                MainFragments.callMMFragmentFeature(2, "find_friends_by_shake");
                break;
            case R.string.sns_people_nearby:
                MainFragments.callMMFragmentFeature(2, "find_friends_by_near");
                break;
            case R.string.sns_drift_bottle:
                MainFragments.callMMFragmentFeature(2, "voice_bottle");
                break;
            case R.string.sns_shopping:
                MainFragments.callMMFragmentFeature(2, "jd_market_entrance");
                break;
            case R.string.sns_games:
                MainFragments.callMMFragmentFeature(2, "more_tab_game_recommend");
                break;
            case R.string.me_posts:
                MainFragments.callMMFragmentFeature(3, "settings_my_album");
                break;
            case R.string.me_favorites:
                MainFragments.callMMFragmentFeature(3, "settings_mm_favorite");
                break;
            case R.string.me_wallet:
                MainFragments.callMMFragmentFeature(3, "settings_mm_wallet");
                break;
            case R.string.me_settings:
                MainFragments.callMMFragmentFeature(3, "more_setting");
                break;
        }
    }

    private class DrawerListItem {
        int ICON_ID;
        int TEXT_ID;
        String unread = "";

        public DrawerListItem(int TextResid) {
            TEXT_ID = TextResid;
        }

        public DrawerListItem(int IconResid, int TextResid) {
            ICON_ID = IconResid;
            TEXT_ID = TextResid;
        }

        public void setIcon(int resId) {
            ICON_ID = resId;
        }

        public void setText(int resId) {
            TEXT_ID = resId;
        }
    }
}
