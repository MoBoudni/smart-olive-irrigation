package de.smartolive.irrigation.domain.repository;

import de.smartolive.irrigation.domain.model.OlivenParzelle;
import de.smartolive.irrigation.domain.model.ParzellenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OlivenParzelleRepository extends JpaRepository<OlivenParzelle, Long> {
    List<OlivenParzelle> findByStatus(ParzellenStatus status);
    Optional<OlivenParzelle> findByName(String name);
}