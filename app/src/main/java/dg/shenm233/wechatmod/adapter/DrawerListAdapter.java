package dg.shenm233.wechatmod.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.TreeSet;

import dg.shenm233.wechatmod.Common;
import dg.shenm233.wechatmod.Common.DrawerListItem;
import dg.shenm233.wechatmod.R;
import dg.shenm233.wechatmod.hooks.ui.LauncherUI;
import dg.shenm233.wechatmod.hooks.ui.MMFragmentActivity;

public class DrawerListAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    private Context mContext;
    private SparseArray<DrawerListItem> mDrawerListItems = new SparseArray<DrawerListItem>(15);
    private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

    private final int TYPE_ITEM = 0;
    private final int TYPE_SEPARATOR = 1;

    private int mHighlightedItemPosition;

    public DrawerListAdapter(Context context) {
        mContext = context;
    }

    public void addItem(int key, int IconResid, int TextResid) {
        mDrawerListItems.put(key, new DrawerListItem(key, IconResid, TextResid));
        notifyDataSetChanged();
    }

    public void addSectionHeaderItem(int key, int TextResid) {
        mDrawerListItems.put(key, new DrawerListItem(key, TextResid));
        sectionHeader.add(mDrawerListItems.indexOfKey(key));
        notifyDataSetChanged();
    }

    private class ViewHolder {
        ImageView icon;
        TextView text;
        TextView unread;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DrawerListItem drawerListItem = getItem(position);
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

        if (drawerListItem.KEY == Common.item_sns_shopping) {
            //get "shopping" text from module,because wechat don't have this string.
            viewHolder.text.setText(Common.MOD_RES.getText(drawerListItem.TEXT_ID));
        } else {
            viewHolder.text.setText(Common.MM_Context.getResources().getText(drawerListItem.TEXT_ID));
        }
        if (ItemType == TYPE_ITEM) {
            viewHolder.icon.setImageDrawable(Common.MOD_RES.getDrawable(drawerListItem.ICON_ID));
            viewHolder.unread.setText(drawerListItem.unread);
        }
        if (position == mHighlightedItemPosition) {
            convertView.setBackgroundColor(Common.MOD_RES.getColor(R.color.item_selected_bg_color));
            viewHolder.text.setTextColor(MMFragmentActivity.actionBarColor);
            if (ItemType == TYPE_ITEM) {
                viewHolder.icon.setColorFilter(MMFragmentActivity.actionBarColor);
                viewHolder.unread.setTextColor(MMFragmentActivity.actionBarColor);
            }
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.text.setTextColor(Common.MOD_RES.getColor(R.color.item_text_default_color));
            if (ItemType == TYPE_ITEM) {
                viewHolder.icon.clearColorFilter();
                viewHolder.unread.setTextColor(Common.MOD_RES.getColor(R.color.item_text_default_color));
            }
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
    public DrawerListItem getItem(int position) {
        return mDrawerListItems.valueAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int key = mDrawerListItems.keyAt(position);
        if (key == Common.item_main_chat || key == Common.item_main_contact
                || key == Common.item_main_addcontact || key == Common.item_main_more) {
            setSingleItemHighlighted(key);
        }
        LauncherUI.callMMFeature(key);
    }

    public void setSingleItemHighlighted(int key) {
        mHighlightedItemPosition = mDrawerListItems.indexOfKey(key);
        notifyDataSetChanged();
    }

    /*set unread message*/
    public void setMainChattingUnread(int count) {
        String text;
        if (count > 0) text = Integer.toString(count);
        else text = "";
        mDrawerListItems.get(Common.item_main_chat).unread = text;
        notifyDataSetChanged();
    }

    public void setContactUnread(int count) {
        String text;
        if (count > 0) text = Integer.toString(count);
        else text = "";
        mDrawerListItems.get(Common.item_main_contact).unread = text;
        notifyDataSetChanged();
    }

    public void setMomentsUnread(int count) {
        String text;
        if (count > 0) text = Integer.toString(count);
        else text = "";
        mDrawerListItems.get(Common.item_sns_moments).unread = text;
        notifyDataSetChanged();
    }

    public void setMomentsPoint(boolean show) {
        String text;
        if (show) text = "new";
        else text = "";
        mDrawerListItems.get(Common.item_sns_moments).unread = text;
        notifyDataSetChanged();
    }

    public void setNearbyPeopleUnread(int count) {
        String text;
        if (count > 0) text = Integer.toString(count);
        else text = "";
        mDrawerListItems.get(Common.item_sns_people_nearby).unread = text;
        notifyDataSetChanged();
    }

    public void setShakeUnread(int count) {
        String text;
        if (count > 0) text = Integer.toString(count);
        else text = "";
        mDrawerListItems.get(Common.item_sns_shake).unread = text;
        notifyDataSetChanged();
    }

    public void setDriftBottleUnread(int count) {
        String text;
        if (count > 0) text = Integer.toString(count);
        else text = "";
        mDrawerListItems.get(Common.item_sns_drift_bottle).unread = text;
        notifyDataSetChanged();
    }
}