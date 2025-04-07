package services;

import java.util.ArrayList;
import java.util.List;

public class Analysis {
    private final String denumireAnaliza;
    private final double rezultat;
    private final String UM;
    private final String intervalReferinta;
    private final String severitate;

    public Analysis(String denumireAnaliza, double rezultat, String UM, String intervalReferinta, String severitate) {
        this.denumireAnaliza = denumireAnaliza;
        this.rezultat = rezultat;
        this.UM = UM;
        this.intervalReferinta = intervalReferinta;
        this.severitate = severitate;
    }

    /**
     * Returnează o listă cu toate variabilele ca șiruri de caractere.
     *
     * @return Lista cu valorile variabilelor
     */
    public List<String> toList() {
        List<String> values = new ArrayList<>();
        values.add("Denumire Analiza: " + denumireAnaliza);
        values.add("Rezultat: " + rezultat);
        values.add("Unitate de Masura: " + UM);
        values.add("Interval Referinta: " + intervalReferinta);
        values.add("Severitate: " + severitate);
        return values;
    }
}