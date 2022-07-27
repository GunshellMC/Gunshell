package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.api.enums.BuiltinAmmoActionType;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.common.actions.ammunition.DamageAction;
import com.jazzkuh.gunshell.common.actions.ammunition.DemoMenuAction;
import com.jazzkuh.gunshell.common.actions.ammunition.EndCreditsAction;

import java.util.HashMap;
import java.util.Map;

public class AmmunitionActionRegistry {
    private static Map<String, Class<? extends AbstractAmmunitionAction>> actions = new HashMap<>();

    static {
        actions.put(BuiltinAmmoActionType.DAMAGE.toString(), DamageAction.class);
    }

    public static void registerAction(String actionType, Class<? extends AbstractAmmunitionAction> actionClass) {
        actions.remove(actionType);
        actions.put(actionType, actionClass);
    }

    public static void unregisterAction(String actionType) {
        actions.remove(actionType);
    }

    public static AbstractAmmunitionAction getAction(GunshellFireable fireable, GunshellAmmunition ammunition, String actionType) {
        Class<? extends AbstractAmmunitionAction> actionClass = actions.get(actionType);
        if (actionClass == null) actionClass = DamageAction.class;

        try {
            return actionClass.getConstructor(GunshellFireable.class, GunshellAmmunition.class).newInstance(fireable, ammunition);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
