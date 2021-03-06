package rs.lukaj.upisstats.scraper.download;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import rs.lukaj.upisstats.scraper.obrada2017.LetterUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by luka on 2.7.17.
 */
public class Smer2017 extends Smer {
    protected String skola;
    protected String smer;
    protected String jezik;
    protected String opstina;
    protected String okrug;
    protected String podrucje;
    protected String trajanje;
    protected String kvotaUmanjenje;
    protected String upisano1K, upisano2K;
    protected String minBodova1K, minBodova2K, kvota2K;

    protected String json;


    public String getSkola() {
        return skola;
    }

    public String getJezik() {
        return jezik;
    }

    public String getOpstina() {
        return opstina;
    }

    public String getOkrug() {
        return okrug;
    }

    public String getTrajanje() {
        return trajanje;
    }

    public String getSmer() {
        return smer;
    }

    //let's pretend this isn't godawful
    public String getPodrucje2017() {
        return podrucje;
    }

    public String getJson() {
        return json;
    }

    public Smer2017(String sifra, String kvota) {
        super(sifra, null, kvota);
    }

    public Smer2017(String compactString) {
        super(compactString);
        smer = super.getPodrucje();
        String[] tokens = compactString.split("\\\\");
        skola = LetterUtils.toLatin(tokens[3]);
        trajanje = tokens[4];
        kvotaUmanjenje = tokens[5];
        jezik = tokens[6];
        opstina = tokens[7];
        okrug = tokens[8];
        podrucje = tokens[9];
        upisano1K = tokens[10];
        minBodova1K = tokens[11];
        kvota2K = tokens[12];
        upisano2K = tokens[13];
        minBodova2K = tokens[14];
    }

    public String toCompactString() {
        StringBuilder str = new StringBuilder(128);
        if(skola != null && skola.endsWith(",")) skola = skola.substring(0, skola.length()-1);
        str.append(getSifra()).append("\\").append(smer).append("\\").append(getKvota()).append("\\").append(skola).append("\\")
                .append(trajanje).append("\\").append(kvotaUmanjenje).append("\\").append(jezik).append("\\")
                .append(opstina).append("\\").append(okrug).append("\\").append(podrucje).append("\\")
        .append(upisano1K).append("\\").append(minBodova1K).append("\\").append(kvota2K).append("\\").append(upisano2K).append("\\").append(minBodova2K).append("\n");
        return str.toString();
    }

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    public void saveJson(String json) {
        executor.execute(() -> {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Smerovi.SMEROVI_FOLDER, getSifra() + ".json")));
                bw.write(json);
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Smerovi2017.getInstance().save(); //not the finest solution
        });
    }

    public void exetendFromJson() {
        try {
            JsonParser parser = new JsonParser();
            trajanje = (parser.parse(Files.readAllLines(new File(Smerovi.SMEROVI_FOLDER, getSifra() + ".json").toPath()).stream().reduce("", (a, b)->a+b)).getAsJsonArray().get(0).getAsJsonObject().get("Trajanje").getAsString()); //I'm not it the mood, mkay?
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromJson() {
        try {
            JsonParser parse = new JsonParser();
            SmerMappingTools.Mapper mapper = SmerMappingTools.getMapper(2018);
            JsonObject json = parse.parse(Files.readAllLines(new File(Smerovi.SMEROVI_FOLDER, getSifra() + ".json").toPath()).get(0)).getAsJsonArray().get(0).getAsJsonObject();
            loadFromJson(mapper, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromJson(SmerMappingTools.Mapper mapper, JsonObject json) {
        skola = json.get("NazivSkole2").getAsString();
        smer = json.get("Profil2").getAsString();
        jezik = mapper.getJezik(Integer.parseInt(json.get("IDJezik").getAsString()));
        opstina = mapper.getOpstina(Integer.parseInt(json.get("IDOpstina").getAsString()));
        okrug = mapper.getOkrug(Integer.parseInt(json.get("IDOkrug").getAsString()));
        kvotaUmanjenje = json.get("KvotaUmanjenje").getAsString();
        podrucje = json.get("Naziv2").getAsString();
        trajanje = json.get("Trajanje").getAsString();

        upisano1K = json.get("Upisano1K").getAsString();
        upisano2K = json.get("Upisano2K").getAsString();
        minBodova1K = json.get("MinBodova1K").getAsString();
        minBodova2K = json.get("MinBodova2K").getAsString();
        kvota2K  = json.get("Kvota2K").getAsString().trim();
    }

    public String getKvotaUmanjenje() {
        return kvotaUmanjenje;
    }

    public String getUpisano1K() {
        return upisano1K;
    }

    public String getUpisano2K() {
        return upisano2K;
    }

    public String getMinBodova1K() {
        return minBodova1K;
    }

    public String getMinBodova2K() {
        return minBodova2K;
    }

    public String getKvota2K() {
        return kvota2K;
    }
}
