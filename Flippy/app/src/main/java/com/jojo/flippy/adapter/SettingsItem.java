package com.jojo.flippy.adapter;

import android.widget.ImageView;

/**
 * Created by bright on 6/12/14.
 */
public class SettingsItem {
    private String settingTitle;
    private String settingSubTitle;
    private int settingIcon;

    public SettingsItem(int settingIcon, String settingTitle, String settingSubTitle) {
        this.settingIcon = settingIcon;
        this.settingTitle = settingTitle;
        this.settingSubTitle = settingSubTitle;
    }

    public int getSettingIcon() {
        return settingIcon;
    }

    public String getSettingSubTitle() {
        return settingSubTitle;
    }

    public String getSettingTitle() {
        return settingTitle;
    }

    public void setSettingIcon(int settingIcon) {
        this.settingIcon = settingIcon;
    }

    public void setSettingSubTitle(String settingSubTitle) {
        this.settingSubTitle = settingSubTitle;
    }

    public void setSettingTitle(String settingTitle) {
        this.settingTitle = settingTitle;
    }
}
