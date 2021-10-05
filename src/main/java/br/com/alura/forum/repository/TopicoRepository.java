package br.com.alura.forum.repository;

import br.com.alura.forum.modelo.Topico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *  Pensando nisso, o pessoal do Spring Boot criou uma facilidade. Você não precisa criar uma classe,
 *  e não precisa implementar aqueles métodos que são sempre iguais e repetitivos. Não vamos trabalhar
 *  com o padrão DAO, vamos utilizar outro padrão chamado Repository. No Spring Data, não vamos criar uma classe,
 *  vamos criar uma interface e ela vai herdar de outra interface do Spring Data que já tem alguns métodos prontos
 *  e abstraídos para nós.
 *
 *  temos uma interface e não uma classe, não preciso colocar nenhuma anotação em cima dela.
 *  Normalmente, as classes que são gerenciadas pelo Spring, temos que colocar um @controller, @service,
 *  @Repository, @Component. Esse, por ser interface, não precisa. O Spring já encontra a classe automaticamente.
 */
public interface TopicoRepository extends JpaRepository<Topico, Long> {
    List<Topico> findByCurso_Nome(String nomeCurso);
}
/**
 * Essa interface, eu preciso herdar de alguma interface do Spring data. O Spring data tem algumas interfaces
 * que você pode utilizar na herança. No nosso caso, vamos herdar de uma interface chamada JpaRepository.
 * Quando você herda dessa interface, percebe que ela tem um generics que você tem que passar dois tipos.
 * O primeiro é a entidade com que o JpaRepository vai trabalhar (no nosso caso é Topico). E o segundo é
 * qual o tipo do atributo do ID, da chave primária dessa entidade. No nosso caso, estamos usando o Long.
 *
 * Na verdade, como estou herdando, toda vez que herdo ganho tudo da interface ou da classe que estou herdando.
 * Essa interface JpaRepository já tem vários métodos comuns. Daí que vem a facilidade. Você não precisa
 * implementar esses métodos, porque eles são comuns neste tipo de classe
 */