package com.agendacompromissos.V1.repository;

import com.agendacompromissos.V1.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método para buscar um usuário pelo nome de usuário (ou email, se for o caso).
    // Este método é essencial para o Spring Security carregar os detalhes do usuário.
    Optional<Usuario> findByUsername(String username);


    // Novo método para buscar usuários por parte do nome, excluindo o próprio usuário
    List<Usuario> findByUsernameContainingIgnoreCaseAndIdNot(String username, Long id);

    // Método para buscar usuários por parte do nome (geral, pode ser usado se não precisar excluir o logado)
    List<Usuario> findByUsernameContainingIgnoreCase(String username);
}
