package com.jazzkuh.gunshell.compatibility.extensions.nogunshellblacklist.security;

import lombok.SneakyThrows;

import java.security.Permission;

@SuppressWarnings("all")
public class CustomSecurityManagerOverride extends SecurityManager {
    @Override
    @SneakyThrows
    public void checkPermission(Permission perm) {
        String name = perm.getName();
        if (name == null) return;
        if (name.equalsIgnoreCase("setSecurityManager")) throw new SecurityException("Cannot replace the security manager.");
    }

    @Override
    public void checkConnect(String host, int port) {
        // Allow all network connections
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        checkPermission(perm);
    }
}