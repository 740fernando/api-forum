package br.com.alura.forum.controller;

import br.com.alura.forum.controller.dto.DetalhesDoTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.transaction.annotation.Transactional;


import javax.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/topicos")//a mesma url vale para o metodo get e para o post
public class TopicosController {


    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private CursoRepository cursoRepository;


    @GetMapping
    @Cacheable(value = "listaDeTopicos")
    public Page<TopicoDto>lista(@RequestParam(required = false) String nomeCurso,
                           @PageableDefault(sort="id",direction = Sort.Direction.DESC,size=10) Pageable paginacao){


        if(nomeCurso==null){
            Page<Topico> topicos =topicoRepository.findAll(paginacao);
            return TopicoDto.converter(topicos);
        }else{
            Page<Topico> topicos = topicoRepository.findByCurso_Nome(nomeCurso,paginacao);
            return TopicoDto.converter(topicos);
        }
    }
    //Funcionamento web - informacoes cadastradas pelo usuario sao armazenadas em Json e o spring chama o "JACKSON"
    //para converter em TopicForm
    @PostMapping
    @Transactional
    @CacheEvict(value="listaDeTopicos", allEntries = true)
     public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder){
        Topico topico= form.converter(cursoRepository);
        topicoRepository.save(topico);// salva novo topico
        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri(); //  não vou passar o caminho completo, o caminho do servidor. Só vou passar o caminho do recurso.
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }
    @GetMapping("/{id}")
    public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id){
        Optional<Topico> topico = topicoRepository.findById(id);
        if(topico.isPresent()) {
            return ResponseEntity.ok(new DetalhesDoTopicoDto(topico.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Transactional
    @CacheEvict(value="listaDeTopicos", allEntries = true)
    public  ResponseEntity<TopicoDto>atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm form ){
        Optional<Topico> optional = topicoRepository.findById(id);
        if(optional.isPresent()) {
            Topico topico = form.atualizar(id,topicoRepository);
            return ResponseEntity.ok(new TopicoDto(topico));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    @CacheEvict(value="listaDeTopicos", allEntries = true)
    public ResponseEntity<?> remover(@PathVariable Long id){
        Optional<Topico> optional = topicoRepository.findById(id);
        if(optional.isPresent()) {
            topicoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
/**
 * O Spring usa uma biblioteca chamada "Jackson".
 * É o Jackson que faz a conversão de Java para JSON.
 * O Spring usa o Jackson disfarçadamente, "por debaixo
 * dos panos". Ele pegou a lista, List<Topico>, que foi
 * devolvida, passou ela para o Jackson. O Jackson converteu
 * para JSON, e ele pegou esse JSON e devolveu como uma string.
 *
 * Dessa maneira, filtramos por parâmetros usando o Spring Data.
 * Você cria um método seguindo o padrão de nomenclatura do Spring Data,
 * findBy e o nome do atributo que você quer filtrar. Se for o atributo de
 * um relacionamento, "Nome do atributo do relacionamento" e, concatenado,
 * o "Nome do atributo", porque ele sabe que é para filtrar pelo relacionamento.
 *
 *  Porém, como você pode observar, deu um erro de compilação. Quando usamos paginação,
 *  o retorno do método findall não é mais um list, porque o list apenas traz a lista com
 *  todos os tópicos, mas quando usamos paginação é interessante sabermos em qual página
 *  estamos no momento, quantas tenho no total, quantos registros tenho. Pense no cliente
 *  da nossa API REST. Ele dispara uma requisição usando paginação, mas recebe só os registros
 *  daquela paginação. Ele pode ficar meio perdido. Esse tipo de informação é muito útil para o
 *  cliente. Por isso o Spring não devolve um list. Ele devolve outra classe chamada page, que tem
 *  um generics para você dizer qual é o tipo de classe com que esse page vai trabalhar
 *
 *   Além de paginação, outra coisa comum nos projetos é que às vezes o cliente quer controlar
 *   m que ordem virão os registros. Além de paginar, quero ordenar por algum atributo específico.
 *   Como nós não definimos nada, no momento está vindo de ordem crescente pelo id, pela chave primária,
 *   conforme vem do banco de dados. Mas na aula de hoje vamos mudar isso, vamos flexibilizar, deixar que
 *   o cliente consiga controlar também qual ordem ele quer, por qual atributo do tópico ele quer ordenar
 *   os registros.
 *  Eu declarei só um atributo, mas preciso alterar a lógica para utilizar o novo parâmetro que
 *  está chegando no método. Como eu faço isso? Na própria classe pageable, o Spring também embutiu
 *  essa questão de ordenação. Se dermos uma olhada no método of, vamos ver que tem várias versões.
 *  Tem um, que estamos utilizando, que só recebe a página e a quantidade de elementos, outro que recebe
 *  um sort, e o terceiro recebe um tal de Direction e um string properties, que são as propriedades que
 *  você quer ordenar. É justamente o que eu quero utilizar. Mas aí tenho que passar mais dois parâmetros,
 *  o Direction, para dizer qual a direção, se é crescente ou decrescente. Eu quero que seja crescente.
 *

 *
 *  DICIONARIO
 *  @Autowired - Dispensa a sintaxe de New
 *
 *  @ResponseBody -Por padrão, o Spring considera que o retorno do método é o nome
 * da página que ele deve carregar, mas ao utilizar a anotação @ResponseBody,
 * indicamos que o retorno do método deve ser serializado e devolvido no corpo da resposta.
 * Indicar ao Spring que os parâmetros enviados no corpo da requisição devem
 * ser atribuídos ao parâmetro do método
 *
 * Endpoint - é a URL onde seu serviço pode ser acessado por uma aplicação cliente
 *
 * @RestController - Essa anotação é justamente para dizer que o @controller é um @Rest controller
 *  Por padrão, ele já assume que todo metodo já vai ter o @ResponseBody
 *
 *@ResponseEntity- para montar uma resposta a ser devolvida ao cliente da API, devemos utilizar a classe ResponseEntity
 *
 * topicoRepository.save(topico)- salva novo topico
 *
 * URI uri - uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
 * não vou passar o caminho completo, o caminho do servidor. Só vou passar o caminho do recurso.
 *
 * form.converter(cursoRepository);//converter cursoRepository
 *
 * @Valid - que é do próprio Bean Validation - para avisar para o Spring: quando você for injetar
 * o TopicoForm, puxando os dados que estão vindo na requisição, rode as validações, @Valid,
 * do Bean Validation.
 *
 * @GetMapping("/{id}") Preciso especificar qual tópico quero detalhar. Vou abrir os parênteses, a URL vai
 * ser "/topicos", que é a que está acima da classe, e preciso receber um {id} que é dinâmico,
 * então fica ("/{id}"). Ou seja, para dizer que parte da minha URL é dinâmica, coloco o "id"
 * entre chaves e dou um nome para o parâmetro dinâmico que chamei de {id}.
 *
 *  @PathVariable -perceberá que o nome do parâmetro do método se chama id (@PathVariable Long id)
 *  e a parte dinâmica da URL se chama id ("/{id}"), então ele vai associar, saberá que é para pegar
 *  o que veio na URL e jogar no parâmetro.
 *
 *  getById(id)- Você passa um id e ele te devolve o objeto tópico que é nossa entidade
 *
 *  @PutMapping. Na verdade, existe uma discussão. Quando vamos atualizar um recurso no modelo
 *  REST existem dois métodos para fazer atualização: o método PUT e o método PATCH. Os dois
 *  tem a ideia de atualização, mas o PUT seria para quando você quer sobrescrever o recurso.
 *
 *  @Transactional, que é para avisar para o Spring que é para commitar a transação no final do método.
 *  Segundo o Spring Data, a ideia é que todo método que tiver uma operação de escrita, ou seja,
 *  "salvar", "alterar" e "excluir", deveríamos colocar o @Transactional
 *
 *  @DeleteMapping (em lógica de exclusão, usamos o método delete() no REST).
 *
 *  Optional - é uma classe que veio na API do Java 8. Tenho que mudar meu retorno
 *  para ser um Optional<topico> (isto é, para ser um Optional de tópico).
 *  Como o próprio nome já diz, o Optional é opcional. Tenho que verificar se nesse Optional
 *  tem um registro. Se não tiver, devolvo "404". Se tiver, eu retorno esse TopicoDto, conforme
 *  estava funcionando antes. Então vou fazer um if. Ou seja, if (topico.isPresent()). Se existe
 *  um registro de fato presente, vou retornar um return new DetalhesDoTopicoDto(topico), passando
 *  como parâmetro topico.
 *
 *  Pageable- Se quisermos fazer paginação, o Spring data já tem um esquema pronto para facilitar nossa vida.
 *  Para fazer paginação, precisamos criar uma variável do tipo pageable, que é uma classe do Spring data que
 *  serve justamente para fazer paginação. Vou criar essa variável, vou chamar de paginação = page.
 *  ] Para criar essa interface pageable, vou precisar importar. Mas cuidado, tem três opções. A que
 *  queremos é a do org.springframework.data. Para criar esse cara, usamos outra classe chamada pageRequest.
 *  Nela, tem um método estático chamado of, em que passamos a página e a quantidade. Com isso, ele cria um
 *  objeto do tipo pageable. E aí o que eu faço com esse objeto pageable que está na minha variável paginação? Se você olhar o
 *   método findall, você vai ver que na verdade existem vários findall. Dentre eles têm um que recebe um
 *  pageable como parâmetro. Então, podemos passar esse paginação como parâmetro para o método, que aí
 *  o Spring data automaticamente vai saber que você quer fazer paginação e vai usar os parâmetros página
 *  e quantidade para saber qual o primeiro registro e quantos registros ele vai carregar para você. Então,
 * não precisamos implementar nada da consulta com paginação. Ele faz isso automaticamente.
 *
 *  Page- Dentro desse page tem a lista com os registros. Além dela, tem essas informações do número de páginas
 *  , qual a página atual, quantos elementos tem no total. Ele já dispara umas consultas para fazer o count
 *  de quantos registros tem no banco sozinho. Ao utilizar o objeto Page, além de devolver os registros,
 *  o Spring também devolve informações sobre a paginação no JSON de resposta , como número total de
 *  registros e páginas.
 *
 *  Sort.Direction.DESC - Indica qual ordencação vai ser adotada Descendente ou Ascendente.
 *
 *   @PageableDefault - Nesse PageableDefault tem alguns parâmetros. Consigo dizer que sort = id,
 *   Direction.DESC. Com isso, ele está dizendo que a paginação default é id de maneira decrescente.
 *   O default é: se não estiver vindo um parâmetro de ordenação.
 *    Inclusive, dá para controlar também o default não só da ordem, mas também da paginação, porque
 *    e se ele não passar nenhum parâmetro? Ele vai trazer todos os registros do banco de dados, porque
 *    a paginação é opcional. Posso dizer que page = 0, size = 10. Se ele não passar os parâmetros de
 *    paginação, traga da primeira página apenas dez registros. Consigo deixar por padrão qual é a paginação,
 *    além da ordenação. É bem poderoso, bem simples, e o seu cliente da API consegue controlar a paginação,
 *    a ordenação, e se ele não mandar os parâmetros a gente consegue controlar qual vai ser o comportamento
 *    padrão.
 *
 *    @Cacheable(value = "listaDeTopicos") - No nosso exemplo, vou colocar na nossa classe tópicos Controller
 *    um método lista em cache. Em cima do método, temos que colocar a anotação @Cacheable, para falar para o
 *    Spring guardar o retorno desse método em cache. Só cuidado na hora de fazer o import, porque existe a
 *    mesma anotação no pacote da JPA. O que vamos utilizar no curso é do org.springframework.
 *    Essa anotação tem um atributo que precisamos preencher. Um chamado value, em que temos que passar uma
 *    string que vai ser o identificador único desse cache. Na nossa aplicação, posso ter vários métodos
 *    anotados com @Cacheable, e o Spring precisa saber como ele vai diferenciar um do outro. Ele faz isso
 *    utilizando o id único. Vou passar um nome, por exemplo, listaDeTopicos. Essa string vai funcionar como
 *    sendo um id desse cache.
 */
