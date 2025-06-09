package com.agendacompromissos.V1.repository;

import com.agendacompromissos.V1.model.Amizade;
import com.agendacompromissos.V1.model.Usuario;
import com.agendacompromissos.V1.model.enums.StatusAmizade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmizadeRepository extends JpaRepository<Amizade, Long> {

    // ... (outros métodos como findBySolicitanteAndSolicitado, etc. permanecem os mesmos) ...
    Optional<Amizade> findBySolicitanteAndSolicitado(Usuario solicitante, Usuario solicitado);

    @Query("SELECT a FROM Amizade a WHERE (a.solicitante = :user1 AND a.solicitado = :user2) OR (a.solicitante = :user2 AND a.solicitado = :user1)")
    Optional<Amizade> findAmizadeEntreUsuarios(@Param("user1") Usuario user1, @Param("user2") Usuario user2);

    List<Amizade> findBySolicitadoAndStatus(Usuario solicitado, StatusAmizade status);

    List<Amizade> findBySolicitanteAndStatus(Usuario solicitante, StatusAmizade status);

    Optional<Amizade> findByIdAndSolicitado(Long id, Usuario solicitado);
         
    @Query("SELECT a FROM Amizade a WHERE ((a.solicitante = :user1 AND a.solicitado = :user2) OR (a.solicitante = :user2 AND a.solicitado = :user1)) AND a.status = :status")
    Optional<Amizade> findAmizadeAceitaEntreUsuarios(@Param("user1") Usuario user1, @Param("user2") Usuario user2, @Param("status") StatusAmizade status);


    // ---- ALTERAÇÃO AQUI ----
    // Removemos a query com "CASE WHEN" e a substituímos por uma que busca as entidades de amizade.
    @Query("SELECT a FROM Amizade a " +
           "WHERE (a.solicitante = :usuario OR a.solicitado = :usuario) AND a.status = :status")
    List<Amizade> findAllAmizadesByUsuarioAndStatus(@Param("usuario") Usuario usuario, @Param("status") StatusAmizade status);

}