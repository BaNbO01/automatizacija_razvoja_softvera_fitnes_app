/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;
import rs.ac.bg.fon.nst.fitnes.domain.PlanVezbe;
import rs.ac.bg.fon.nst.fitnes.dto.PlanVezbeRequestItem;
import rs.ac.bg.fon.nst.fitnes.dto.PlanVezbeResponse;

@Mapper(componentModel = "spring", uses = {VezbaMapper.class})
public interface PlanVezbeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planTreninga", ignore = true)
    @Mapping(target = "vezba", ignore = true) 
    @Mapping(target = "datum", ignore = true) 
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PlanVezbe toPlanVezbe(PlanVezbeRequestItem requestItem);

    @Mapping(target = "vezba", source = "vezba")
    PlanVezbeResponse toPlanVezbeResponse(PlanVezbe planVezbe);

    List<PlanVezbeResponse> toPlanVezbeResponseList(List<PlanVezbe> planoviVezbi);
}