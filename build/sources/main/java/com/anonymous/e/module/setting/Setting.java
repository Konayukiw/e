package com.anonymous.e.module.setting;

public abstract class Setting {

    public String name;
    public boolean visible = true;

    public Setting(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
