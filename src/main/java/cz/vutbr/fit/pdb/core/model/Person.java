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

/**
 * Model of database table Person.
 */
public class Person {

    protected int idPerson;

    protected String firstName;

    protected String lastName;

    protected String street;

    protected String city;

    protected String psc;

    protected String email;

    protected List<Owner> propertyHistory;

    /**
     * Constructor of @see Person
     */
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

    /**
     * Constructor of @see Person
     * @param id Integer value, which represents id of person
     * @param firstName String value, which represents first name of person
     * @param lastName String value, which represents last name of person
     * @param street String value, which represents street, where person lives
     * @param city String value, which represents city, where person lives
     * @param psc String value, which represents psc
     * @param email String value, which represents email address
     * @param propertyHistory List of @see Owner objects, history of properties, which person owned
     */
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

    /**
     * Method returns id of person.
     * @return Integer value, which represents id of person
     */
    public int getIdPerson() {
        return idPerson;
    }

    /**
     * Method sets id of person.
     * @param idPerson Integer value, which represents id of person
     */
    public void setIdPerson(int idPerson) {
        this.idPerson = idPerson;
    }

    /**
     * Method returns first name of person.
     * @return String value, which represents first name of person
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Method sets first name of person.
     * @param firstName String value, which represents first name of person
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Method gets last name of person.
     * @return String value, which represents last name of person
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Method sets last name of person.
     * @param lastName String value, which represents last name of person
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Method gets street of person.
     * @return String value, which represents street, where person lives
     */
    public String getStreet() {
        return street;
    }

    /**
     * Method sets street of person.
     * @param street String value, which represents street, where person lives
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Method gets city, where person lives.
     * @return String value, which represents city, where person lives
     */
    public String getCity() {
        return city;
    }

    /**
     * Method sets city, where person lives.
     * @param city String value, which represents city, where person lives
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Method gets psc of city, where person lives.
     * @return String value, which represents psc
     */
    public String getPsc() {
        return psc;
    }

    /**
     * Method sets psc of city, where person lives.
     * @param psc String value, which represents psc
     */
    public void setPsc(String psc) {
        this.psc = psc;
    }

    /**
     * Method gets email address of person.
     * @return String value, which represents email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Method sets email address of person.
     * @param email String value, which represents email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Method returns list of history of properties, which person owned
     * @return List of @see Owner objects, history of properties, which person owned
     */
    public List<Owner> getPropertyHistory() {
        return propertyHistory;
    }

    /**
     * Method sets list of history of properties, which person owned
     * @param propertyHistory List of @see Owner objects, history of properties, which person owned
     */
    public void setPropertyHistory(List<Owner> propertyHistory) {
        this.propertyHistory = propertyHistory;
    }
}
