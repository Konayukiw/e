package com.anonymous.e.module.setting.impl;

import com.anonymous.e.module.setting.Setting;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderSetting extends Setting {

    private String settingName;
    private String[] options;
    private double defaultValue;
    private double max;
    private double min;
    private double intervals;
    public boolean isString;
    private String suffix;

    public SliderSetting(String settingName, String suffix, double defaultValue, double min, double max, double intervals) {
        super(settingName);
        this.suffix = suffix;
        this.settingName = settingName;
        this.options = null;
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.intervals = intervals;
        this.isString = false;
    }

    public SliderSetting(String settingName, double defaultValue, double min, double max, double intervals) {
        this(settingName, "", defaultValue, min, max, intervals);
    }

    public SliderSetting(String settingName, int defaultValue, String[] options) {
        super(settingName);
        this.suffix = "";
        this.settingName = settingName;
        this.options = options;
        this.defaultValue = (double) defaultValue;
        this.min = 0.0;
        this.max = (double) (options.length - 1);
        this.intervals = 1.0;
        this.isString = true;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String[] getOptions() {
        return this.options;
    }

    @Override
    public String getName() {
        return this.settingName;
    }

    public double getInput() {
        return roundToInterval(this.defaultValue, 4);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public double setValue(double newValue) {
        newValue = Math.max(this.min, newValue);
        newValue = Math.min(this.max, newValue);
        newValue = Math.round(newValue * (1.0 / this.intervals)) / (1.0 / this.intervals);
        return this.defaultValue = newValue;
    }

    public void setValueRaw(double n) {
        this.defaultValue = n;
    }

    public static double roundToInterval(double v, int p) {
        if (p < 0) return 0.0;
        BigDecimal bd = new BigDecimal(v);
        bd = bd.setScale(p, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
