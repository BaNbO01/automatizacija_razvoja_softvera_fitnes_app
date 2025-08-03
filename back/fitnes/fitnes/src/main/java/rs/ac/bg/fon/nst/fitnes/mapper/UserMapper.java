/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import rs.ac.bg.fon.nst.fitnes.domain.User;
import rs.ac.bg.fon.nst.fitnes.dto.RegisterRequest;
import rs.ac.bg.fon.nst.fitnes.dto.UserResponse;

@Mapper(componentModel = "spring", uses = {RolesMapper.class}) 
public interface UserMapper {

   
    @Mapping(target = "email", source = "email")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toUser(RegisterRequest request);

    
    @Mapping(target = "datumRegistracije", source = "createdAt")
    @Mapping(target = "email", source = "email") 
    @Mapping(target = "role.role", source = "role.role")
    UserResponse toUserResponse(User user);

  
    List<UserResponse> toUserResponseList(List<User> users);
}