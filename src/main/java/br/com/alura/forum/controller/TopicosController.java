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
        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri(); //  n??o vou passar o caminho completo, o caminho do servidor. S?? vou passar o caminho do recurso.
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
 * ?? o Jackson que faz a convers??o de Java para JSON.
 * O Spring usa o Jackson disfar??adamente, "por debaixo
 * dos panos". Ele pegou a lista, List<Topico>, que foi
 * devolvida, passou ela para o Jackson. O Jackson converteu
 * para JSON, e ele pegou esse JSON e devolveu como uma string.
 *
 * Dessa maneira, filtramos por par??metros usando o Spring Data.
 * Voc?? cria um m??todo seguindo o padr??o de nomenclatura do Spring Data,
 * findBy e o nome do atributo que voc?? quer filtrar. Se for o atributo de
 * um relacionamento, "Nome do atributo do relacionamento" e, concatenado,
 * o "Nome do atributo", porque ele sabe que ?? para filtrar pelo relacionamento.
 *
 *  Por??m, como voc?? pode observar, deu um erro de compila????o. Quando usamos pagina????o,
 *  o retorno do m??todo findall n??o ?? mais um list, porque o list apenas traz a lista com
 *  todos os t??picos, mas quando usamos pagina????o ?? interessante sabermos em qual p??gina
 *  estamos no momento, quantas tenho no total, quantos registros tenho. Pense no cliente
 *  da nossa API REST. Ele dispara uma requisi????o usando pagina????o, mas recebe s?? os registros
 *  daquela pagina????o. Ele pode ficar meio perdido. Esse tipo de informa????o ?? muito ??til para o
 *  cliente. Por isso o Spring n??o devolve um list. Ele devolve outra classe chamada page, que tem
 *  um generics para voc?? dizer qual ?? o tipo de classe com que esse page vai trabalhar
 *
 *   Al??m de pagina????o, outra coisa comum nos projetos ?? que ??s vezes o cliente quer controlar
 *   m que ordem vir??o os registros. Al??m de paginar, quero ordenar por algum atributo espec??fico.
 *   Como n??s n??o definimos nada, no momento est?? vindo de ordem crescente pelo id, pela chave prim??ria,
 *   conforme vem do banco de dados. Mas na aula de hoje vamos mudar isso, vamos flexibilizar, deixar que
 *   o cliente consiga controlar tamb??m qual ordem ele quer, por qual atributo do t??pico ele quer ordenar
 *   os registros.
 *  Eu declarei s?? um atributo, mas preciso alterar a l??gica para utilizar o novo par??metro que
 *  est?? chegando no m??todo. Como eu fa??o isso? Na pr??pria classe pageable, o Spring tamb??m embutiu
 *  essa quest??o de ordena????o. Se dermos uma olhada no m??todo of, vamos ver que tem v??rias vers??es.
 *  Tem um, que estamos utilizando, que s?? recebe a p??gina e a quantidade de elementos, outro que recebe
 *  um sort, e o terceiro recebe um tal de Direction e um string properties, que s??o as propriedades que
 *  voc?? quer ordenar. ?? justamente o que eu quero utilizar. Mas a?? tenho que passar mais dois par??metros,
 *  o Direction, para dizer qual a dire????o, se ?? crescente ou decrescente. Eu quero que seja crescente.
 *

 *
 *  DICIONARIO
 *  @Autowired - Dispensa a sintaxe de New
 *
 *  @ResponseBody -Por padr??o, o Spring considera que o retorno do m??todo ?? o nome
 * da p??gina que ele deve carregar, mas ao utilizar a anota????o @ResponseBody,
 * indicamos que o retorno do m??todo deve ser serializado e devolvido no corpo da resposta.
 * Indicar ao Spring que os par??metros enviados no corpo da requisi????o devem
 * ser atribu??dos ao par??metro do m??todo
 *
 * Endpoint - ?? a URL onde seu servi??o pode ser acessado por uma aplica????o cliente
 *
 * @RestController - Essa anota????o ?? justamente para dizer que o @controller ?? um @Rest controller
 *  Por padr??o, ele j?? assume que todo metodo j?? vai ter o @ResponseBody
 *
 *@ResponseEntity- para montar uma resposta a ser devolvida ao cliente da API, devemos utilizar a classe ResponseEntity
 *
 * topicoRepository.save(topico)- salva novo topico
 *
 * URI uri - uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
 * n??o vou passar o caminho completo, o caminho do servidor. S?? vou passar o caminho do recurso.
 *
 * form.converter(cursoRepository);//converter cursoRepository
 *
 * @Valid - que ?? do pr??prio Bean Validation - para avisar para o Spring: quando voc?? for injetar
 * o TopicoForm, puxando os dados que est??o vindo na requisi????o, rode as valida????es, @Valid,
 * do Bean Validation.
 *
 * @GetMapping("/{id}") Preciso especificar qual t??pico quero detalhar. Vou abrir os par??nteses, a URL vai
 * ser "/topicos", que ?? a que est?? acima da classe, e preciso receber um {id} que ?? din??mico,
 * ent??o fica ("/{id}"). Ou seja, para dizer que parte da minha URL ?? din??mica, coloco o "id"
 * entre chaves e dou um nome para o par??metro din??mico que chamei de {id}.
 *
 *  @PathVariable -perceber?? que o nome do par??metro do m??todo se chama id (@PathVariable Long id)
 *  e a parte din??mica da URL se chama id ("/{id}"), ent??o ele vai associar, saber?? que ?? para pegar
 *  o que veio na URL e jogar no par??metro.
 *
 *  getById(id)- Voc?? passa um id e ele te devolve o objeto t??pico que ?? nossa entidade
 *
 *  @PutMapping. Na verdade, existe uma discuss??o. Quando vamos atualizar um recurso no modelo
 *  REST existem dois m??todos para fazer atualiza????o: o m??todo PUT e o m??todo PATCH. Os dois
 *  tem a ideia de atualiza????o, mas o PUT seria para quando voc?? quer sobrescrever o recurso.
 *
 *  @Transactional, que ?? para avisar para o Spring que ?? para commitar a transa????o no final do m??todo.
 *  Segundo o Spring Data, a ideia ?? que todo m??todo que tiver uma opera????o de escrita, ou seja,
 *  "salvar", "alterar" e "excluir", dever??amos colocar o @Transactional
 *
 *  @DeleteMapping (em l??gica de exclus??o, usamos o m??todo delete() no REST).
 *
 *  Optional - ?? uma classe que veio na API do Java 8. Tenho que mudar meu retorno
 *  para ser um Optional<topico> (isto ??, para ser um Optional de t??pico).
 *  Como o pr??prio nome j?? diz, o Optional ?? opcional. Tenho que verificar se nesse Optional
 *  tem um registro. Se n??o tiver, devolvo "404". Se tiver, eu retorno esse TopicoDto, conforme
 *  estava funcionando antes. Ent??o vou fazer um if. Ou seja, if (topico.isPresent()). Se existe
 *  um registro de fato presente, vou retornar um return new DetalhesDoTopicoDto(topico), passando
 *  como par??metro topico.
 *
 *  Pageable- Se quisermos fazer pagina????o, o Spring data j?? tem um esquema pronto para facilitar nossa vida.
 *  Para fazer pagina????o, precisamos criar uma vari??vel do tipo pageable, que ?? uma classe do Spring data que
 *  serve justamente para fazer pagina????o. Vou criar essa vari??vel, vou chamar de pagina????o = page.
 *  ] Para criar essa interface pageable, vou precisar importar. Mas cuidado, tem tr??s op????es. A que
 *  queremos ?? a do org.springframework.data. Para criar esse cara, usamos outra classe chamada pageRequest.
 *  Nela, tem um m??todo est??tico chamado of, em que passamos a p??gina e a quantidade. Com isso, ele cria um
 *  objeto do tipo pageable. E a?? o que eu fa??o com esse objeto pageable que est?? na minha vari??vel pagina????o? Se voc?? olhar o
 *   m??todo findall, voc?? vai ver que na verdade existem v??rios findall. Dentre eles t??m um que recebe um
 *  pageable como par??metro. Ent??o, podemos passar esse pagina????o como par??metro para o m??todo, que a??
 *  o Spring data automaticamente vai saber que voc?? quer fazer pagina????o e vai usar os par??metros p??gina
 *  e quantidade para saber qual o primeiro registro e quantos registros ele vai carregar para voc??. Ent??o,
 * n??o precisamos implementar nada da consulta com pagina????o. Ele faz isso automaticamente.
 *
 *  Page- Dentro desse page tem a lista com os registros. Al??m dela, tem essas informa????es do n??mero de p??ginas
 *  , qual a p??gina atual, quantos elementos tem no total. Ele j?? dispara umas consultas para fazer o count
 *  de quantos registros tem no banco sozinho. Ao utilizar o objeto Page, al??m de devolver os registros,
 *  o Spring tamb??m devolve informa????es sobre a pagina????o no JSON de resposta , como n??mero total de
 *  registros e p??ginas.
 *
 *  Sort.Direction.DESC - Indica qual ordenca????o vai ser adotada Descendente ou Ascendente.
 *
 *   @PageableDefault - Nesse PageableDefault tem alguns par??metros. Consigo dizer que sort = id,
 *   Direction.DESC. Com isso, ele est?? dizendo que a pagina????o default ?? id de maneira decrescente.
 *   O default ??: se n??o estiver vindo um par??metro de ordena????o.
 *    Inclusive, d?? para controlar tamb??m o default n??o s?? da ordem, mas tamb??m da pagina????o, porque
 *    e se ele n??o passar nenhum par??metro? Ele vai trazer todos os registros do banco de dados, porque
 *    a pagina????o ?? opcional. Posso dizer que page = 0, size = 10. Se ele n??o passar os par??metros de
 *    pagina????o, traga da primeira p??gina apenas dez registros. Consigo deixar por padr??o qual ?? a pagina????o,
 *    al??m da ordena????o. ?? bem poderoso, bem simples, e o seu cliente da API consegue controlar a pagina????o,
 *    a ordena????o, e se ele n??o mandar os par??metros a gente consegue controlar qual vai ser o comportamento
 *    padr??o.
 *
 *    @Cacheable(value = "listaDeTopicos") - No nosso exemplo, vou colocar na nossa classe t??picos Controller
 *    um m??todo lista em cache. Em cima do m??todo, temos que colocar a anota????o @Cacheable, para falar para o
 *    Spring guardar o retorno desse m??todo em cache. S?? cuidado na hora de fazer o import, porque existe a
 *    mesma anota????o no pacote da JPA. O que vamos utilizar no curso ?? do org.springframework.
 *    Essa anota????o tem um atributo que precisamos preencher. Um chamado value, em que temos que passar uma
 *    string que vai ser o identificador ??nico desse cache. Na nossa aplica????o, posso ter v??rios m??todos
 *    anotados com @Cacheable, e o Spring precisa saber como ele vai diferenciar um do outro. Ele faz isso
 *    utilizando o id ??nico. Vou passar um nome, por exemplo, listaDeTopicos. Essa string vai funcionar como
 *    sendo um id desse cache.
 *    Para o Spring invalidar algum cache ap??s um determinado m??todo ser chamado, devemos anot??-lo com @CacheEvict;
 * Devemos utilizar cache apenas para as informa????es que nunca ou raramente s??o atualizadas no banco de dados.
 */
