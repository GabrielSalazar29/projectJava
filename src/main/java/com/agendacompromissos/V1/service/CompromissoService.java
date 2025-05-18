package com.agendacompromissos.V1.service;

import com.agendacompromissos.V1.model.Compromisso;
import com.agendacompromissos.V1.model.Usuario;
import com.agendacompromissos.V1.repository.CompromissoRepository;
import com.agendacompromissos.V1.repository.UsuarioRepository; // Para buscar o usuário
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Para usuário não encontrado
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Boa prática para métodos de escrita

import java.util.List;
import java.util.Optional;

@Service
public class CompromissoService {

    private final CompromissoRepository compromissoRepository;
    private final UsuarioRepository usuarioRepository; // Adicionado para buscar o usuário

    @Autowired
    public CompromissoService(CompromissoRepository compromissoRepository, UsuarioRepository usuarioRepository) {
        this.compromissoRepository = compromissoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Lista compromissos apenas para o usuário logado
    @Transactional(readOnly = true) // Boa prática para métodos de leitura
    public List<Compromisso> listarPorUsuario(Long usuarioId) {
        return compromissoRepository.findByUsuarioId(usuarioId);
    }

    // Busca um compromisso específico do usuário logado
    @Transactional(readOnly = true)
    public Optional<Compromisso> buscarPorIdEUsuarioId(Long id, Long usuarioId) {
        return compromissoRepository.findByIdAndUsuarioId(id, usuarioId);
    }

    @Transactional
    public Compromisso salvar(Compromisso compromisso, Long usuarioId) {
        // Busca o usuário pelo ID para associá-lo ao compromisso
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com ID: " + usuarioId));
        compromisso.setUsuario(usuario); // Associa o usuário ao compromisso

        // Validações de negócio
        if (compromisso.getDataHoraFim() != null && compromisso.getDataHoraInicio() != null &&
            compromisso.getDataHoraFim().isBefore(compromisso.getDataHoraInicio())) {
            throw new IllegalArgumentException("A data/hora de término não pode ser anterior à data/hora de início.");
        }
        return compromissoRepository.save(compromisso);
    }

    @Transactional
    public void deletarPorIdEUsuarioId(Long id, Long usuarioId) {
        Compromisso compromisso = compromissoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RuntimeException("Compromisso não encontrado ou não pertence ao usuário. ID: " + id));
        compromissoRepository.delete(compromisso);
    }

    @Transactional
    public Compromisso atualizar(Long id, Compromisso compromissoAtualizado, Long usuarioId) {
        // Busca o compromisso existente, garantindo que pertence ao usuário
        Compromisso compromissoExistente = compromissoRepository.findByIdAndUsuarioId(id, usuarioId)
            .orElseThrow(() -> new RuntimeException("Compromisso não encontrado ou não pertence ao usuário. ID: " + id));

        // Atualiza os campos do compromisso existente
        compromissoExistente.setTitulo(compromissoAtualizado.getTitulo());
        compromissoExistente.setDescricao(compromissoAtualizado.getDescricao());
        compromissoExistente.setDataHoraInicio(compromissoAtualizado.getDataHoraInicio());
        compromissoExistente.setDataHoraFim(compromissoAtualizado.getDataHoraFim());
        compromissoExistente.setLocal(compromissoAtualizado.getLocal());
        // O usuário não deve mudar na atualização, já foi verificado

        // Adicione validações de negócio aqui também
        if (compromissoExistente.getDataHoraFim() != null && compromissoExistente.getDataHoraInicio() != null &&
            compromissoExistente.getDataHoraFim().isBefore(compromissoExistente.getDataHoraInicio())) {
            throw new IllegalArgumentException("A data/hora de término não pode ser anterior à data/hora de início.");
        }

        return compromissoRepository.save(compromissoExistente);
    }
}