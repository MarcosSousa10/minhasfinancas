package com.msousa.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.msousa.minhasfinancas.model.entity.Usuario;
//@SpringBootTest
@ExtendWith(SpringExtension.class)

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {
    @Autowired
    UsuarioRepository repository;
    @Autowired
    TestEntityManager entityManager;
    @Test
    public void deveVerificarAExistenciaDeUmEmail(){
       //cenario
        Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").build();
        entityManager.persist(usuario);
        //acao
        boolean result = repository.existsByEmail("usuario@email.com");
       //verificacao
        Assertions.assertThat(result).isTrue();
    }
    @Test
    public void deveRetornaFalsoQuandoNaoHouverUsuarioCadastradoComOEmail(){
       //cenario
     //   repository.deleteAll();
       //acao
        boolean result = repository.existsByEmail("usuario@email.com");
      //verificacao
        Assertions.assertThat(result).isFalse();
    }
    @Test
    public void devePersistirUmUsuarioNaBaseDeDados(){
      Usuario usuario = Usuario.builder()
      .nome("usuario").email("usuario@emial.com").senha("senha")
      .build();
      Usuario usuarioSalva = repository.save(usuario);
      Assertions.assertThat(usuarioSalva.getId()).isNotNull();

    }
    @Test
    public void deveBuscarUmUsuarioPorEmail(){
      Usuario usuario = criarusuario();
      entityManager.persist(usuario);

      Optional<Usuario> result= repository.findByEmail("usuario@email.com");
      Assertions.assertThat(result.isPresent()).isTrue();
    }
    @Test
    public void deveRetornaVazioAoBuscarUsuarioPorEmailQuandoNaoExistirNaBase(){
          Optional<Usuario> result= repository.findByEmail("usuario@email.com");
      Assertions.assertThat(result.isPresent()).isFalse();
    }
    public static Usuario criarusuario(){
      return Usuario.builder().nome("usuario").email("usuario@email.com").senha("senha").build();
    }
} 
