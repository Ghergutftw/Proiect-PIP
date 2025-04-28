package services;

import java.util.ArrayList;
import java.util.List;

public class Analysis {
    private final String denumireAnaliza;
    private final double rezultat;
    private final String UM;
    private final String intervalReferinta;
    private final String severitate;

    public List<String> toList() {
        List<String> details = new ArrayList<>();
        details.add(denumireAnaliza);
        details.add(String.valueOf(rezultat));
        details.add(UM);
        details.add(intervalReferinta);
        details.add(severitate);
        return details;
    }

    public Analysis(String denumireAnaliza, double rezultat, String UM, String intervalReferinta, String severitate) {
        this.denumireAnaliza = denumireAnaliza;
        this.rezultat = rezultat;
        this.UM = UM;
        this.intervalReferinta = intervalReferinta;
        this.severitate = severitate;

    }

    public String getDenumireAnaliza() {
        return denumireAnaliza;
    }

    public double getRezultat() {
        return rezultat;
    }

    public String getUM() {
        return UM;
    }

    public String getIntervalReferinta() {
        return intervalReferinta;
    }

    public String getSeveritate() {
        return severitate;
    }
}