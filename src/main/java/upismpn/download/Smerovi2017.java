package upismpn.download;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by luka on 2.7.17..
 */
public class Smerovi2017 extends Smerovi {
    protected Smerovi2017() {
    }

    private static Smerovi2017 instance;

    public static Smerovi2017 getInstance() {
        if(instance == null) instance = new Smerovi2017();
        return instance;
    }

    private static final int BR_OKRUGA = 30;

    @Override
    public void loadFromNet() {
        try {
            for (int i = 1; i <= BR_OKRUGA; i++) {
                int page = 1;
                while(true) {
                    Document doc = Jsoup.connect(generateOkrugUrl(i))
                            .requestBody(generatePageParams(page++))
                            .post();
                    Elements skole = doc.select(".tbody .kolona2");
                    Elements kvote = doc.select(".tbody .kolona3");
                    if(skole.size() == 0) break;
                    for (int j = 0; j < skole.size(); j++) {
                        Element sk = skole.get(j);
                        String[] data = parseSmer(sk.text());
                        String kvota = kvote.get(j).text();
                        Smer2017 smer = new Smer2017(data[0], data[1], data[2], data[3], kvota);
                        addToBase(smer);
                    }
                }
                System.out.println("Smerovi: gotov okrug " + i + "/" + BR_OKRUGA);
            }
        } catch (IOException ex) {
            Logger.getLogger(StudentDownloader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String generateOkrugUrl(int okrug) {
        DecimalFormat df = new DecimalFormat("00");
        return "http://upis.mpn.gov.rs/Lat/Srednje-skole-pretraga/" + df.format(okrug) + "0000000000000000";
    }

    private static String generatePageParams(int page) {
        return "id_grid=wuc_Grid1&grid_refresh=1&filter=(IDJezik+%3D+1)+AND+(PodrucjeRadaID+%3D+16)+AND+(ObrazovniProfilID+%3D+685)&sort=&page=" + page + "&page_size=-1&IDPocetniFilter=0&IDStalniFilter=0&multiselect=0&Pretraga=&executeUCMethod=wuc_Grid%3FDBID%3D8%26ID%3Dwuc_Grid1%26PageSize%3D-1%26ClientMode%3D1&methodName=InitGrid";
    }

    //sifra, ime, mesto, jezik
    private static String[] parseSmer(String smer) {
        StringBuilder ime= new StringBuilder();
        String sifra, podrucje, jezik;
        int spaces = 0, i=0;
        while(spaces < 3)
            if(Character.isWhitespace(smer.charAt(i++)))
                spaces++;
        sifra = smer.substring(0, i);
        String[] data = smer.substring(i).split(",");
        int l = data.length;
        jezik = data[l-1];
        podrucje = data[l-2];
        for(int j=0; j<l-2; j++)
            ime.append(data[j]).append(",");
        return new String[]{sifra, ime.toString(), podrucje, jezik};
    }

    @Override
    public Smer2017 get(String sifra) {
        return (Smer2017) super.get(sifra);
    }

    @Override
    protected Smer createSmer(String compactString) {
        return new Smer2017(compactString);
    }
}