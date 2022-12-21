package com.msousa.minhasfinancas.service;

import java.util.Optional;

import org.aspectj.lang.annotation.Before;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.msousa.minhasfinancas.exception.ErrorAltenticacao;
import com.msousa.minhasfinancas.exception.RegraNegocioException;
import com.msousa.minhasfinancas.model.entity.Usuario;
import com.msousa.minhasfinancas.model.repository.UsuarioRepository;
import com.msousa.minhasfinancas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)

@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;
    @MockBean
    UsuarioRepository repoditory;
    @BeforeEach
    
    @Test
    public void deveSalvarUmUsuario(){
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario=Usuario.builder().nome("nome").email("email@email.com").senha("senha").id(1l).build();
        Mockito.when(repoditory.save(Mockito.any(Usuario.class))).thenReturn(usuario);
        Usuario usuariosalvo =service.salvaUsuario(new Usuario());
        Assertions.assertThat(usuariosalvo).isNotNull();
        Assertions.assertThat(usuariosalvo.getId()).isEqualTo(1l);
        Assertions.assertThat(usuariosalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuariosalvo.getEmail()).isEqualTo("email@email.com");
        Assertions.assertThat(usuariosalvo.getSenha()).isEqualTo("senha");
    });
    }
    @Test
    public void naoDeveSalvarusuaarioComEmailJaCadastrado(){
        org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () ->{
        Usuario usuario = Usuario.builder().email("email@email.com").build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail("email@email.com");
        service.salvaUsuario(usuario);
        Mockito.verify(repoditory, Mockito.never()).save(usuario);
        });
    }
    @Test
    public void deveAutenticarUmUsuarioComSucesso(){
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
        String email ="email@email.com";
        String senha="senha";
        Usuario usuario=Usuario.builder().email(email).senha(senha).id(  1l).build();
        Mockito.when(repoditory.findByEmail(email)).thenReturn(Optional.of(usuario));
        Usuario resul=service.autenticar(email, senha);
        Assertions.assertThat(resul).isNotNull();
        });
    }
    @Test
    public void deveLancarQuandoNaoEncontrarOUsuarioCadastradoComEmailInformado(){
        
            Mockito.when(repoditory.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
         //   service.autenticar("email@email.com", "senha");
            Throwable exception = Assertions.catchThrowable(()-> service.autenticar("email@email.com", "senha"));
        Assertions.assertThat(exception).isInstanceOf(ErrorAltenticacao.class).hasMessage("Usuario nao encontrado!");
      
    }
    @Test
    public void deveLancarErroQuandoSenhaNaoBater(){
        
        Usuario usuario = Usuario.builder().email("email@email.com").senha("senha").build();
        Mockito.when(repoditory.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
        Throwable exception = Assertions.catchThrowable(()-> service.autenticar("email@email.com", "123"));
        Assertions.assertThat(exception).isInstanceOf(ErrorAltenticacao.class).hasMessage("Senha Invalida");
    
    }
    @Test
    public void deveValidarEmail(){
        
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
        
        Mockito.when(repoditory.existsByEmail(Mockito.anyString())).thenReturn(false);   
       
      service.validarEmail("email@email.com");
       //     repoditory.deleteAll();
        });
    }
    @Test
    public void deveLancarErroQuandoExistirEmailCadastrado(){
        org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () ->{
          //  Usuario usuario= Usuario.builder().nome("usuario").email("email@email.com").build();
         //   repoditory.save(usuario);
              Mockito.when(repoditory.existsByEmail(Mockito.anyString())).thenReturn(true);
        service.validarEmail("email@email.com");
        });
    }
}
