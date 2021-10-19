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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

/**
 * O problema é que em todos os métodos a URL vai se repetir. Se um dia eu quiser alterar,
 * vou ter que alterar em todos os métodos. Podemos tirar a anotação @RequestMapping(Value=
 * "/topicos", method = RequestMethod.GET) de cima do método e colocar em cima da classe.
 * Mas aí, na classe, não vou colocar o método, vou colocar só a URL. Então, é como se
 * disséssemos ao Spring: o TopicosController responde às aquisições que começam com "/
 *
 * E aí, no método cadastrar eu faço a mesma coisa. Só que aí é POST ao invés de GET, porque estou
 * postando uma informação, fazendo um cadastro. Dessa maneira, não teria mais conflito. O Spring
 * sabe que a URL é a mesma, mas os métodos são diferentes.
 */

@RestController
@RequestMapping("/topicos")//a mesma url vale para o metodo get e para o post
public class TopicosController {


    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private CursoRepository cursoRepository;


    @GetMapping
    public Page<TopicoDto>lista(@RequestParam(required = false) String nomeCurso,
                                @RequestParam int pagina, int qtd){

        //para criar paginação, necessario criar um objeto do tipo Pagiable, o pagiable é um interface, então é
        //necesario instanciar com PageRequest,- Nela, tem um método estático chamado of, em que passamos a página e a quantidade. Com isso, ele cria um objeto do tipo pageable.
        Pageable paginacao = PageRequest.of(pagina,qtd);

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
 *  de quantos registros tem no banco sozinho
 */
