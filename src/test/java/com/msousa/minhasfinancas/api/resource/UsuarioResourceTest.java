package com.msousa.minhasfinancas.api.resource;

import org.apache.tomcat.util.http.parser.MediaType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msousa.minhasfinancas.api.dto.UsuarioDTO;
import com.msousa.minhasfinancas.exception.ErrorAltenticacao;
import com.msousa.minhasfinancas.exception.RegraNegocioException;
import com.msousa.minhasfinancas.model.entity.Usuario;
import com.msousa.minhasfinancas.service.LancamentoService;
import com.msousa.minhasfinancas.service.UsuarioService;

@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class UsuarioResourceTest {
    static final String API = "/api/usuarios";
    static final org.springframework.http.MediaType JSON= org.springframework.http.MediaType.APPLICATION_JSON;
    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService service;
    @MockBean
    LancamentoService lancamentoService;
    @Test
    public void deveAutenticarUmUsuario() throws Exception{
    
    UsuarioDTO dto = UsuarioDTO.builder().email("usuario@email.com").senha("123").build();
    Usuario usuario=Usuario.builder().id(1l).email("usuario@email.com").senha("123").build();
    
    Mockito.when(service.autenticar("usuario@email.com","123")).thenReturn(usuario);
    
    String json= new ObjectMapper().writeValueAsString(dto);

       MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON).contentType(JSON).content(json);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
        .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
        
    }
    @Test
    public void deveRetornaBadRequestAoObterErroDeAutenticacao() throws Exception{
    
    UsuarioDTO dto = UsuarioDTO.builder().email("usuario@email.com").senha("123").build();
    
    
    Mockito.when(service.autenticar("usuario@email.com","123")).thenThrow(ErrorAltenticacao.class);
    
    String json= new ObjectMapper().writeValueAsString(dto);

       MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON).contentType(JSON).content(json);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    @Test
    public void deveCriarUmUsuario() throws Exception{
    
    UsuarioDTO dto = UsuarioDTO.builder().email("usuario@email.com").senha("123").build();
    Usuario usuario=Usuario.builder().id(1l).email("usuario@email.com").senha("123").build();
    
    Mockito.when(service.salvaUsuario(Mockito.any(Usuario.class)) ).thenReturn(usuario);
    
    String json= new ObjectMapper().writeValueAsString(dto);

       MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON).contentType(JSON).content(json);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
        .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
        .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
        
    }

    @Test
    public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception{
    
    UsuarioDTO dto = UsuarioDTO.builder().email("usuario@email.com").senha("123").build();
    Usuario usuario=Usuario.builder().id(1l).email("usuario@email.com").senha("123").build();
    
    Mockito.when(service.salvaUsuario(Mockito.any(Usuario.class)) ).thenThrow(RegraNegocioException.class);
    
    String json= new ObjectMapper().writeValueAsString(dto);

       MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON).contentType(JSON).content(json);
        mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
        
    }




}
