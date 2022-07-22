package com.jazzkuh.gunshell.api.objects;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;

import javax.swing.text.html.parser.Entity;
import java.util.Optional;

public class GunshellRayTraceResult {
    private final @Getter Optional<LivingEntity> optionalLivingEntity;
    private final @Getter boolean headshot;

    public GunshellRayTraceResult(Optional<LivingEntity> optionalLivingEntity, boolean headshot) {
        this.optionalLivingEntity = optionalLivingEntity;
        this.headshot = headshot;
    }
}
