package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.api.enums.BuiltinAmmoActionType;
import com.jazzkuh.gunshell.api.enums.BuiltinThrowableActionType;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.actions.ammunition.DamageAction;
import com.jazzkuh.gunshell.common.actions.ammunition.DemoMenuAction;
import com.jazzkuh.gunshell.common.actions.ammunition.EndCreditsAction;
import com.jazzkuh.gunshell.common.actions.throwable.ExplosiveAction;

import java.util.HashMap;
import java.util.Map;

public class ThrowableActionRegistry {
    private static Map<String, Class<? extends AbstractThrowableAction>> actions = new HashMap<>();

    static {
        actions.put(BuiltinThrowableActionType.EXPLOSIVE.toString(), ExplosiveAction.class);
    }

    public static void registerAction(String actionType, Class<? extends AbstractThrowableAction> actionClass) {
        actions.put(actionType, actionClass);
    }

    public static AbstractThrowableAction getAction(GunshellThrowable throwable, String actionType) {
        Class<? extends AbstractThrowableAction> actionClass = actions.get(actionType);
        if (actionClass == null) return null;

        try {
            return actionClass.getConstructor(GunshellThrowable.class).newInstance(throwable);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
