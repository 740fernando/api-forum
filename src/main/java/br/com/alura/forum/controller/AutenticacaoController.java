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

    @PostMapping
    public ResponseEntity<?> autenticar(@RequestBody @Valid LoginForm form) {

        UsernamePasswordAuthenticationToken dadosLogin = form.converter();

        try{
            Authentication authentication = authManager.authenticate(dadosLogin); // Spring vai chamar o authentication service
            String token = tokenService.gerarToken(authentication);
            return ResponseEntity.ok(new TokenDto(token,"Bearer"));
        }catch (AuthenticationException e){
                return ResponseEntity.badRequest().build();
        }
    }
}
