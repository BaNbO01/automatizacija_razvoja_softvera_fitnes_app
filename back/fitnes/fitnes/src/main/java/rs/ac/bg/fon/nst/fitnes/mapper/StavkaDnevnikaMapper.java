/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import rs.ac.bg.fon.nst.fitnes.domain.StavkaDnevnika;
import rs.ac.bg.fon.nst.fitnes.dto.StavkaDnevnikaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.StavkaDnevnikaResponse;

@Mapper(componentModel = "spring")
public interface StavkaDnevnikaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dnevnik", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StavkaDnevnika toStavkaDnevnika(StavkaDnevnikaRequest request);

    StavkaDnevnikaResponse toStavkaDnevnikaResponse(StavkaDnevnika stavkaDnevnika);

    List<StavkaDnevnikaResponse> toStavkaDnevnikaResponseList(List<StavkaDnevnika> stavkeDnevnika);
}
