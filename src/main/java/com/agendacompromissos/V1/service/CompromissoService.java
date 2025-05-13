package com.agendacompromissos.V1.service;

import com.agendacompromissos.V1.model.Compromisso;
import com.agendacompromissos.V1.repository.CompromissoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Anotação para indicar que é um componente de serviço Spring
public class CompromissoService {

    private final CompromissoRepository compromissoRepository;

    @Autowired // Injeção de dependência do repositório
    public CompromissoService(CompromissoRepository compromissoRepository) {
        this.compromissoRepository = compromissoRepository;
    }

    public List<Compromisso> listarTodos() {
        return compromissoRepository.findAll();
    }

    public Optional<Compromisso> buscarPorId(Long id) {
        return compromissoRepository.findById(id);
    }

    public Compromisso salvar(Compromisso compromisso) {
        // Adicione validações de negócios aqui antes de salvar
        // Ex: verificar se dataHoraFim é posterior a dataHoraInicio
        if (compromisso.getDataHoraFim() != null && compromisso.getDataHoraInicio() != null &&
            compromisso.getDataHoraFim().isBefore(compromisso.getDataHoraInicio())) {
            throw new IllegalArgumentException("A data/hora de término não pode ser anterior à data/hora de início.");
        }
        return compromissoRepository.save(compromisso);
    }

    public void deletarPorId(Long id) {
        if (!compromissoRepository.existsById(id)) {
            // Poderia lançar uma exceção personalizada aqui (ex: CompromissoNaoEncontradoException)
            throw new RuntimeException("Compromisso não encontrado com o ID: " + id);
        }
        compromissoRepository.deleteById(id);
    }

    public Compromisso atualizar(Long id, Compromisso compromissoAtualizado) {
        return compromissoRepository.findById(id)
            .map(compromissoExistente -> {
                compromissoExistente.setDescricao(compromissoAtualizado.getDescricao());
                compromissoExistente.setDataHoraInicio(compromissoAtualizado.getDataHoraInicio());
                compromissoExistente.setDataHoraFim(compromissoAtualizado.getDataHoraFim());
                compromissoExistente.setLocal(compromissoAtualizado.getLocal());
                // Adicione validações aqui também
                return compromissoRepository.save(compromissoExistente);
            }).orElseThrow(() -> new RuntimeException("Compromisso não encontrado com o ID: " + id));
            // Ou .orElseGet(() -> { compromissoAtualizado.setId(id); return compromissoRepository.save(compromissoAtualizado); });
            // dependendo da sua lógica de PUT (criar se não existir ou apenas atualizar)
    }
}
