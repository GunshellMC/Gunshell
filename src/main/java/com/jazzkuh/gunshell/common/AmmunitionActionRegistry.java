package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.api.enums.BuiltinActionType;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.common.actions.DamageAction;
import com.jazzkuh.gunshell.common.actions.DemoMenuAction;
import com.jazzkuh.gunshell.common.actions.EndCreditsAction;

import java.util.HashMap;
import java.util.Map;

public class AmmunitionActionRegistry {
    private static Map<String, Class<? extends AbstractAmmunitionAction>> actions = new HashMap<>();

    static {
        actions.put(BuiltinActionType.DAMAGE.toString(), DamageAction.class);
        actions.put(BuiltinActionType.END_CREDITS.toString(), EndCreditsAction.class);
        actions.put(BuiltinActionType.DEMO_MENU.toString(), DemoMenuAction.class);
    }

    public static void registerAction(String actionType, Class<? extends AbstractAmmunitionAction> actionClass) {
        actions.put(actionType, actionClass);
    }

    public static AbstractAmmunitionAction getAction(GunshellFireable fireable, GunshellAmmunition ammunition, String actionType) {
        Class<? extends AbstractAmmunitionAction> actionClass = actions.get(actionType);
        if (actionClass == null) return null;

        try {
            return actionClass.getConstructor(GunshellFireable.class, GunshellAmmunition.class).newInstance(fireable, ammunition);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
