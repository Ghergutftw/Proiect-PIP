package services;

import java.util.*;

class PrepareResponse {
    public List<Analysis> processResponse(String responseBody) {
        List<Analysis> analyses = new ArrayList<>();

        // Simulare parsare JSON (în practică se poate folosi o bibliotecă precum Jackson sau Gson)
        analyses.add(new Analysis("Hemaglobina", 4.76, "g/dL", "[3.8-5.8]", "e bine"));

        return analyses;
    }
}

class Analysis {
    private String denumireAnaliza;
    private double rezultat;
    private String UM;
    private String intervalReferinta;
    private String severitate;

    public Analysis(String denumireAnaliza, double rezultat, String UM, String intervalReferinta, String severitate) {
        this.denumireAnaliza = denumireAnaliza;
        this.rezultat = rezultat;
        this.UM = UM;
        this.intervalReferinta = intervalReferinta;
        this.severitate = severitate;
    }
    @Override
    public String toString() {
        return "Analysis{" +
                "denumireAnaliza='" + denumireAnaliza + '\'' +
                ", rezultat=" + rezultat +
                ", UM='" + UM + '\'' +
                ", intervalReferinta='" + intervalReferinta + '\'' +
                ", severitate='" + severitate + '\'' +
                '}';
    }
}
