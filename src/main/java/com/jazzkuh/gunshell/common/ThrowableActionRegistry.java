package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.api.enums.BuiltinThrowableActionType;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.actions.throwable.DemoMenuThrowableAction;
import com.jazzkuh.gunshell.common.actions.throwable.EndCreditsThrowableAction;
import com.jazzkuh.gunshell.common.actions.throwable.ExplosiveThrowableAction;

import java.util.HashMap;
import java.util.Map;

public class ThrowableActionRegistry {
    private static Map<String, Class<? extends AbstractThrowableAction>> actions = new HashMap<>();

    static {
        actions.put(BuiltinThrowableActionType.EXPLOSIVE.toString(), ExplosiveThrowableAction.class);
        actions.put(BuiltinThrowableActionType.END_CREDITS.toString(), EndCreditsThrowableAction.class);
        actions.put(BuiltinThrowableActionType.DEMO_MENU.toString(), DemoMenuThrowableAction.class);
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
