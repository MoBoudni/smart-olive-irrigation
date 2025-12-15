package de.smartolive.irrigation.domain.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SensorReadingRepositoryTest {

    @Autowired
    private SensorReadingRepository repository;

    @Test
    void shouldCalculateAverageMoistureSince() {
        // Given
        Long parzelleId = 1L;
        LocalDateTime since = LocalDateTime.now().minusDays(1);

        // When
        Optional<Double> average = repository.findAverageMoistureSince(parzelleId, since);

        // Then
        assertThat(average).isPresent();
        assertThat(average.get()).isBetween(0.0, 100.0);
    }
}