package com.msousa.minhasfinancas.service;

import java.util.List;

import com.msousa.minhasfinancas.model.entity.Lancamento;
import com.msousa.minhasfinancas.model.enuns.StatusLancamento;

public interface LancamentoService {
    Lancamento salvar(Lancamento lancamento);
    Lancamento atualizar(Lancamento lancamento);
    void deletar(Lancamento lancamento);
    List<Lancamento> buscar(Lancamento lancamentoFiltro);
    void atulaizarStatus(Lancamento lancamento, StatusLancamento status);
    void validar(Lancamento lancamento);
}
