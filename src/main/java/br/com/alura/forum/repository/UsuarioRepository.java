package br.com.alura.forum.repository;

import br.com.alura.forum.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long> {

    Optional<Usuario> findByEmail(String email); //Se veio um usuário, é porque está certo o e-mail que você digitou. Simplesmente devolvo o usuário. Se não vier o usuário, preciso dizer para o Spring que o usuário não existe. Lembre-se que dá para fazer um if desse optional. Se estiver presente, devolve. Senão, jogo uma exception para avisar o Spring que o usuário não foi encontrado.
}
