package br.com.alura.forum.modelo;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//UserDetails= a interface para dizer que essa é a classe que tem detalhes de um usuário
@Data
@Entity
public class Usuario implements UserDetails {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private String email;
	private String senha;

	@ManyToMany(fetch = FetchType.EAGER) //quando eu carregar o usuário já carrego a lista de perfis, porque vou precisar dos perfis de acesso do usuário.
	private List<Perfil> perfis = new ArrayList<>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.perfis;
	}

	@Override
	public String getPassword() { // para saber a senha do usuario, precisamos devolver o atributo que representa a senha
		return this.senha;
	}

	@Override
	public String getUsername() { // utiliza o email como user do login
		return this.email;
	}
// Tem alguns métodos que desenvolvem boolean que é caso você
// faça controle na sua aplicação, da conta do usuário, se a
// conta está bloqueada, se tem data de expiração ou coisas do
// gênero, você devolveria os atributos que representam essas
// informações. No nosso caso não vamos ter esse controle mais fino,
// mais detalhado. Vamos devolver true em todos os métodos.
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
