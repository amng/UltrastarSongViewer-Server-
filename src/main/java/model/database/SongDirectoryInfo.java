package model.database;

import controller.managers.DatabaseManager;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(uniqueConstraints=
@UniqueConstraint(columnNames = {"directory"}))
public class SongDirectoryInfo implements Serializable{

    @Id
    @GeneratedValue
    private int id;

    public String directory = "";

    public boolean save(){
        try {
            EntityManager em = DatabaseManager.getInstance().getFactory();
            em.getTransaction().begin();
            em.persist(this);
            em.getTransaction().commit();
            return true;
        } catch (Exception  e) {
            return false;
        }
    }

    public boolean delete(){
        try {
            EntityManager em = DatabaseManager.getInstance().getFactory();
            em.getTransaction().begin();
            em.remove(this);
            em.getTransaction().commit();
            return true;
        } catch (Exception  e) {
            return false;
        }
    }
}
