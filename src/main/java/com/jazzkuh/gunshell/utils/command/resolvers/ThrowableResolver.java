package com.jazzkuh.gunshell.utils.command.resolvers;

import com.jazzkuh.commandlib.common.AnnotationCommandSender;
import com.jazzkuh.commandlib.common.resolvers.CompletionResolver;
import com.jazzkuh.commandlib.common.resolvers.ContextResolver;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;

import java.util.ArrayList;
import java.util.List;

public class ThrowableResolver implements ContextResolver<GunshellThrowable>, CompletionResolver<GunshellThrowable> {
    @Override
    public List<String> resolve(AnnotationCommandSender<GunshellThrowable> annotationCommandSender, String s) {
        return new ArrayList<>(GunshellPlugin.getInstance().getWeaponRegistry().getThrowables().keySet());
    }

    @Override
    public GunshellThrowable resolve(String string) {
        if (string == null) return null;
        if (!GunshellPlugin.getInstance().getWeaponRegistry().getThrowables().containsKey(string)) return null;
        return GunshellPlugin.getInstance().getWeaponRegistry().getThrowables().get(string);
    }
}
