package br.com.alura.forum.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


//habilita o modulo de seg na application
@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {

    //O primeiro, que recebe um authentication manager builder é um método que serve para configurar a parte de autenticação. A parte de controle de acesso, de login, fica nesse método.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }


    // O segundo, que recebe um tal de http security, serve para fazer configurações de autorização. A parte de URLs, quem pode acessar cada url, perfil de acesso.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests() //método que vamos precisar chamar para configurar quais requests vamos autorizar, e como vai ser essa autorização.
                .antMatchers(HttpMethod.GET,"/topicos").permitAll() //antMatchers- Nós vamos falar para ele qual url quero filtrar e o que é para fazer, se é para emitir ou bloquear.
                .antMatchers(HttpMethod.GET,"/topicos/*").permitAll()
                .anyRequest().authenticated() // Qualquer outra requisição tem que estar autenticada
                .and().formLogin(); //Existe esse método que é para falar para o Spring gerar um formulário de autenticação. O Spring já tem um formulário de autenticação e um controller que recebe as requisições desse formulário. Então vou chamar esse método porque quero utilizar esse formulário padrão do Spring.
    }

    //terceiro, que recebe um tal de web security, serve para fazermos configurações de recursos estáticos. São requisições para arquivo CSS, Javascript, imagens, etc. Não é nosso caso, já que estamos desenvolvendo só a parte do backend.
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }
}
/**
 * Eu vou precisar criar uma classe onde todas as configurações de segurança
 * estarão. Vou criar uma nova classe, colocar no pacote security, criado
 * dentro do pacote config, só para colocarmos coisas relacionadas a
 * segurança dentro desse pacote. E vou criar uma classe chamada, por
 * exemplo, securityConfigurations. A ideia é que dentro dessa classe
 * estarão todas as configurações de segurança do nosso projeto
 *
 * É uma classe Java, não tem nada a ver com Spring. Tenho que habilitar
 * a parte do Spring security. Para fazer isso, fazemos na própria classe.
 * Existe uma anotação chamada @EnableWebSecurity. Como essa é uma classe
 * que tem configurações, precisamos colocar a anotação @Configuration.
 * O Spring vai carregar e ler as configurações que estiverem dentro dessa
 * classe.
 *
 * Além disso, vamos ter que herdar essa classe de outra classe do Spring chamada
 * web security configurer adapter. Essa classe tem alguns métodos para fazer as
 * configurações que vamos sobrescrever posteriormente
 *
 * Nós colocamos a dependência do Spring security no projeto, criamos a classe, anotada com @EnableWebSecurity, com @Configuration. Dentro, depois, vamos colocar as configurações de segurança.
 * Por enquanto está vazio, mas só de ter feito isso já habilitamos a parte
 * de segurança. Por padrão, o Spring bloqueia todo acesso à nossa API.
 * Tudo está restrito até que eu faça a configuração e libere o que precisa
 * ser liberado.
 */