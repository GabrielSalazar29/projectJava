package com.agendacompromissos.V1.service;

import com.agendacompromissos.V1.dto.SolicitacaoAmizadeDTO;
import com.agendacompromissos.V1.dto.UsuarioSummaryDTO;
import com.agendacompromissos.V1.model.Amizade;
import com.agendacompromissos.V1.model.Usuario;
import com.agendacompromissos.V1.model.enums.StatusAmizade;
import com.agendacompromissos.V1.repository.AmizadeRepository;
import com.agendacompromissos.V1.repository.UsuarioRepository;
import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // Importar Optional
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AmizadeService {

    private static final Logger logger = LoggerFactory.getLogger(AmizadeService.class); // Adicionar logger

    private final AmizadeRepository amizadeRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AmizadeService(AmizadeRepository amizadeRepository, UsuarioRepository usuarioRepository) {
        this.amizadeRepository = amizadeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private Usuario findUsuarioByIdOrThrow(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + usuarioId));
    }
    
    // ---- MÉTODO ATUALIZADO AQUI ----
    @Transactional
    public SolicitacaoAmizadeDTO enviarSolicitacaoAmizade(Long solicitanteId, Long solicitadoId) {
        if (solicitanteId.equals(solicitadoId)) {
            throw new IllegalArgumentException("Usuário não pode enviar solicitação para si mesmo.");
        }

        Usuario solicitante = findUsuarioByIdOrThrow(solicitanteId);
        Usuario solicitado = findUsuarioByIdOrThrow(solicitadoId);

        Optional<Amizade> amizadeExistenteOpt = amizadeRepository.findAmizadeEntreUsuarios(solicitante, solicitado);

        if (amizadeExistenteOpt.isPresent()) {
            Amizade amizadeExistente = amizadeExistenteOpt.get();
            // Verifica o status da relação existente
            switch (amizadeExistente.getStatus()) {
                case ACEITO:
                    throw new IllegalStateException("Vocês já são amigos.");
                case PENDENTE:
                    // Verifica se a solicitação pendente é na mesma direção. Se for, informa que já foi enviada.
                    if (amizadeExistente.getSolicitante().equals(solicitante)) {
                        throw new IllegalStateException("Você já enviou uma solicitação para este usuário.");
                    } else {
                        throw new IllegalStateException("Este usuário já enviou uma solicitação para você. Verifique suas solicitações recebidas.");
                    }
                case RECUSADO:
                    // Se a relação foi recusada, permite enviar uma nova solicitação.
                    // Reativa a relação existente, atualizando o solicitante e o status.
                    logger.info("Reativando uma solicitação de amizade anteriormente recusada entre {} e {}.", solicitante.getUsername(), solicitado.getUsername());
                    amizadeExistente.setSolicitante(solicitante); // Garante que o solicitante atual é o correto
                    amizadeExistente.setSolicitado(solicitado);
                    amizadeExistente.setStatus(StatusAmizade.PENDENTE);
                    Amizade amizadeReativada = amizadeRepository.save(amizadeExistente);
                    return mapToSolicitacaoAmizadeDTO(amizadeReativada);
                default:
                    // Para outros status como BLOQUEADO, etc.
                    throw new IllegalStateException("Não é possível enviar uma solicitação para este usuário no momento.");
            }
        } else {
            // Se não existe nenhuma relação, cria uma nova.
            logger.info("Criando nova solicitação de amizade de {} para {}.", solicitante.getUsername(), solicitado.getUsername());
            Amizade novaAmizade = new Amizade(solicitante, solicitado);
            novaAmizade = amizadeRepository.save(novaAmizade);
            return mapToSolicitacaoAmizadeDTO(novaAmizade);
        }
    }

    @Transactional
    public SolicitacaoAmizadeDTO aceitarSolicitacaoAmizade(Long amizadeId, Long usuarioLogadoId) {
        Usuario usuarioLogado = findUsuarioByIdOrThrow(usuarioLogadoId);
        Amizade amizade = amizadeRepository.findByIdAndSolicitado(amizadeId, usuarioLogado)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de amizade não encontrada ou não direcionada a este usuário. ID: " + amizadeId));
        if (amizade.getStatus() != StatusAmizade.PENDENTE) {
            throw new IllegalStateException("Esta solicitação não está mais pendente.");
        }
        amizade.setStatus(StatusAmizade.ACEITO);
        amizade = amizadeRepository.save(amizade);
        return mapToSolicitacaoAmizadeDTO(amizade);
    }

    @Transactional
    public SolicitacaoAmizadeDTO rejeitarSolicitacaoAmizade(Long amizadeId, Long usuarioLogadoId) {
        Usuario usuarioLogado = findUsuarioByIdOrThrow(usuarioLogadoId);
        Amizade amizade = amizadeRepository.findByIdAndSolicitado(amizadeId, usuarioLogado)
                .orElseThrow(() -> new EntityNotFoundException("Solicitação de amizade não encontrada ou não direcionada a este usuário. ID: " + amizadeId));
        if (amizade.getStatus() != StatusAmizade.PENDENTE) {
            throw new IllegalStateException("Esta solicitação não está mais pendente.");
        }
        amizade.setStatus(StatusAmizade.RECUSADO);
        amizade = amizadeRepository.save(amizade);
        return mapToSolicitacaoAmizadeDTO(amizade);
    }

    @Transactional
    public void desfazerAmizade(Long usuarioLogadoId, Long amigoId) {
        Usuario usuarioLogado = findUsuarioByIdOrThrow(usuarioLogadoId);
        Usuario amigo = findUsuarioByIdOrThrow(amigoId);
        Amizade amizade = amizadeRepository.findAmizadeAceitaEntreUsuarios(usuarioLogado, amigo, StatusAmizade.ACEITO)
                .orElseThrow(() -> new EntityNotFoundException("Amizade não encontrada entre os usuários."));
        amizadeRepository.delete(amizade);
    }
    
    @Transactional(readOnly = true)
    public List<SolicitacaoAmizadeDTO> listarSolicitacoesPendentesRecebidas(Long usuarioId) {
        Usuario usuario = findUsuarioByIdOrThrow(usuarioId);
        return amizadeRepository.findBySolicitadoAndStatus(usuario, StatusAmizade.PENDENTE)
                .stream()
                .map(this::mapToSolicitacaoAmizadeDTO)
                .collect(Collectors.toList());
    }
        
    @Transactional(readOnly = true)
    public List<SolicitacaoAmizadeDTO> listarSolicitacoesPendentesEnviadas(Long usuarioId) {
        Usuario usuario = findUsuarioByIdOrThrow(usuarioId);
        return amizadeRepository.findBySolicitanteAndStatus(usuario, StatusAmizade.PENDENTE)
                .stream()
                .map(this::mapToSolicitacaoAmizadeDTO)
                .collect(Collectors.toList());
    }

    private SolicitacaoAmizadeDTO mapToSolicitacaoAmizadeDTO(Amizade amizade) {
        UsuarioSummaryDTO solicitanteDto = new UsuarioSummaryDTO(amizade.getSolicitante().getId(), amizade.getSolicitante().getUsername());
        UsuarioSummaryDTO solicitadoDto = new UsuarioSummaryDTO(amizade.getSolicitado().getId(), amizade.getSolicitado().getUsername());
        return new SolicitacaoAmizadeDTO(amizade.getId(), solicitanteDto, solicitadoDto, amizade.getStatus(), amizade.getDataSolicitacao());
    }

    @Transactional(readOnly = true)
    public List<UsuarioSummaryDTO> listarAmigos(Long usuarioId) {
        Usuario usuario = findUsuarioByIdOrThrow(usuarioId);
        
        // 1. Usa a nova query simples para buscar as entidades de amizade
        List<Amizade> amizadesAceitas = amizadeRepository.findAllAmizadesByUsuarioAndStatus(usuario, StatusAmizade.ACEITO);

        // 2. Processa a lista em Java para extrair os amigos
        return amizadesAceitas.stream()
                .map(amizade -> {
                    // Se o solicitante for o usuário logado, o amigo é o solicitado.
                    // Caso contrário, o amigo é o solicitante.
                    if (amizade.getSolicitante().getId().equals(usuarioId)) {
                        return amizade.getSolicitado();
                    } else {
                        return amizade.getSolicitante();
                    }
                })
                // 3. Mapeia a lista de Usuario para UsuarioSummaryDTO
                .map(amigo -> new UsuarioSummaryDTO(amigo.getId(), amigo.getUsername()))
                .collect(Collectors.toList());
    }
}
