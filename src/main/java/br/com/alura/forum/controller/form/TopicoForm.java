package br.com.alura.forum.controller.form;

import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Essas anotações fazem parte do Bean Validation que posso utilizar para fazer a validação.
 * Posso colocar, por exemplo, que a mensagem tem que ter no mínimo 10 caracteres,
 * @NotNull @NotEmpty @Length(min = 10), o título no mínimo 5 caracteres, @NotNull @NotEmpty @Length(min = 5) .
 * Tudo como @NotNull,não nulo, e @NotEmpty, não vazio.
 */
@Data
public class TopicoForm {

    @NotNull @NotEmpty @Length(min=5)
    private String titulo;

    @NotNull @NotEmpty @Length(min=10)
    private String mensagem;

    @NotNull @NotEmpty
    private String nomeCurso;


    public Topico converter(CursoRepository cursoRepository) {
        Curso curso = cursoRepository.findByNome(nomeCurso);
        return new Topico(titulo, mensagem, curso);
    }
}
