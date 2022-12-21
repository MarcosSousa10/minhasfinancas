package com.msousa.minhasfinancas.api.resource;

import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msousa.minhasfinancas.api.dto.LancamentoDTO;
import com.msousa.minhasfinancas.exception.RegraNegocioException;
import com.msousa.minhasfinancas.model.entity.Lancamento;
import com.msousa.minhasfinancas.model.entity.Usuario;
import com.msousa.minhasfinancas.model.enuns.StatusLancamento;
import com.msousa.minhasfinancas.model.enuns.TipoLancamento;
import com.msousa.minhasfinancas.service.LancamentoService;
import com.msousa.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {
    private LancamentoService service;
    private UsuarioService usuarioService;

    public LancamentoResource (LancamentoService service){
        this.service = service;
    }   
    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto ){
        try{
            Lancamento entidade = converter(dto);
        entidade = service.salvar(entidade);
        return new ResponseEntity(entidade, HttpStatus.CREATED);
        }catch(RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }       

    }
    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto){
        return service.obterPorId(id).map( entity -> {
            try{
            Lancamento lancamento = converter(dto);
            lancamento.setId(entity.getId());
            service.atualizar(lancamento);
            return ResponseEntity.ok(lancamento);
            }   catch(RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }         
        }).orElseGet(()-> new ResponseEntity("Lancamento não encontrado na base de Dados", HttpStatus.BAD_REQUEST));
    }
    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id){
        return service.obterPorId(id).map(entidade -> {
            service.deletar(entidade);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(()->new ResponseEntity("Lancamento não encontrado na base de Dados", HttpStatus.BAD_REQUEST));
    }

    private Lancamento converter(LancamentoDTO dto){
        Lancamento lancamento = new Lancamento();
        lancamento.setId((dto.getId()));
        lancamento.setDescricao(dto.getDescricao());
        lancamento.setAno(dto.getAno());
        lancamento.setMes(dto.getMes());
        lancamento.setValor(dto.getValor());
        Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
        .orElseThrow( () -> new RegraNegocioException("Usuario não encontrado para o id infromado."));

        lancamento.setUsuario(usuario);
        lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
        lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
        return lancamento;
    }
    
}
