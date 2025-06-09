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

    // Busca todos os compromissos onde o utilizador (pelo seu ID) está na lista de participantes.
    List<Compromisso> findByParticipantes_Id(Long usuarioId);
    
    // NOVO MÉTODO: Busca um compromisso específico pelo seu ID E pelo ID do seu criador.
    // Isso garante que apenas o criador possa encontrar o compromisso para editar/excluir.
    Optional<Compromisso> findByIdAndCriador_Id(Long id, Long criadorId);
}