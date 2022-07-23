package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.api.enums.ActionType;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.common.actions.DamageAction;

import java.util.HashMap;
import java.util.Map;

public class AmmunitionActionRegistry {
    private static Map<ActionType, Class<? extends AbstractAmmunitionAction>> actions = new HashMap<>();

    static {
        actions.put(ActionType.DAMAGE, DamageAction.class);
    }

    public static void registerAction(ActionType actionType, Class<? extends AbstractAmmunitionAction> actionClass) {
        actions.put(actionType, actionClass);
    }

    public static AbstractAmmunitionAction getAction(GunshellFireable fireable, GunshellAmmunition ammunition, ActionType actionType) {
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
