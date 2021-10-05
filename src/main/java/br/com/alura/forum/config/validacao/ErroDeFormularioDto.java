package br.com.alura.forum.config.validacao;

import lombok.Data;

@Data
public class ErroDeFormularioDto {

    private final String campo;
    private final String erro;

    public ErroDeFormularioDto(String campo, String erro) {
        this.campo = campo;
        this.erro = erro;
    }
}

