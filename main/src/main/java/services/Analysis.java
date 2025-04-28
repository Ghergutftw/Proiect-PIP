package services;

import java.util.ArrayList;
import java.util.List;

public class Analysis {
    private final String denumireAnaliza;
    private final double rezultat;
    private final String intervalReferinta;
    private final String severitate;


    public Analysis(String denumireAnaliza, double rezultat, String intervalReferinta, String severitate) {
        this.denumireAnaliza = denumireAnaliza;
        this.rezultat = rezultat;
        this.intervalReferinta = intervalReferinta;
        this.severitate = severitate;

    }

    public String getDenumireAnaliza() {
        return denumireAnaliza;
    }

    public double getRezultat() {
        return rezultat;
    }

    public String getIntervalReferinta() {
        return intervalReferinta;
    }

    public String getSeveritate() {
        return severitate;
    }
}