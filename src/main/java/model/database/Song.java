package model.database;

import controller.managers.DatabaseManager;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(uniqueConstraints=
@UniqueConstraint(columnNames = {"path"}))
public class Song implements Serializable{

    @Id
    @GeneratedValue
    private int id;

    public String artist = "";
    public String song = "";
    public String image = "";
    public String path  = "";
    public String mp3 = "";
    public String parent_dir = "";

    public int getId(){
        return id;
    }

    public boolean save(){
        try {
            EntityManager em = DatabaseManager.getInstance().getFactory();
            em.getTransaction().begin();
            em.persist(this);
            em.getTransaction().commit();
        } catch (Exception  e) {
            return false;
        }
        return true;
    }
}
