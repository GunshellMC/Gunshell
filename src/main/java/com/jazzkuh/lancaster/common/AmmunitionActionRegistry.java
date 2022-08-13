package com.jazzkuh.lancaster.common;

import com.jazzkuh.lancaster.api.enums.BuiltinAmmoActionType;
import com.jazzkuh.lancaster.api.objects.LancasterAmmunition;
import com.jazzkuh.lancaster.api.objects.LancasterFireable;
import com.jazzkuh.lancaster.common.actions.ammunition.DamageAction;
import com.jazzkuh.lancaster.common.actions.ammunition.abstraction.AbstractAmmunitionAction;

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

    public static AbstractAmmunitionAction getAction(LancasterFireable fireable, LancasterAmmunition ammunition, String actionType) {
        Class<? extends AbstractAmmunitionAction> actionClass = actions.get(actionType);
        if (actionClass == null) actionClass = DamageAction.class;

        try {
            return actionClass.getConstructor(LancasterFireable.class, LancasterAmmunition.class).newInstance(fireable, ammunition);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
