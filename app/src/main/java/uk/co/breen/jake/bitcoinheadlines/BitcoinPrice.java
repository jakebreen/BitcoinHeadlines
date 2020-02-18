package uk.co.breen.jake.bitcoinheadlines;

/**
 * Created by Jacob on 01/06/2017.
 */

public class BitcoinPrice {

    private String usd, gbp, eur;

    public BitcoinPrice (String usdIn, String gbpIn, String eurIn){
        usd = usdIn;
        gbp = gbpIn;
        eur = eurIn;
    }

    public String getUsd() {
        return usd;
    }

    public String getGbp() {
        return gbp;
    }

    public String getEur() {
        return eur;
    }
}
