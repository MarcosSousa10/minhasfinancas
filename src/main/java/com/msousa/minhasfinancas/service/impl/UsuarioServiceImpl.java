package com.msousa.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.msousa.minhasfinancas.exception.ErrorAltenticacao;
import com.msousa.minhasfinancas.exception.RegraNegocioException;
import com.msousa.minhasfinancas.model.entity.Usuario;
import com.msousa.minhasfinancas.model.repository.UsuarioRepository;
import com.msousa.minhasfinancas.service.UsuarioService;

import jakarta.transaction.Transactional;
@Service
public class UsuarioServiceImpl implements UsuarioService{
    
    private UsuarioRepository repository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository repository) {
        super();
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
       Optional<Usuario> usuario = repository.findByEmail(email);
       if(!usuario.isPresent()){
        throw new ErrorAltenticacao("Usuario nao encontrado!");
       }
       if(!usuario.get().getSenha().equals(senha)){
        throw new ErrorAltenticacao("Senha Invalida");
       }
       return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvaUsuario(Usuario usuario) {
        validarEmail((usuario.getEmail()));
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
       boolean existe= repository.existsByEmail(email);
       if(existe){
        throw new RegraNegocioException("Já existe um usuario cadastrado com este email");
       }
        
    }
    @Override
    public Optional<Usuario> obterPorId(Long id){
        return repository.findById(id);
    }

}