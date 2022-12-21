package com.msousa.minhasfinancas.api.resource;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msousa.minhasfinancas.api.dto.UsuarioDTO;
import com.msousa.minhasfinancas.exception.ErrorAltenticacao;
import com.msousa.minhasfinancas.exception.RegraNegocioException;
import com.msousa.minhasfinancas.model.entity.Usuario;
import com.msousa.minhasfinancas.service.LancamentoService;
import com.msousa.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {
    private final UsuarioService service;
    private final LancamentoService lancamentoService;
    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDTO dto){
        try{
           Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
           return ResponseEntity.ok(usuarioAutenticado);
        }catch(ErrorAltenticacao e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO dto){
        Usuario usuario = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();
    try {
        Usuario usuarioSalvo = service.salvaUsuario(usuario);
        return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
    } catch (RegraNegocioException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    }
    @GetMapping("{id}/saldo")
    public ResponseEntity obterSaldo(@PathVariable("id") Long id){
        Optional<Usuario> usuario = service.obterPorId(id);
       if(!usuario.isPresent()){
        return new ResponseEntity(HttpStatus.NOT_FOUND);
       }
        BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
    return ResponseEntity.ok(saldo);
    }

}
