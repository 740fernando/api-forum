package br.com.alura.forum.config.swagger;

import br.com.alura.forum.modelo.Usuario;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;

@Configuration // Vou colocar o @Configuration em cima da classe, que é aquela anotação do Spring para carregar essa classe de configuração.
public class SwaggerConfigurations {

    @Bean //Esse método tem que estar anotado com o @Bean do Spring, para o Spring saber que estou exportando esse bean, que é o objeto do tipo docket.
    public Docket forumAp(){ //Dentro do método tenho que instanciar esse objeto docket e setar todas as informações que o SpringFox Swagger precisa para configurar nosso projeto.
        return new Docket(DocumentationType.SWAGGER_2)//tipo de documentacao- swagger2
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.alura.forum")) // basePackage- a a partir de qual pacote vai ler o projeto
                .paths(PathSelectors.ant("/**"))//Quais endpoints o swagger vai fazer uma analise
                .build()
                .ignoredParameterTypes(Usuario.class)//Classe Usuario possui atributos relacionados ao login, senha e perfis de acesso, não é recomendado que essas informações sejam expostas na documentação do Swagger
                .globalOperationParameters(Arrays.asList(
                        new ParameterBuilder()
                                .name("Authorization")
                                .description("Header para token JWT")
                                .modelRef(new ModelRef("string"))
                                .parameterType("header")
                                .required(false)
                                .build()));
    }
}
/**
 *  ideia é que existe o método chamado globalOperationParameters, em que conseguimos adicionar
 *  parâmetros globais. Ou seja, é um parâmetro que quero que o Swagger apresente em todos os
 *  endpoints. Esse método recebe uma lista com parâmetros. Eu fiz a chamada para o arrays.aslist,
 *  mas no caso só estamos passando um único parâmetro. Para passar o parâmetro, precisamos dar
 *  new nessa classe, que é um builder, onde vamos construir como vai ser esse parâmetro.
 *
 */