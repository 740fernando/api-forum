package br.com.alura.forum.controller;

import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

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
    public List<TopicoDto>lista(String nomeCurso){
        if(nomeCurso==null){
            List<Topico> topicos =topicoRepository.findAll();
            return TopicoDto.converter(topicos);
        }else{
            List<Topico> topicos = topicoRepository.findByCurso_Nome(nomeCurso);
            return TopicoDto.converter(topicos);
        }
    }
    //Funcionamento web - informacoes cadastradas pelo usuario sao armazenadas em Json e o spring chama o "JACKSON"
    //para converter em TopicForm
    @PostMapping
     public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm form, UriComponentsBuilder uriBuilder){
        Topico topico= form.converter(cursoRepository);
        topicoRepository.save(topico);// salva novo topico
        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri(); //  não vou passar o caminho completo, o caminho do servidor. Só vou passar o caminho do recurso.
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
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
 */
