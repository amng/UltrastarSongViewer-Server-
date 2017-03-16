package controller.managers;

import javafx.util.Pair;
import model.database.Song;
import org.mozilla.universalchardet.UniversalDetector;
import views.constants.Extras;

import java.io.*;
import java.util.HashMap;
import java.util.Observable;

public class FileManager extends Observable {

    private static FileManager instance;

    public static String DATA_FILE_EXTENSION = ".txt";

    private static String TITLE 	= 	"#TITLE";
    private static String ARTIST 	= 	"#ARTIST";
    private static String MP3 	    = 	"#MP3";
    private static String COVER 	= 	"#COVER";
    private static String VIDEO 	= 	"#VIDEO";
    private static String BPM  	    = 	"#BPM";
    private static String GAP 	    = 	"#GAP";

    private FileManager(){}

    public static FileManager getInstance(){
        return instance == null ? instance = new FileManager() : instance;
    }

    public void syncData(String dir) {
        File dirFile = new File(dir);
        String[] directories = dirFile.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        setChanged();
        notifyObservers(new Pair<String, Integer>(Extras.SONGS_COUNT, directories.length));
        for (int i = 0; i < directories.length; i++) {
            HashMap<String, String> hashMap = getDataFromFile(dir+"/"+directories[i]);
            Song song = new Song();
            song.artist = hashMap.get(ARTIST);
            song.image = hashMap.get(COVER);
            song.mp3 = hashMap.get(MP3);
            song.path = dir+"/"+directories[i];
            song.song = hashMap.get(TITLE);
            song.parent_dir = dir;
            song.save();
            setChanged();
            notifyObservers(new Pair<String, Integer>(Extras.SONGS_PROGRESS, i));
        }
    }

    public HashMap<String, String> getDataFromFile(String dir) {
        HashMap<String, String> res = new HashMap<String, String>();

        File directory = new File(dir);
        String file;
        if (directory.isDirectory()) {
            String[] files = directory.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(DATA_FILE_EXTENSION);
                }
            });
            if (files.length > 0) {
                file = files[0];
            } else {
                return res;
            }
        } else return res;


        FileInputStream freader = null;
        BufferedReader reader = null;
        try {
            File f = new File(dir+"/"+file);
            freader = new FileInputStream(f);
            reader = new BufferedReader(new InputStreamReader(freader , getCharset(f)));
            String line;
            while((line = reader.readLine()) != null && line.contains("#")){
                if(line.contains(":")){
                    String[] splitter = line.split(":");
                    if (splitter.length == 2) {
                        res.put(splitter[0], splitter[1]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert freader != null;
                freader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert reader != null;
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }


    private String getCharset(File f) throws IOException {
        // (1)
        if (!f.exists()) {
            return "utf-8";
        }
        UniversalDetector detector = new UniversalDetector(null);

        int nread;
        FileInputStream fis = new FileInputStream(f);
        byte[] buf = new byte[4096];
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, nread);
        }
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        if (encoding != null) {
            System.out.println("Detected encoding = " + encoding);
        } else {
            System.out.println("No encoding detected.");
        }

        detector.reset();
        return encoding == null ? "utf-8" : encoding;
    }
}
