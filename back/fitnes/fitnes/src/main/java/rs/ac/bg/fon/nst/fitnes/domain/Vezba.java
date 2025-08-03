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
@Table(name = "vezbe")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vezba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String naziv;

    @Column(columnDefinition = "TEXT")
    private String opis;

    private String slika; 

    @Column(name = "misici_na_koje_utice", columnDefinition = "TEXT")
    private String misiciNaKojeUtice;

    @Column(columnDefinition = "TEXT")
    private String savet;

    private Integer preporuceniBrojSerija;

    private Integer preporuceniBrojPonavljanja;

    private String videoUrl; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupa_misica_id", nullable = false)
    private GrupaMisica grupaMisica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kategorija_id", nullable = false)
    private KategorijaVezbe kategorija;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trener_id") 
    private User trener;

    @OneToMany(mappedBy = "vezba", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanVezbe> planoviVezbi;

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
