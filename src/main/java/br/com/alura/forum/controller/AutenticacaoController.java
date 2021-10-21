package br.com.alura.forum.controller;


import br.com.alura.forum.controller.dto.TokenDto;
import br.com.alura.forum.form.LoginForm;
import br.com.alura.forum.config.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authManager; // Para fazer uma autenticação de maneira programática, manualmente, no Spring security,

    @Autowired
    private TokenService tokenService;


    //recebe a requisição do cliente com o e-mail e a senha, valida no banco de dados se os dados estão corretos, e se estiver gera o token devolvendo dentro do dto.
    @PostMapping
    public ResponseEntity<TokenDto> autenticar(@RequestBody @Valid LoginForm form) {

        UsernamePasswordAuthenticationToken dadosLogin = form.converter();

        try{
            Authentication authentication = authManager.authenticate(dadosLogin); // Spring vai chamar o authentication service
            String token = tokenService.gerarToken(authentication);
            return ResponseEntity.ok(new TokenDto(token,"Bearer"));//Bearer é um dos mecanismos de autenticação utilizados no protocolo HTTP, tal como o Basic e o Digest.
        }catch (AuthenticationException e){
                return ResponseEntity.badRequest().build();
        }
    }
}
/**
 * Em uma API Rest, não é uma boa prática utilizar autenticação com o uso de session;
 * Uma das maneiras de fazer autenticação stateless é utilizando tokens JWT (Json Web Token);
 * Para utilizar JWT na API, devemos adicionar a dependência da biblioteca jjwt no arquivo pom.xml do projeto;
 * Para configurar a autenticação stateless no Spring Security, devemos utilizar o método sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
 * Para disparar manualmente o processo de autenticação no Spring Security, devemos utilizar a classe AuthenticationManager;
 * Para poder injetar o AuthenticationManager no controller, devemos criar um método anotado com @Bean, na classe SecurityConfigurations, que retorna uma chamada ao método super.authenticationManager();
 * Para criar o token JWT, devemos utilizar a classe Jwts;
 * O token tem um período de expiração, que pode ser definida no arquivo application.properties;
 * Para injetar uma propriedade do arquivo application.properties, devemos utilizar a anotação @Value.
 *
 */