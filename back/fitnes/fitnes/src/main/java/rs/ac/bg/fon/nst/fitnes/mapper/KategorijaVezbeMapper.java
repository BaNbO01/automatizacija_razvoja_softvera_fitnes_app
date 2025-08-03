/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import rs.ac.bg.fon.nst.fitnes.domain.KategorijaVezbe;
import rs.ac.bg.fon.nst.fitnes.dto.KategorijaVezbeRequest;
import rs.ac.bg.fon.nst.fitnes.dto.KategorijaVezbeResponse;

@Mapper(componentModel = "spring")
public interface KategorijaVezbeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vezbe", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    KategorijaVezbe toKategorijaVezbe(KategorijaVezbeRequest request);

    KategorijaVezbeResponse toKategorijaVezbeResponse(KategorijaVezbe kategorijaVezbe);

    List<KategorijaVezbeResponse> toKategorijaVezbeResponseList(List<KategorijaVezbe> kategorijeVezbe);
}