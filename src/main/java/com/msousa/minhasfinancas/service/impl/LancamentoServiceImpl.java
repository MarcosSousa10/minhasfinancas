package com.msousa.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import com.msousa.minhasfinancas.exception.RegraNegocioException;
import com.msousa.minhasfinancas.model.entity.Lancamento;
import com.msousa.minhasfinancas.model.enuns.StatusLancamento;
import com.msousa.minhasfinancas.model.enuns.TipoLancamento;
import com.msousa.minhasfinancas.model.repository.LancamentoRepository;
import com.msousa.minhasfinancas.service.LancamentoService;


import org.springframework.transaction.annotation.Transactional;


@Service
public class LancamentoServiceImpl implements LancamentoService {
    private LancamentoRepository repository;

   



    public LancamentoServiceImpl(LancamentoRepository repository){
        this.repository  = repository;
    }

    

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return repository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        repository.delete(lancamento);
        
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {        
        Example example = Example.of(lancamentoFiltro, ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
        return repository.findAll(example);
    }

    @Override
    public void atulaizarStatus(Lancamento lancamento, StatusLancamento status) {
        lancamento.setStatus(status);
        atualizar(lancamento);
        
    }
    @Override
    public void validar(Lancamento lancamento){
        if (lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")){
            throw new RegraNegocioException("informe uma descri????o v??lida");
        }
        if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12 ){
            throw new RegraNegocioException("informe um mes v??lida");
        }
        if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4 ) {
            throw new RegraNegocioException("informe um Ano v??lido");
        }
        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null ){
            throw new RegraNegocioException("informe um Usuario");
        }
        if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1){
            throw new RegraNegocioException("informe um Valor v??lida");
        }
        if (lancamento.getTipo() == null ){
            throw new RegraNegocioException("informe um Tipo de Lan??amento");
        }
    }



    @Override
    public Optional<Lancamento> obterPorId(Long id) {
        return repository.findById(id);
    }



    @Override
    @Transactional(readOnly = true)
    public BigDecimal obterSaldoPorUsuario(Long id) {

        BigDecimal receitas =repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.RECEITA,StatusLancamento.EFETIVADO);
        BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.DESPESA,StatusLancamento.EFETIVADO);
        if(receitas ==null){
            receitas = BigDecimal.ZERO;
        }
        if(despesas == null){
            despesas = BigDecimal.ZERO;
        }
        return receitas.subtract(despesas);
    }   
   
    
}
