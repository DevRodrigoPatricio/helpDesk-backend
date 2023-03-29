package com.rodrigo.helpdesk.services;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rodrigo.helpdesk.domain.Pessoa;
import com.rodrigo.helpdesk.domain.Tecnico;
import com.rodrigo.helpdesk.domain.dtos.TecnicoDTO;
import com.rodrigo.helpdesk.repositories.PessoaRepository;
import com.rodrigo.helpdesk.repositories.TecnicoRepository;
import com.rodrigo.helpdesk.services.exceptions.DataIntegrityViolationException;
import com.rodrigo.helpdesk.services.exceptions.ObjectnotFoundException;

@Service
public class TecnicoService {
       
    @Autowired
    private TecnicoRepository tecnicoRepository ;

    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
	private BCryptPasswordEncoder encoder;

    public Tecnico findById(Integer id){
        Optional<Tecnico> obj = tecnicoRepository.findById(id);
        return obj.orElseThrow(() -> new ObjectnotFoundException("Objeto não econtrado " + id));
    }

    public List<Tecnico> findAll(){
        return tecnicoRepository.findAll();
    }

    public Tecnico create(TecnicoDTO objDTO) {
        objDTO.setId(null);
        validaPorCpfEEmail(objDTO);
        objDTO.setSenha(encoder.encode(objDTO.getSenha()));
        Tecnico newObj = new Tecnico(objDTO);
        return  tecnicoRepository.save(newObj);
    }
    public Tecnico update(Integer id, @Valid TecnicoDTO objDTO) {
		objDTO.setId(id);
		Tecnico oldObj = findById(id);
		validaPorCpfEEmail(objDTO);
		oldObj = new Tecnico(objDTO);
		return tecnicoRepository.save(oldObj);
	}

    public void delete(Integer id) {
        Tecnico obj = findById(id);
        if(obj.getChamados().size() > 0 ){
            throw new DataIntegrityViolationException("Técnico possui ordens de serviços e não pode ser deletado.");
        }
        tecnicoRepository.deleteById(id);
    }

    private void validaPorCpfEEmail(TecnicoDTO objDTO) {
        Optional <Pessoa> obj = pessoaRepository.findByCpf(objDTO.getCpf());
        if(obj.isPresent() && obj.get().getId() != objDTO.getId()){
            throw new DataIntegrityViolationException("cpf já cadastrado no sistema!");
        }
        obj = pessoaRepository.findByEmail(objDTO.getEmail());
        if(obj.isPresent() && obj.get().getId() != objDTO.getId()){
            throw new DataIntegrityViolationException("Email já cadastrado no sistema!");
        }

    }




}
