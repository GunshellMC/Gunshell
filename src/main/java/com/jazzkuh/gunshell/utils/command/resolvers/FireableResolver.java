package com.jazzkuh.gunshell.utils.command.resolvers;

import com.jazzkuh.commandlib.common.AnnotationCommandSender;
import com.jazzkuh.commandlib.common.resolvers.CompletionResolver;
import com.jazzkuh.commandlib.common.resolvers.ContextResolver;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;

import java.util.ArrayList;
import java.util.List;

public class FireableResolver implements ContextResolver<GunshellFireable>, CompletionResolver<GunshellFireable> {
    @Override
    public List<String> resolve(AnnotationCommandSender<GunshellFireable> annotationCommandSender, String s) {
        return new ArrayList<>(GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().keySet());
    }

    @Override
    public GunshellFireable resolve(String string) {
        if (string == null) return null;
        if (!GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().containsKey(string)) return null;
        return GunshellPlugin.getInstance().getWeaponRegistry().getWeapons().get(string);
    }
}
