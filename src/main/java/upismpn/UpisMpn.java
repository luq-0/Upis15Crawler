package upismpn;

import upismpn.download.DownloadConfig;
import upismpn.download.DownloadController;
import upismpn.download.misc.OsnovneDownloader;
import upismpn.obrada.Exec;

/**
 *
 * @author Luka
 */
public class UpisMpn {

    public static final boolean    DEBUG             = true;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length < 1)
            System.out.println("invalid args. Valid args: dl & exec [method name]");
        else {
            if(args[0].equalsIgnoreCase("dl") &&
                    (args.length == 1 || args[1].equalsIgnoreCase("uc")))
                DownloadController.startDownload(new DownloadConfig.New());
            if(args[0].equalsIgnoreCase("dl") && args[1].equalsIgnoreCase("os"))
                OsnovneDownloader.downloadOsnovneData();
            if(args[0].equalsIgnoreCase("exec")) {
                String[] semPrvog = new String[args.length-1];
                System.arraycopy(args, 1, semPrvog, 0, semPrvog.length);
                Exec.doExec(semPrvog);
            }
        }
    }

}