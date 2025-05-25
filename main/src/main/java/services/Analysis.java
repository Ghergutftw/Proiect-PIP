package services;

import lombok.Data;


@Data
public class Analysis {
    private final String denumireAnaliza;
    private final double rezultat;
    private final String intervalReferinta;
    private final String severitate;
}