package com.udfilasystem.entity;

/**
 * Tipos de setor de atendimento suportados pelo sistema.
 * O prefixo é usado para gerar o código da senha (ex: C01, F03, S07).
 */
public enum TipoSetor {

    COORDENACAO("C", "Coordenação"),
    FINANCEIRO("F", "Financeiro"),
    SECRETARIA("S", "Secretaria");

    private final String prefixo;
    private final String descricao;

    TipoSetor(String prefixo, String descricao) {
        this.prefixo  = prefixo;
        this.descricao = descricao;
    }

    public String getPrefixo()   { return prefixo; }
    public String getDescricao() { return descricao; }
}
