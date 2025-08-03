/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.domain;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fitnes_dnevnici")
@Data 
@NoArgsConstructor
@AllArgsConstructor
public class FitnesDnevnik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    private String naziv;

    @Column(name = "kratak_opis", columnDefinition = "TEXT") 
    private String kratakOpis;

    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "vezbac_id", nullable = false) 
    private User vezbac;

    @OneToMany(mappedBy = "dnevnik", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StavkaDnevnika> stavkeDnevnika;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
