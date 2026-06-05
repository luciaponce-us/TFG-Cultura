package com.tfg.cultura.api.users.model.enumerators;

import java.util.List;

public enum Role {
    COORDINADOR,
    SECRETARIO,
    ENCARGADO,
    COLABORADOR,
    SOCIO;

    public String asAuthority() {
        return "ROLE_" + name();
    }

    public static List<Role> getAdminRoles() {
        return List.of(COORDINADOR, SECRETARIO, ENCARGADO, COLABORADOR);
    }
}
