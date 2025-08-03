/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


import java.util.List;
import rs.ac.bg.fon.nst.fitnes.domain.Vezba;
import rs.ac.bg.fon.nst.fitnes.dto.VezbaRequest;
import rs.ac.bg.fon.nst.fitnes.dto.VezbaResponse;

@Mapper(componentModel = "spring", uses = {KategorijaVezbeMapper.class})
public interface VezbaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slika", ignore = true) 
    @Mapping(target = "videoUrl", ignore = true) 
    @Mapping(target = "grupaMisica", ignore = true) 
    @Mapping(target = "kategorija", ignore = true)
    @Mapping(target = "trener", ignore = true) 
    @Mapping(target = "planoviVezbi", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Vezba toVezba(VezbaRequest request);

   
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slika", ignore = true)
    @Mapping(target = "videoUrl", ignore = true)
    @Mapping(target = "grupaMisica", ignore = true)
    @Mapping(target = "kategorija", ignore = true)
    @Mapping(target = "trener", ignore = true)
    @Mapping(target = "planoviVezbi", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateVezbaFromRequest(VezbaRequest request, @MappingTarget Vezba vezba);

   
    @Mapping(target = "slika", expression = "java(vezba.getSlika() != null ? \"/uploads/\" + vezba.getSlika() : null)")
    @Mapping(target = "videoUrl", expression = "java(vezba.getVideoUrl() != null ? \"/uploads/\" + vezba.getVideoUrl() : null)")
    @Mapping(target = "kategorija", source = "kategorija")
    VezbaResponse toVezbaResponse(Vezba vezba);

    List<VezbaResponse> toVezbaResponseList(List<Vezba> vezbe);
}
