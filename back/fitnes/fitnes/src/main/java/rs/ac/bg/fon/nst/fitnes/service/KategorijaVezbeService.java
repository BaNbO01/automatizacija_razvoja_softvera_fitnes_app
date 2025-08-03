/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.service;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import rs.ac.bg.fon.nst.fitnes.domain.KategorijaVezbe;
import rs.ac.bg.fon.nst.fitnes.dto.KategorijaVezbeResponse;
import rs.ac.bg.fon.nst.fitnes.mapper.KategorijaVezbeMapper;
import rs.ac.bg.fon.nst.fitnes.repo.KategorijaVezbeRepository;

@Service
public class KategorijaVezbeService {

    private final KategorijaVezbeRepository kategorijaVezbeRepository;
    private final KategorijaVezbeMapper kategorijaVezbeMapper;

    public KategorijaVezbeService(KategorijaVezbeRepository kategorijaVezbeRepository,
                                  KategorijaVezbeMapper kategorijaVezbeMapper) {
        this.kategorijaVezbeRepository = kategorijaVezbeRepository;
        this.kategorijaVezbeMapper = kategorijaVezbeMapper;
    }

   
    @Transactional(readOnly = true)
    public List<KategorijaVezbeResponse> getAllKategorijeVezbe() {
        List<KategorijaVezbe> kategorije = kategorijaVezbeRepository.findAll();
        return kategorijaVezbeMapper.toKategorijaVezbeResponseList(kategorije);
    }
}

