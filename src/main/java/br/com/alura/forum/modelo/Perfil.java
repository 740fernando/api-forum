package br.com.alura.forum.modelo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class Perfil implements GrantedAuthority {



    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String nome;

    @Override
    public String getAuthority() {
        return nome;
    }
}
/**
 * A classe que representa o perfil também precisamos implementar
 * uma interface do Spring. E a interface que vamos implementar é
 * a grantedAuthority. Nela só tem um único método, que é o
 * getAuthority, para devolvermos qual atributo tem o nome do
 * authority, o nome do perfil.
 */