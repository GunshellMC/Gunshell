package com.jazzkuh.gunshell.compatibility.extensions.combattagplus;

import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.compatibility.framework.Extension;
import com.jazzkuh.gunshell.compatibility.framework.ExtensionInfo;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.TagManager;

@ExtensionInfo(name = "CombatTagPlusExtension", loadPlugin = "CombatTagPlus")
public class CombatTagPlusExtension implements Extension {
    @Override
    public void onEnable() {
        GunshellPlugin.getInstance().getLogger().info("CombatTagPlus compatibility layer enabled!");
    }

    @Override
    public void onDisable() {
        GunshellPlugin.getInstance().getLogger().info("CombatTagPlus compatibility layer disabled!");
    }

    @Override
    public void onLoad() {
    }

    public TagManager getTagManager() {
         return CombatTagPlus.getPlugin(CombatTagPlus.class).getTagManager();
    }
}
