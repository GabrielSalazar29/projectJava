package com.agendacompromissos.V1.repository;

import com.agendacompromissos.V1.model.Compromisso;
import com.agendacompromissos.V1.model.Usuario; // Importe Usuario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // Para o novo método

@Repository
public interface CompromissoRepository extends JpaRepository<Compromisso, Long> {

    // Métodos de consulta personalizados existentes (se houver)
    List<Compromisso> findByDescricaoContainingIgnoreCase(String descricao);
    List<Compromisso> findByDataHoraInicioBetween(LocalDateTime inicio, LocalDateTime fim);

    // Novos métodos para buscar compromissos por usuário
    List<Compromisso> findByUsuario(Usuario usuario);

    List<Compromisso> findByUsuarioId(Long usuarioId);

    // Para buscar um compromisso específico de um usuário específico
    Optional<Compromisso> findByIdAndUsuarioId(Long id, Long usuarioId);
}