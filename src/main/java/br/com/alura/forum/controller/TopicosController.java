package br.com.alura.forum.controller;

import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.modelo.Curso;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class TopicosController {

    //@Autowired - Dispensa a sintaxe de New
    @Autowired
    private TopicoRepository topicoRepository;

    //mostra todas
    @RequestMapping("/topicos")
    public List<TopicoDto>lista(String nomeCurso){
        if(nomeCurso==null){
            List<Topico> topicos =topicoRepository.findAll();
            return TopicoDto.converter(topicos);
        }else{
            List<Topico> topicos = topicoRepository.findByCurso_Nome(nomeCurso);
            return TopicoDto.converter(topicos);
        }



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
 *  @ResponseBody -Por padrão, o Spring considera que o retorno do método é o nome
 * da página que ele deve carregar, mas ao utilizar a anotação @ResponseBody,
 * indicamos que o retorno do método deve ser serializado e devolvido no corpo da resposta.
 *
 * Endpoint - é a URL onde seu serviço pode ser acessado por uma aplicação cliente
 *
 * @RestController - Essa anotação é justamente para dizer que o @controller é um @Rest controller
 *  Por padrão, ele já assume que todo metodo já vai ter o @ResponseBody
 *
 */