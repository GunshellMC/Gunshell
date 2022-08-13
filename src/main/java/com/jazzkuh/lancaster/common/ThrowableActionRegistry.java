package com.jazzkuh.lancaster.common;

import com.jazzkuh.lancaster.api.enums.BuiltinThrowableActionType;
import com.jazzkuh.lancaster.api.objects.LancasterThrowable;
import com.jazzkuh.lancaster.common.actions.throwable.ExplosiveThrowableAction;
import com.jazzkuh.lancaster.common.actions.throwable.MolotovThrowableAction;
import com.jazzkuh.lancaster.common.actions.throwable.abstraction.AbstractThrowableAction;

import java.util.HashMap;
import java.util.Map;

public class ThrowableActionRegistry {
    private static Map<String, Class<? extends AbstractThrowableAction>> actions = new HashMap<>();

    static {
        actions.put(BuiltinThrowableActionType.EXPLOSIVE.toString(), ExplosiveThrowableAction.class);
        actions.put(BuiltinThrowableActionType.MOLOTOV.toString(), MolotovThrowableAction.class);
    }

    public static void registerAction(String actionType, Class<? extends AbstractThrowableAction> actionClass) {
        actions.remove(actionType);
        actions.put(actionType, actionClass);
    }

    public static void unregisterAction(String actionType) {
        actions.remove(actionType);
    }

    public static AbstractThrowableAction getAction(LancasterThrowable throwable, String actionType) {
        Class<? extends AbstractThrowableAction> actionClass = actions.get(actionType);
        if (actionClass == null) actionClass = ExplosiveThrowableAction.class;

        try {
            return actionClass.getConstructor(LancasterThrowable.class).newInstance(throwable);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
