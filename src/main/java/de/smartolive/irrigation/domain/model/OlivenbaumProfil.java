package de.smartolive.irrigation.domain.model;

import java.util.Objects;

/**
 * Value Object: Beschreibt die spezifischen Eigenschaften der Olivenbäume in einer Parzelle.
 * Immutable – einmal gesetzt, nicht mehr änderbar.
 */
public class OlivenbaumProfil {

    private final String sorte;              // z. B. "Picual", "Koroneiki"
    private final BodenTyp bodenTyp;         // SANDIG, LEHMIG, etc.
    private final int alterJahre;            // Durchschnittsalter
    private final boolean bioZertifiziert;
    private final double basisWasserbedarfLiterProTag; // Basiswert pro Baum/Tag

    public OlivenbaumProfil(String sorte, BodenTyp bodenTyp, int alterJahre,
                            boolean bioZertifiziert, double basisWasserbedarfLiterProTag) {
        this.sorte = Objects.requireNonNull(sorte, "Sorte darf nicht null sein.");
        this.bodenTyp = Objects.requireNonNull(bodenTyp, "BodenTyp darf nicht null sein.");
        if (alterJahre < 0) throw new IllegalArgumentException("Alter darf nicht negativ sein.");
        if (basisWasserbedarfLiterProTag <= 0) throw new IllegalArgumentException("Wasserbedarf muss positiv sein.");

        this.alterJahre = alterJahre;
        this.bioZertifiziert = bioZertifiziert;
        this.basisWasserbedarfLiterProTag = basisWasserbedarfLiterProTag;
    }

    // Getter
    public String getSorte() { return sorte; }
    public BodenTyp getBodenTyp() { return bodenTyp; }
    public int getAlterJahre() { return alterJahre; }
    public boolean isBioZertifiziert() { return bioZertifiziert; }
    public double getBasisWasserbedarfLiterProTag() { return basisWasserbedarfLiterProTag; }

    // equals/hashCode basierend auf allen Attributen (Value Object!)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OlivenbaumProfil that)) return false;
        return alterJahre == that.alterJahre &&
                Double.compare(that.basisWasserbedarfLiterProTag, basisWasserbedarfLiterProTag) == 0 &&
                bioZertifiziert == that.bioZertifiziert &&
                sorte.equals(that.sorte) &&
                bodenTyp == that.bodenTyp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sorte, bodenTyp, alterJahre, bioZertifiziert, basisWasserbedarfLiterProTag);
    }
}