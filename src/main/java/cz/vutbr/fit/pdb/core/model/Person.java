/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

import java.util.LinkedList;
import java.util.List;

public class Person {

    protected int idPerson;

    protected String firstName;

    protected String lastName;

    protected String street;

    protected String city;

    protected String psc;

    protected String email;

    protected List<Owner> propertyHistory;

    public Person() {
        idPerson = 0;
        firstName = "";
        lastName = "";
        street = "";
        city = "";
        psc = "";
        email = "";
        propertyHistory = new LinkedList<Owner>() ;
    }

    public Person(int id, String firstName, String lastName, String street,
                  String city, String psc, String email, LinkedList<Owner> propertyHistory) {
        this.idPerson = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.city = city;
        this.psc = psc;
        this.email = email;
        this.propertyHistory = propertyHistory;
    }

    public int getIdPerson() {
        return idPerson;
    }

    public void setIdPerson(int idPerson) {
        this.idPerson = idPerson;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPsc() {
        return psc;
    }

    public void setPsc(String psc) {
        this.psc = psc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Owner> getPropertyHistory() {
        return propertyHistory;
    }

    public void setPropertyHistory(List<Owner> propertyHistory) {
        this.propertyHistory = propertyHistory;
    }
}
