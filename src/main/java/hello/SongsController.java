package hello;

import controller.managers.DatabaseManager;
import javafx.util.Pair;
import model.database.Song;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class SongsController {

    @RequestMapping("/songs")
    public Vector<Song> songs(@RequestHeader(value="Range", defaultValue="items=0-40") String range) {
        Pair<Integer, Integer> parsedRange = parseRange(range);
        EntityManager em = DatabaseManager.getInstance().getFactory();
        Vector<Song> listData = new Vector<Song>();
        Query query = em.createQuery("from " + Song.class.getSimpleName());
        query.setFirstResult(parsedRange.getKey());
        query.setMaxResults(parsedRange.getValue()-parsedRange.getKey());
        listData.addAll(query.getResultList());
        return listData;
    }

    @RequestMapping(value = "/songs/{songId}/image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> songImage(@PathVariable Long songId) {

        EntityManager em = DatabaseManager.getInstance().getFactory();
        Query query = em.createQuery("from " + Song.class.getSimpleName() + " where id = " + songId);
        Song song = (Song) query.getSingleResult();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(song.path+"/"+song.image);
            File f = new File(song.path+"/"+song.image);
            return ResponseEntity.ok()
                    .contentLength(f.length())
                    .contentType(MediaType.parseMediaType(Files.probeContentType(f.toPath())))
                    .body(new InputStreamResource(fileInputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ResourceNotFoundException();
    }

    @RequestMapping(value = "/songs/{songId}/song", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getSong(@PathVariable Long songId,
                                                       @RequestHeader(value="Range", defaultValue="bytes=0-40") String range) {

        EntityManager em = DatabaseManager.getInstance().getFactory();
        Query query = em.createQuery("from " + Song.class.getSimpleName() + " where id = " + songId);
        Song song = (Song) query.getSingleResult();
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(song.path+"/"+song.mp3);
            File f = new File(song.path+"/"+song.mp3);
            return ResponseEntity.ok()
                    .contentLength(f.length())
                    .contentType(MediaType.parseMediaType(Files.probeContentType(f.toPath())))
                    .body(new InputStreamResource(fileInputStream));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ResourceNotFoundException();
    }

    private Pair<Integer, Integer> parseRange(String s){
        s = s.replaceAll("[^0-9]+", " ");
        List<String> arr = Arrays.asList(s.trim().split(" "));
        return new Pair<Integer, Integer>(Integer.parseInt(arr.get(0)), Integer.parseInt(arr.get(1)));
    }
}