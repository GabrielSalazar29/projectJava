package com.agendacompromissos.V1.service;

import com.agendacompromissos.V1.dto.CompromissoCreateDTO;
import com.agendacompromissos.V1.model.Compromisso;
import com.agendacompromissos.V1.model.Usuario;
import com.agendacompromissos.V1.repository.CompromissoRepository;
import com.agendacompromissos.V1.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException; // Importação necessária
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CompromissoService {

    private final UsuarioRepository usuarioRepository;
    private final CompromissoRepository compromissoRepository;

    @Autowired
    public CompromissoService(UsuarioRepository usuarioRepository, CompromissoRepository compromissoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.compromissoRepository = compromissoRepository;
    }

    @Transactional
    public Compromisso salvarCompartilhado(CompromissoCreateDTO dto, Long criadorId) {
        Usuario criador = usuarioRepository.findById(criadorId)
                .orElseThrow(() -> new EntityNotFoundException("Criador não encontrado com ID: " + criadorId));

        Compromisso novoCompromisso = new Compromisso();
        novoCompromisso.setTitulo(dto.getTitulo());
        novoCompromisso.setDescricao(dto.getDescricao());
        novoCompromisso.setDataHoraInicio(dto.getDataHoraInicio());
        novoCompromisso.setDataHoraFim(dto.getDataHoraFim());
        novoCompromisso.setLocal(dto.getLocal());
        novoCompromisso.setCriador(criador);

        Set<Usuario> participantes = new HashSet<>();
        participantes.add(criador);

        if (dto.getAmigoIds() != null && !dto.getAmigoIds().isEmpty()) {
            List<Usuario> amigosConvidados = usuarioRepository.findAllById(dto.getAmigoIds());
            participantes.addAll(amigosConvidados);
        }
        
        novoCompromisso.setParticipantes(participantes);
        return compromissoRepository.save(novoCompromisso);
    }
    
    @Transactional(readOnly = true)
    public List<Compromisso> listarPorUsuario(Long usuarioId) {
        return compromissoRepository.findByParticipantes_Id(usuarioId);
    }

    // ---- MÉTODO ATUALIZAR CORRIGIDO ----
    @Transactional
    public Compromisso atualizar(Long compromissoId, CompromissoCreateDTO dto, Long editorId) {
        // 1. Busca o compromisso pelo seu ID e pelo ID do utilizador que está a tentar editar (que deve ser o criador)
        Compromisso compromissoExistente = compromissoRepository.findByIdAndCriador_Id(compromissoId, editorId)
            .orElseThrow(() -> new AccessDeniedException("Compromisso não encontrado ou você não tem permissão para editá-lo."));

        // 2. Atualiza os campos do compromisso existente com os dados do DTO
        compromissoExistente.setTitulo(dto.getTitulo());
        compromissoExistente.setDescricao(dto.getDescricao());
        compromissoExistente.setDataHoraInicio(dto.getDataHoraInicio());
        compromissoExistente.setDataHoraFim(dto.getDataHoraFim());
        compromissoExistente.setLocal(dto.getLocal());

        // 3. Atualiza a lista de participantes
        Set<Usuario> novosParticipantes = new HashSet<>();
        novosParticipantes.add(compromissoExistente.getCriador()); // O criador está sempre na lista
        if (dto.getAmigoIds() != null && !dto.getAmigoIds().isEmpty()) {
            List<Usuario> amigosConvidados = usuarioRepository.findAllById(dto.getAmigoIds());
            novosParticipantes.addAll(amigosConvidados);
        }
        compromissoExistente.setParticipantes(novosParticipantes);

        return compromissoRepository.save(compromissoExistente);
    }

    // ---- MÉTODO DELETAR CORRIGIDO ----
    @Transactional
    public void deletar(Long compromissoId, Long editorId) {
        // Busca o compromisso para garantir que apenas o criador pode deletar
        Compromisso compromissoParaDeletar = compromissoRepository.findByIdAndCriador_Id(compromissoId, editorId)
            .orElseThrow(() -> new AccessDeniedException("Compromisso não encontrado ou você não tem permissão para excluí-lo."));

        compromissoRepository.delete(compromissoParaDeletar);
    }
}