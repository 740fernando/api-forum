package br.com.alura.forum.config.security;

import br.com.alura.forum.modelo.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {


    @Value("${forum.jwt.expiration}") //Sintaxe para importacao de atributo do application.properties
    private String expiration;

    @Value("${forum.jwt.expiration}") //Sintaxe para importacao de atributo do application.properties
    private String secret;


    public String gerarToken(Authentication authentication) {

        Usuario logado = (Usuario) authentication.getPrincipal(); //Esse authentication tem um método chamado getPrincipal para conseguirmos recuperar o usuário que está logado. Eu vou jogá-lo em uma variável usuário, que vou chamar de logado. Vai dar um erro de compilação, porque o getPrincipal devolve um object, então tenho que fazer um cast para usuário.
        Date hoje = new Date();
        Long experationLong = Long.parseLong(expiration);
        Date dataExpiracao= new Date(hoje.getTime()+experationLong);// converte hoje para milisegegundo e soma com expiration



        return Jwts.builder()
                .setIssuer("API do Fórum da Alura") //  Precisamos acertar alguma coisas. A primeira coisa vai ser o issuer. Quem é que está gerando o token. Vou colocar que foi a API do fórum da Alura, porque aí o cliente consegue identificar quem foi que fez a geração.
                .setSubject(logado.getId().toString()) // vou colocar logado.getId e vou passar o id. Mas o id precisa que seja string.
                .setIssuedAt(hoje) //Também preciso dizer qual foi a data de geração do token.Quando ele foi concedido
                .setExpiration(dataExpiracao) // O token também tem uma data de validação, onde ele vai expirar, igual a sessão tradicional, para não ficar infinito, porque isso seria um risco de segurança. E tenho que passar uma data. Eu poderia pegar a data hoje, somar com trinta minutos, mas esse tempo, para não ficar no código, vou injetar em uma propriedade do application.properties chamada fórum.jwt.expiration= e passei um tempo em milissegundos. Coloquei um dia, só para ficar mais fácil no teste. Na prática, o ideal é que o tempo seja menor.
                .signWith(SignatureAlgorithm.HS256,secret) //signWith-Especificacao JSON WEB TOKEN, um json precisa ser criptografado, determina o algoritmo de criptografia e a senha da aplicacao, utilizado para fazer assinatura e gerar o hash da aplicacao
                .compact();// compacta e transforma em uma string

    }
}

/**
 *
 * Lembra que esse método eu passei como parâmetro authentication? Esse authentication tem um método chamado getPrincipal para conseguirmos recuperar o usuário que está logado. Eu vou jogá-lo em uma variável usuário, que vou chamar de logado. Vai dar um erro de compilação, porque o getPrincipal devolve um object, então tenho que fazer um cast para usuário.
 *
 * No subject, vou colocar logado.getId e vou passar o id. Mas o id precisa que seja string. Também preciso dizer qual foi a data de geração do token. Quando ele foi concedido. E ele trabalha usando a API de datas antigas do Java, então ele está esperando um date. Eu vou criar uma variável ali em cima de date e vou importar do Java.
 *
 *
 */