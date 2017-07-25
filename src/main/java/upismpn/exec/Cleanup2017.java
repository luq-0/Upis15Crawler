package upismpn.exec;

import upismpn.download.DownloadController;
import upismpn.download.Smer2017;
import upismpn.download.Smerovi2017;
import upismpn.download.Ucenik2017;
import upismpn.obrada.FileMerger;
import upismpn.obrada2017.UceniciBase;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cleanup2017 {
    public static void test() {
        List<String> strings = FileMerger.readFromOne(new File(DownloadController.DATA_FOLDER, FileMerger.FILENAME));
        Set<Ucenik2017> ucenici = new HashSet<>();
        for(String ucStr : strings) {
            String[] ucData = ucStr.split("\\n", 2);
            Ucenik2017 uc = new Ucenik2017(ucData[0]);
            ucenici.add(uc);
            try {
                uc.loadFromJson();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ucenici.forEach(uc -> uc.saveToFile(DownloadController.DATA_FOLDER, true));
        FileMerger.mergeToOne(DownloadController.DATA_FOLDER);
    }

    public static void test2() {
        UceniciBase.load();
        System.out.println(UceniciBase.svi().filter(uc -> uc.bodovaTakmicenja >= 8).findAny());
    }

    public static void reloadSmerovi17() {
        Smerovi2017 smerovi = Smerovi2017.getInstance();
        smerovi.loadFromFile();
        smerovi.iterate(0);
        while(smerovi.hasNext()) {
            Smer2017 smer = (Smer2017) smerovi.getNext();
            smer.loadFromJson();
        }
        smerovi.save();
    }

    public static void sviSmerovi17() {
        UceniciBase.load();
        Set<String> predmeti = new HashSet<>();
        UceniciBase.svi().map(uc -> uc.osmiRaz.ocene.keySet()).forEach(predmeti::addAll);
        System.out.println(predmeti);
    }
}
