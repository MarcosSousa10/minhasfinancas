package com.msousa.minhasfinancas.service;

import java.util.Optional;

import com.msousa.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {
    Usuario autenticar(String email, String senha);
    Usuario salvaUsuario (Usuario usuario);
    void validarEmail(String email);
    Optional<Usuario> obterPorId(Long id);
    Optional<Usuario> obterPorId(Usuario usuario);
}
