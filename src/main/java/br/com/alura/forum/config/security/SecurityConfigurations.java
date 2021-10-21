package br.com.alura.forum.config.security;

import br.com.alura.forum.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Para habilitar e configurar o controle de autenticação e autorização do projeto,
 * devemos criar uma classe e anotá-la com @Configuration e @EnableWebSecurity
 *
 * Devemos indicar ao Spring Security qual o algoritmo de hashing de senha que utilizaremos na API, chamando o método passwordEncoder(), dentro do método configure(AuthenticationManagerBuilder auth), que está na classe SecurityConfigurations
 */
@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private TokenService tokenService;

    @Override
    @Bean // Esse método devolve o authenticationManager, deste modo, é possivel implementar a injecao de dependencias
    protected AuthenticationManager authenticationManager() throws Exception{
        return super.authenticationManager();
    }



    //O primeiro, que recebe um authentication manager builder é um método que serve para configurar a parte de autenticação. A parte de controle de acesso, de login, fica nesse método.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(autenticacaoService).passwordEncoder(new BCryptPasswordEncoder());
    }


    // O segundo, que recebe um tal de http security, serve para fazer configurações de autorização. A parte de URLs, quem pode acessar cada url, perfil de acesso.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests() //método que vamos precisar chamar para configurar quais requests vamos autorizar, e como vai ser essa autorização.
                .antMatchers(HttpMethod.GET,"/topicos").permitAll() //antMatchers- Nós vamos falar para ele qual url quero filtrar e o que é para fazer, se é para emitir ou bloquear.
                .antMatchers(HttpMethod.GET,"/topicos/*").permitAll()
                .antMatchers(HttpMethod.POST,"/auth").permitAll()
                .anyRequest().authenticated() // Qualquer outra requisição tem que estar autenticada
                .and().csrf().disable() //Csrf é uma abreviação para cross-site request forgery, que é um tipo de ataque hacker que acontece em aplicações web. Como vamos fazer autenticação via token, automaticamente nossa API está livre desse tipo de ataque. Nós vamos desabilitar isso para o Spring security não fazer a validação do token do csrf.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//aviso para o Spring security que no nosso projeto, quando eu fizer autenticação, não é para criar sessão, porque vamos usar token
                .and().addFilterBefore(new AutenticacaoViaTokenFilter(tokenService), UsernamePasswordAuthenticationFilter.class);//No nosso método configure, que tem as URLs, depois que eu configurei que a autenticação é stateless, vou colocar mais uma sentença, o addFilter. Só que não posso chamar isso, porque o Spring internamente já tem o filtro de autenticação. Ele precisa saber qual a ordem dos filtros, quem vem antes. Por isso, tem que ser o método addFilterBefore. Passo para ele quem é o filtro que quero adicionar e antes de quem esse filtro virá. Depois, damos um new AutenticacaoViaTokenFilter(), UsernamePasswordAuthenticationFilter.class. Esse é o token que já tem no Spring por padrão. Vou falar para o nosso filtro rodar antes dele.
    }

    //terceiro, que recebe um tal de web security, serve para fazermos configurações de recursos estáticos. São requisições para arquivo CSS, Javascript, imagens, etc. Não é nosso caso, já que estamos desenvolvendo só a parte do backend.
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    /** utilizado para encriptar a senha
    public static void main(String[] args){
        System.out.println(new BCryptPasswordEncoder().encode("123456"));
    }*/
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
 *
 * Para liberar acesso a algum endpoint da nossa API, devemos chamar o método http.authorizeRequests().antMatchers().permitAll() dentro do método configure(HttpSecurity http), que está na classe SecurityConfigurations
 * O método anyRequest().authenticated() indica ao Spring Security para bloquear todos os endpoints que não foram liberados anteriormente com o método permitAll();
 *
 * Devemos indicar ao Spring Security qual o algoritmo de hashing de senha que utilizaremos na API, chamando o método passwordEncoder(), dentro do método configure(AuthenticationManagerBuilder auth), que está na classe SecurityConfigurations
 */