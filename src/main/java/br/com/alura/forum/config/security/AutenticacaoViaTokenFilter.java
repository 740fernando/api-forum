package br.com.alura.forum.config.security;

import antlr.Token;
import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {

    private TokenService tokenService;
    private UsuarioRepository repository;


    public AutenticacaoViaTokenFilter(TokenService tokenService,UsuarioRepository repository) {
        this.tokenService = tokenService;
        this.repository = repository;
    }

    //No nosso método principal do filter só chamo o autenticar se o token estiver válido. Se não estiver, não vai autenticar, vai seguir o fluxo da requisição e o Spring vai barrar. Agora está tudo implementado.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = recuperarToken(request);

        boolean valido = tokenService.isTokenValido(token);
        if(valido){
            autenticarCliente(token);
        }

        filterChain.doFilter(request,response);

        }
//Na próxima requisição ele vai passar no filter de novo, pegar o token e fazer todo o processo. A autenticação é stateless. Em cada requisição eu reautentico o usuário só para executar aquela requisição.
    private void autenticarCliente(String token) {

        Long idUsuario = tokenService.getIdUsuario(token); // Peguei o id do token
        Usuario usuario = repository.findById(idUsuario).get(); //recuperei o objeto usuário passando o id

        //criei o usernameauthenticationtoken passando o usuário, passando nulo na senha, porque não preciso dela, passando os perfis, e aí por fim chamei a classe do Spring que força a autenticação.
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(usuario, null,usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String recuperarToken(HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if(token == null || token.isEmpty() || !token.startsWith("Bearer")) {
            return null;
        }
        return  token.substring(7,token.length());

    }
}

