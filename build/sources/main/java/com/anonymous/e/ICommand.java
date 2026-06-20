package com.anonymous.e;

import com.anonymous.e.module.Fastmine;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.Collections;
import java.util.List;

public class ICommand extends CommandBase {

    private static final String PREFIX = "§7[§de§7] ";

    private final Fastmine fm;
    private final ConfigUtils cfg;

    public ICommand(Fastmine fm, ConfigUtils cfg) {
        this.fm  = fm;
        this.cfg = cfg;
    }

    @Override
    public String getCommandName() {
        return "fm";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/fm [toggle|set|save|help]";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("fm");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        System.out.println("[e] Command received. Args length: " + args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.println("[e] arg[" + i + "]: '" + args[i] + "'");
        }
        
        if (args.length == 0) {
            printStatus(sender);
            return;
        }

        switch (args[0].toLowerCase()) {

            case "toggle":
                if (fm.isEnabled()) { fm.disable(); send(sender, PREFIX + "Fastmine §cDISABLED"); }
                else                { fm.enable();  send(sender, PREFIX + "Fastmine §aENABLED");  }
                cfg.save(fm);
                break;

            case "set":
                handleSet(sender, args);
                break;

            case "save":
                cfg.save(fm);
                send(sender, PREFIX + "Config saved to §econfigsé/fm.json");
                break;

            case "help":
                sendHelp(sender);
                break;

            default:
                send(sender, PREFIX + "Unknown command. Use §e/fm help");
        }
    }

    private void handleSet(ICommandSender sender, String[] args) {
        if (args.length < 3) {
            send(sender, PREFIX + "Usage: /fm set <key> <value>");
            return;
        }
        String key   = args[1].toLowerCase();
        String value = args[2].toLowerCase();

        switch (key) {

            case "delay": {
                double v = parseDouble(sender, value, fm.delay.getMin(), fm.delay.getMax());
                if (Double.isNaN(v)) return;
                fm.delay.setValue(v);
                send(sender, PREFIX + "Break delay → §f" + (int) fm.delay.getInput() + " tick(s)");
                cfg.save(fm);
                break;
            }

            case "multiplier":
            case "speed": {
                double v = parseDouble(sender, value, fm.multiplier.getMin(), fm.multiplier.getMax());
                if (Double.isNaN(v)) return;
                fm.multiplier.setValue(v);
                send(sender, "Break speed §e→ §f" + fm.multiplier.getInput() + "x");
                cfg.save(fm);
                break;
            }

            case "mode": {
                int idx = parseModeIndex(sender, value);
                if (idx < 0) return;
                fm.mode.setValue(idx);
                String modeName = fm.mode.getOptions()[idx];
                send(sender, PREFIX + "Mode → §f" + modeName);
                cfg.save(fm);
                break;
            }

            case "creative":
            case "disableincreative": {
                boolean on = parseBool(sender, value);
                if (on)  fm.creativeDisable.enable();
                else     fm.creativeDisable.disable();
                send(sender, PREFIX + "Disable in creative → §f" + (fm.creativeDisable.isToggled() ? "ON" : "OFF"));
                cfg.save(fm);
                break;
            }

            default:
                send(sender, "§cUnknown key §e" + key + "§7. Valid keys: delay, multiplier, mode, creative");
        }
    }

    private void printStatus(ICommandSender sender) {
        String enabledStr = fm.isEnabled() ? "§aON" : "§cOFF";
        String modeStr    = fm.mode.getOptions()[(int) fm.mode.getInput()];
        send(sender, PREFIX + "Fastmine: " + enabledStr);
        send(sender, PREFIX + "  delay       = §e" + (int) fm.delay.getInput() + "§f tick(s)  §7(vanilla: 5)");
        send(sender, PREFIX + "  multiplier  = §e" + fm.multiplier.getInput() + "§fx  §7(vanilla: 1.0)");
        send(sender, PREFIX + "  mode        = §e" + modeStr);
        send(sender, PREFIX + "  creative    = §e" + (fm.creativeDisable.isToggled() ? "disable" : "allow"));
        send(sender, PREFIX + "Type §e/fm help §7for commands.");
    }

    private void sendHelp(ICommandSender sender) {
        send(sender, PREFIX + "§e/fm §7— show current settings");
        send(sender, PREFIX + "§e/fm toggle §7— enable / disable");
        send(sender, PREFIX + "§e/fm set delay §f<0-5> §7— break delay ticks");
        send(sender, PREFIX + "§e/fm set multiplier §f<1.0-2.0> §7— speed multiplier");
        send(sender, PREFIX + "§e/fm set mode §f<pre|post|inc> §7— algorithm");
        send(sender, PREFIX + "§e/fm set creative §f<on|off> §7— disable in creative");
        send(sender, PREFIX + "§e/fm save §7— force-save config to disk");
    }

    private void send(ICommandSender sender, String msg) {
        sender.addChatMessage(new ChatComponentText(PREFIX + msg));
    }

    private double parseDouble(ICommandSender sender, String s, double min, double max) {
        try {
            double v = Double.parseDouble(s);
            if (v < min || v > max) {
                send(sender, PREFIX + "Value must be between " + min + " and " + max);
                return Double.NaN;
            }
            return v;
        } catch (NumberFormatException e) {
            send(sender, PREFIX + "Not a number: §e" + s);
            return Double.NaN;
        }
    }

    private int parseModeIndex(ICommandSender sender, String s) {
        switch (s) {
            case "pre":  return 0;
            case "post": return 1;
            case "inc":
            case "increment": return 2;
            default:
                send(sender, PREFIX + "Unknown mode §e" + s + "§7. Use: pre / post / inc");
                return -1;
        }
    }

    private boolean parseBool(ICommandSender sender, String s) {
        return s.equals("on") || s.equals("true") || s.equals("1") || s.equals("yes");
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, net.minecraft.util.BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "toggle", "set", "save", "help");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return getListOfStringsMatchingLastWord(args, "delay", "multiplier", "mode", "creative");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            switch (args[1].toLowerCase()) {
                case "mode":
                    return getListOfStringsMatchingLastWord(args, "pre", "post", "inc");
                case "creative":
                    return getListOfStringsMatchingLastWord(args, "on", "off");
            }
        }
        return Collections.emptyList();
    }
}
