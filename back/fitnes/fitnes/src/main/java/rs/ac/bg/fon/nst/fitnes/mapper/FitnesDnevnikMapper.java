/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import java.util.List;
import rs.ac.bg.fon.nst.fitnes.domain.FitnesDnevnik;
import rs.ac.bg.fon.nst.fitnes.dto.FitnesDnevnikRequest;
import rs.ac.bg.fon.nst.fitnes.dto.FitnesDnevnikResponse;

@Mapper(componentModel = "spring", uses = {StavkaDnevnikaMapper.class})
public interface FitnesDnevnikMapper {

    @Mapping(target = "naziv", source = "naslov") 
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vezbac", ignore = true)
    @Mapping(target = "stavkeDnevnika", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FitnesDnevnik toFitnesDnevnik(FitnesDnevnikRequest request);

    @Mapping(target = "naslov", source = "naziv") 
    @Mapping(target = "stavkeDnevnika", source = "stavkeDnevnika")
    FitnesDnevnikResponse toFitnesDnevnikResponse(FitnesDnevnik fitnesDnevnik);

    List<FitnesDnevnikResponse> toFitnesDnevnikResponseList(List<FitnesDnevnik> fitnesDnevnici);
}