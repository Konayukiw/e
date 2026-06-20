package com.anonymous.e.module.setting.impl;

import com.anonymous.e.module.setting.Setting;

public class ButtonSetting extends Setting {

    private String name;
    private boolean isEnabled;

    public ButtonSetting(String name, boolean isEnabled) {
        super(name);
        this.name = name;
        this.isEnabled = isEnabled;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public boolean isToggled() {
        return this.isEnabled;
    }

    public void toggle() {
        this.isEnabled = !this.isEnabled;
    }

    public void enable() {
        this.isEnabled = true;
    }

    public void disable() {
        this.isEnabled = false;
    }

    public void setEnabled(boolean b) {
        this.isEnabled = b;
    }
}
