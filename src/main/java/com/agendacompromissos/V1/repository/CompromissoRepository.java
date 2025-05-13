package com.agendacompromissos.V1.repository;

import com.agendacompromissos.V1.model.Compromisso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository // Anotação para indicar que é um componente de repositório Spring
public interface CompromissoRepository extends JpaRepository<Compromisso, Long> {
    // JpaRepository já fornece métodos CRUD básicos (save, findById, findAll, deleteById, etc.)

    // Você pode adicionar métodos de consulta personalizados aqui, se necessário:
    List<Compromisso> findByDescricaoContainingIgnoreCase(String descricao);
    List<Compromisso> findByDataHoraInicioBetween(LocalDateTime inicio, LocalDateTime fim);
}
