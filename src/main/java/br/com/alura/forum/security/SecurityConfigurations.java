package br.com.alura.forum.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


//habilita o modulo de seg na application
@EnableWebSecurity
@Configuration
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {
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