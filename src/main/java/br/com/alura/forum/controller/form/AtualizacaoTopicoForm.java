package br.com.alura.forum.controller.form;

import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.TopicoRepository;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class AtualizacaoTopicoForm {

    @NotNull @NotEmpty @Length(min = 5)
    private String titulo;

    @NotNull @NotEmpty @Length(min=10)
    private String mensagem;

    public Topico atualizar(Long id, TopicoRepository topicoRepository) {
        Topico topico = topicoRepository.getById(id);
        topico.setTitulo(this.titulo);
        topico.setMensagem(this.mensagem);

        return topico;
    }
}
/**
 * Para atualizar no banco de dados, não precisamos chamar nenhum método do Repository,
 * porque a partir do momento em que carreguei ele do banco de dados pelo id, pela JPA
 * ele já está sendo gerenciado. Qualquer atributo que eu setar, no final do méotodo,
 * o Spring roda dentro de uma transação. Então eu vou carregar o tópico do banco de
 * dados, no final do método ele vai commitar a transação, a JPA vai detectar que foram
 * alterados os atributos e ela vai disparar o update no banco de dados automaticamente,
 * não preciso chamar.

 */