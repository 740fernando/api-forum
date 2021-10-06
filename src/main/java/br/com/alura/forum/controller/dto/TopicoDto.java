package br.com.alura.forum.controller.dto;

import br.com.alura.forum.modelo.Topico;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * não é uma boa prática retornar entidades JPA nos métodos dos controllers, sendo mais indicado
 * retornar classes que seguem o padrão DTO (Data Transfer Object);
 */
//Consigo controlar quais campos quero devolver, porque nem sempre eu quero devolver tudo que tem na
//minha classe de domínio.
    // quando eu estiver falando de TopicoDto, são dados que saem da API de volta para o cliente.
@Data
public class TopicoDto {

    private Long id;
    private String titulo;
    private String mensagem;
    private LocalDateTime dataCriacao;

    public TopicoDto(Topico topico){
        this.id=topico.getId();
        this.titulo=topico.getTitulo();
        this.mensagem=topico.getMensagem();
        this.dataCriacao=topico.getDataCriacao();
    }


    public static List<TopicoDto> converter(List<Topico> topicos) {
        return topicos.stream().map(TopicoDto::new).collect(Collectors.toList());
    }
}
/**
 * .stream().map(TopicoDto::new).collect(Collectors.toList())->Essa é a sintaxe do Java 8. Sem ele, teríamos que
 * pegar essa lista de tópicos, fazer um for para cada tópico, dar new no topicoDto, guardar em uma lista de
 * topicoDto e devolver essa lista de topicoDto no final. Desse jeito, ele faz tudo isso em uma linha só usando
 * API de strings do Java 8.
 *A função do mapeamento será TopicoDto::new, porque ele vai chamar o construtor que recebe o próprio tópico
 * como parâmetro. No final, tenho que transformar isso em uma lista, então vou encadear a chamada para o método
 * collect(), passando collectors.toList() para transformar numa lista.
 */