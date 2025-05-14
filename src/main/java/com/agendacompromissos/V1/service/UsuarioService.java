package com.agendacompromissos.V1.service;

import com.agendacompromissos.V1.model.Usuario;
import com.agendacompromissos.V1.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Remova importações de Collections, HashSet, Set se eram apenas para roles

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarNovoUsuario(Usuario usuario) throws Exception {
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new Exception("Nome de usuário já existe: " + usuario.getUsername());
        }
        // Hash da senha antes de salvar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Garanta que o usuário esteja habilitado
        usuario.setEnabled(true);

        return usuarioRepository.save(usuario);
    }
}
