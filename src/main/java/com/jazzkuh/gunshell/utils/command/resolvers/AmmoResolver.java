package com.jazzkuh.gunshell.utils.command.resolvers;

import com.jazzkuh.commandlib.common.AnnotationCommandSender;
import com.jazzkuh.commandlib.common.resolvers.CompletionResolver;
import com.jazzkuh.commandlib.common.resolvers.ContextResolver;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellAmmunition;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;

import java.util.ArrayList;
import java.util.List;

public class AmmoResolver implements ContextResolver<GunshellAmmunition>, CompletionResolver<GunshellAmmunition> {
    @Override
    public List<String> resolve(AnnotationCommandSender<GunshellAmmunition> annotationCommandSender, String s) {
        return new ArrayList<>(GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().keySet());
    }

    @Override
    public GunshellAmmunition resolve(String string) {
        if (string == null) return null;
        if (!GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().containsKey(string)) return null;
        return GunshellPlugin.getInstance().getWeaponRegistry().getAmmunition().get(string);
    }
}
