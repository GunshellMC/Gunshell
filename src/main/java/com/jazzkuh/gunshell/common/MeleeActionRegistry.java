package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.api.enums.BuiltinAmmoActionType;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.common.actions.melee.MeleeDamageAction;
import com.jazzkuh.gunshell.common.actions.melee.abstraction.AbstractMeleeAction;

import java.util.HashMap;
import java.util.Map;

public class MeleeActionRegistry {
    private static Map<String, Class<? extends AbstractMeleeAction>> actions = new HashMap<>();

    static {
        actions.put(BuiltinAmmoActionType.DAMAGE.toString(), MeleeDamageAction.class);
    }

    public static void registerAction(String actionType, Class<? extends AbstractMeleeAction> actionClass) {
        actions.remove(actionType);
        actions.put(actionType, actionClass);
    }

    public static void unregisterAction(String actionType) {
        actions.remove(actionType);
    }

    public static AbstractMeleeAction getAction(GunshellMelee melee, String actionType) {
        Class<? extends AbstractMeleeAction> actionClass = actions.get(actionType);
        if (actionClass == null) actionClass = MeleeDamageAction.class;

        try {
            return actionClass.getConstructor(GunshellMelee.class).newInstance(melee);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
