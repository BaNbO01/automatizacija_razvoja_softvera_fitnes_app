/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;
import rs.ac.bg.fon.nst.fitnes.domain.PlanTreninga;
import rs.ac.bg.fon.nst.fitnes.dto.PlanTreningaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.PlanTreningaResponse;

@Mapper(componentModel = "spring", uses = {PlanVezbeMapper.class})
public interface PlanTreningaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vezbac", ignore = true)
    @Mapping(target = "datum", ignore = true) 
    @Mapping(target = "planoviVezbi", ignore = true) 
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PlanTreninga toPlanTreninga(PlanTreningaRequest request);

    @Mapping(target = "planoviVezbi", source = "planoviVezbi")
    PlanTreningaResponse toPlanTreningaResponse(PlanTreninga planTreninga);

    List<PlanTreningaResponse> toPlanTreningaResponseList(List<PlanTreninga> planoviTreninga);
}
