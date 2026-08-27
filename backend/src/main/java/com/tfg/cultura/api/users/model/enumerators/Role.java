package com.tfg.cultura.api.users.model.enumerators;

public enum Role {
	COORDINADOR, SECRETARIO, ENCARGADO, COLABORADOR, SOCIO;

	public String asAuthority() {
		return "ROLE_" + name();
	}

}
