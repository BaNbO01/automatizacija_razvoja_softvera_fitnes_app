/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.mapper;

import org.mapstruct.Mapper;


import java.util.List;
import rs.ac.bg.fon.nst.fitnes.domain.Role;
import rs.ac.bg.fon.nst.fitnes.dto.RoleResponse;

@Mapper(componentModel = "spring")
public interface RolesMapper {

    RoleResponse toRolesResponse(Role roles);

    List<RoleResponse> toRolesResponseList(List<Role> roles);
}
