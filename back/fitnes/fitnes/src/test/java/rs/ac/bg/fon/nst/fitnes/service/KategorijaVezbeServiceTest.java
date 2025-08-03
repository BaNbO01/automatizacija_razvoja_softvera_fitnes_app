/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.ac.bg.fon.nst.fitnes.domain.KategorijaVezbe;
import rs.ac.bg.fon.nst.fitnes.dto.KategorijaVezbeResponse;
import rs.ac.bg.fon.nst.fitnes.mapper.KategorijaVezbeMapper;
import rs.ac.bg.fon.nst.fitnes.repo.KategorijaVezbeRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class KategorijaVezbeServiceTest {

  
    @InjectMocks
    private KategorijaVezbeService kategorijaVezbeService;

   
    @Mock
    private KategorijaVezbeRepository kategorijaVezbeRepository;
    @Mock
    private KategorijaVezbeMapper kategorijaVezbeMapper;

    private List<KategorijaVezbe> mockKategorije;
    private List<KategorijaVezbeResponse> mockResponses;

    @BeforeEach
    void setUp() {
       
        KategorijaVezbe kat1 = new KategorijaVezbe(1L, "Kardio", null, null, null);
        KategorijaVezbe kat2 = new KategorijaVezbe(2L, "Snaga", null, null, null);
        mockKategorije = Arrays.asList(kat1, kat2);

      
        KategorijaVezbeResponse res1 = new KategorijaVezbeResponse(1L, "Kardio");
        KategorijaVezbeResponse res2 = new KategorijaVezbeResponse(2L, "Snaga");
        mockResponses = Arrays.asList(res1, res2);
    }

    
    @Test
    void testGetAllKategorijeVezbe_Success() {
       
        when(kategorijaVezbeRepository.findAll()).thenReturn(mockKategorije);
        
        when(kategorijaVezbeMapper.toKategorijaVezbeResponseList(mockKategorije)).thenReturn(mockResponses);

       
        List<KategorijaVezbeResponse> result = kategorijaVezbeService.getAllKategorijeVezbe();

      
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Kardio", result.get(0).getNaziv());
        assertEquals("Snaga", result.get(1).getNaziv());

       
        verify(kategorijaVezbeRepository, times(1)).findAll();
        verify(kategorijaVezbeMapper, times(1)).toKategorijaVezbeResponseList(mockKategorije);
    }

    
    @Test
    void testGetAllKategorijeVezbe_EmptyList() {
      
        when(kategorijaVezbeRepository.findAll()).thenReturn(Collections.emptyList());
        when(kategorijaVezbeMapper.toKategorijaVezbeResponseList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<KategorijaVezbeResponse> result = kategorijaVezbeService.getAllKategorijeVezbe();

        
        assertNotNull(result);
        assertEquals(0, result.size());

      
        verify(kategorijaVezbeRepository, times(1)).findAll();
        verify(kategorijaVezbeMapper, times(1)).toKategorijaVezbeResponseList(Collections.emptyList());
    }
}
