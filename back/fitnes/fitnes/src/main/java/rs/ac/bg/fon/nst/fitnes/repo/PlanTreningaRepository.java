/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.nst.fitnes.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.ac.bg.fon.nst.fitnes.domain.PlanTreninga;
import rs.ac.bg.fon.nst.fitnes.domain.User;

@Repository
public interface PlanTreningaRepository extends JpaRepository<PlanTreninga, Long> {

    
    Page<PlanTreninga> findByVezbac(User vezbac, Pageable pageable);
}
