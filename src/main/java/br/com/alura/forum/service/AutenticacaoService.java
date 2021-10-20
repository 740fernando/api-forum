package br.com.alura.forum.service;

import br.com.alura.forum.modelo.Usuario;
import br.com.alura.forum.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *  UserDetailsService - A classe que implementa essa interface geralmente contém uma lógica para
 *  validar as credenciais de um cliente que está se autenticando.
 *
 *
 */

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = repository.findByEmail(username);
        if(usuario.isPresent()){
            return  usuario.get();
        }
        throw  new UsernameNotFoundException("Dados inválidos");
    }
}
/**
 * A lógica de autenticação, que consulta o usuário no banco de dados, deve implementar a interface UserDetailsService
 */