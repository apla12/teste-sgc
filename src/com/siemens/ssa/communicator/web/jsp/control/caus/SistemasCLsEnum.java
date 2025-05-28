package com.siemens.ssa.communicator.web.jsp.control.caus;

public enum SistemasCLsEnum {
	
    DAIN("CL047"),
    EXPCAU("CL393"),
    TRACAU("CL195"),
    DSSCAU("CL393"),
    NRCAU("CL393");

    private String codigo;

    SistemasCLsEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static String obterCodigoPeloNome(String nome) {
        for (SistemasCLsEnum sistema : SistemasCLsEnum.values()) {
            if (sistema.name().equalsIgnoreCase(nome)) {
                return sistema.getCodigo();
            }
        }
        return null;
    }

}
