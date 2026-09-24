// @author Paulo Pacifico

package com.vetalert.repository;

import com.vetalert.domain.Avaliacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    @Query("""
            select a from Avaliacao a
            where lower(a.farmaco) like lower(concat('%', :farmaco, '%'))
              and (:especie = '' or lower(a.especie) = lower(:especie))
            order by a.criadoEm desc
            """)
    List<Avaliacao> buscar(@Param("farmaco") String farmaco, @Param("especie") String especie);
}
