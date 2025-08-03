/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;
import rs.ac.bg.fon.nst.fitnes.domain.GrupaMisica;
import rs.ac.bg.fon.nst.fitnes.dto.GrupaMisicaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.GrupaMisicaResponse;

@Mapper(componentModel = "spring", uses = {VezbaMapper.class})
public interface GrupaMisicaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slika", ignore = true) 
    @Mapping(target = "vezbe", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    GrupaMisica toGrupaMisica(GrupaMisicaRequest request);

    @Mapping(target = "slika", expression = "java(grupaMisica.getSlika() != null ? \"/uploads/\" + grupaMisica.getSlika() : null)")
    @Mapping(target = "vezbe", source = "vezbe")
    GrupaMisicaResponse toGrupaMisicaResponse(GrupaMisica grupaMisica);

    List<GrupaMisicaResponse> toGrupaMisicaResponseList(List<GrupaMisica> grupeMisica);
}

