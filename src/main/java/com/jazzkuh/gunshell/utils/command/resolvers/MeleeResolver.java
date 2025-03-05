package com.jazzkuh.gunshell.utils.command.resolvers;

import com.jazzkuh.commandlib.common.AnnotationCommandSender;
import com.jazzkuh.commandlib.common.resolvers.CompletionResolver;
import com.jazzkuh.commandlib.common.resolvers.ContextResolver;
import com.jazzkuh.gunshell.GunshellPlugin;
import com.jazzkuh.gunshell.api.objects.GunshellFireable;
import com.jazzkuh.gunshell.api.objects.GunshellMelee;

import java.util.ArrayList;
import java.util.List;

public class MeleeResolver implements ContextResolver<GunshellMelee>, CompletionResolver<GunshellMelee> {
    @Override
    public List<String> resolve(AnnotationCommandSender<GunshellMelee> annotationCommandSender, String s) {
        return new ArrayList<>(GunshellPlugin.getInstance().getWeaponRegistry().getMelees().keySet());
    }

    @Override
    public GunshellMelee resolve(String string) {
        if (string == null) return null;
        if (!GunshellPlugin.getInstance().getWeaponRegistry().getMelees().containsKey(string)) return null;
        return GunshellPlugin.getInstance().getWeaponRegistry().getMelees().get(string);
    }
}
