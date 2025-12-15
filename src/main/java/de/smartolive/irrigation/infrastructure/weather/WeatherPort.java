package de.smartolive.irrigation.infrastructure.weather;

import de.smartolive.irrigation.domain.valueobject.Wetterdaten;

public interface WeatherPort {
    Wetterdaten getCurrentSnapshot();
    Wetterdaten getForecast24h();
}