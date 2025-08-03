/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.domain;


import com.fasterxml.jackson.annotation.JsonBackReference; // Dodato za sprečavanje rekurzije
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="roles")
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name="role")
    private String role;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id") 
    @JsonBackReference 
    private User user; 

    public Role() {
    }

    public Role(String role) {
        this.role = role;
    }

}