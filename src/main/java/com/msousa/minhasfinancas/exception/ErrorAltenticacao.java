package com.msousa.minhasfinancas.exception;

public class ErrorAltenticacao extends RuntimeException {
    public ErrorAltenticacao(String mensagem){
        super(mensagem);
    }
}
