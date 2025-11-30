package fr.maa.models;

import java.time.LocalDate;

public class VenteParJour {

    private final LocalDate date;
    private final int billetsVendus;

    public VenteParJour(LocalDate date, int billetsVendus) {
        this.date = date;
        this.billetsVendus = billetsVendus;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getBilletsVendus() {
        return billetsVendus;
    }
}
