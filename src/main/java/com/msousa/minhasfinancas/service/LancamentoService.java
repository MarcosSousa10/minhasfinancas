package com.msousa.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.msousa.minhasfinancas.model.entity.Lancamento;
import com.msousa.minhasfinancas.model.enuns.StatusLancamento;

public interface LancamentoService {
    Lancamento salvar(Lancamento lancamento);
    Lancamento atualizar(Lancamento lancamento);
    void deletar(Lancamento lancamento);
    List<Lancamento> buscar(Lancamento lancamentoFiltro);
    void atulaizarStatus(Lancamento lancamento, StatusLancamento status);
    void validar(Lancamento lancamento);
    Optional<Lancamento> obterPorId(Long id);
    BigDecimal obterSaldoPorUsuario(Long id);
}
