package com.msousa.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.msousa.minhasfinancas.exception.RegraNegocioException;
import com.msousa.minhasfinancas.model.entity.Lancamento;
import com.msousa.minhasfinancas.model.entity.Usuario;
import com.msousa.minhasfinancas.model.enuns.StatusLancamento;
import com.msousa.minhasfinancas.model.repository.LancamentoRepository;
import com.msousa.minhasfinancas.model.repository.LancamentoRespositoryTest;
import com.msousa.minhasfinancas.service.impl.LancamentoServiceImpl;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class LancamentoServiceTest {
    @SpyBean
    LancamentoServiceImpl service;
    @MockBean
    LancamentoRepository repository;
    @Test
    public void deveSalvarUmLancamento(){
      
        Lancamento lancamentoASalvar = LancamentoRespositoryTest.criarLancamento();
        Mockito.doNothing().when(service).validar(lancamentoASalvar);

        Lancamento lancamentoSalvo = LancamentoRespositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.salvar(lancamentoASalvar);

        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }
    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao(){
        Lancamento lancamentoASalvar = LancamentoRespositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        Assertions.catchThrowableOfType(() -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);        
    }
    @Test
    public void deveAtualizarUmLancamento(){
      
        
        Lancamento lancamentoSalvo = LancamentoRespositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.doNothing().when(service).validar(lancamentoSalvo);
        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

    service.atualizar(lancamentoSalvo);
        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }
    @Test
    public void deveLancarErroAoTentarAtualizarumLancamentoQueAindaNaoFoiSalvo(){
        Lancamento lancamentoASalvar = LancamentoRespositoryTest.criarLancamento();
        
        Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoASalvar), NullPointerException.class);
        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);        
    }
    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = LancamentoRespositoryTest.criarLancamento();
        lancamento.setId(1l);
        service.deletar(lancamento);
        Mockito.verify(repository).delete(lancamento);
    }
    @Test
    public void deveLancarErroAoTentarDeletarumLancamentoQueAindaNaoFoiSalvo(){
        Lancamento lancamento = LancamentoRespositoryTest.criarLancamento();
        Assertions.catchThrowableOfType(() -> service.deletar(lancamento),NullPointerException.class);
        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }
    @Test
    public void deveFiltarLancamento(){
        Lancamento lancamento = LancamentoRespositoryTest.criarLancamento();
        lancamento.setId(1l);

        List<Lancamento> lista = Arrays.asList(lancamento);
        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
        List<Lancamento> resultado = service.buscar(lancamento);
        Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
    }
    @Test 
    public void deveAtualizarOsStatusDeUmLancamento(){
        Lancamento lancamento = LancamentoRespositoryTest.criarLancamento();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
       Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
        service.atulaizarStatus(lancamento, novoStatus);
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(service).atualizar(lancamento);
    }

    @Test
    public void deveObterUmLancamentoPosId(){
        Lancamento lancamento = LancamentoRespositoryTest.criarLancamento();
        lancamento.setId(1l);
        Mockito.when(repository.findById(1l)).thenReturn(Optional.of(lancamento));
        Optional<Lancamento> resultado = service.obterPorId(1l);
        Assertions.assertThat(resultado.isPresent()).isTrue();

    }
    @Test
    public void deveRetornaVazioQuandoOLancamentoNaoExistir(){
        Lancamento lancamento = LancamentoRespositoryTest.criarLancamento();
        lancamento.setId(1l);
        Mockito.when(repository.findById(1l)).thenReturn(Optional.empty());
        Optional<Lancamento> resultado = service.obterPorId(1l);
        Assertions.assertThat(resultado.isPresent()).isFalse();

    }
    @Test 
    public void deveLancarErrosAoValidarUmLancamento(){
        Lancamento lancamento = new Lancamento();
        Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe uma descrição válida");

        lancamento.setDescricao("");
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe uma descrição válida");

        lancamento.setDescricao("Salario");

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe um mes válida");
        
        lancamento.setAno(0);

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe um mes válida");
        
        lancamento.setAno(13);

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe um mes válida");
        
        lancamento.setMes(1);
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe um Ano válido");

        lancamento.setAno(202);
         erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe um Ano válido");

        lancamento.setAno(2020);
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe um Usuario");

        lancamento.setUsuario(new Usuario());

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe um Usuario");
        lancamento.getUsuario().setId(1l);
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe um Valor válida");

        lancamento.setValor(BigDecimal.ZERO);

        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe um Valor válida");
        lancamento.setValor(BigDecimal.valueOf(1));
        erro = Assertions.catchThrowable(() -> service.validar(lancamento));
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("informe um Tipo de Lançamento");

        



    }

}
