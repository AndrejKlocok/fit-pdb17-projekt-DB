/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

import java.util.HashMap;

public class Owner {

    private String id;

    private String firstName;

    private String lastName;

    private String street;

    private String city;

    private String postcode;

    private String email;

    private HashMap<String, Property> propertyHistory;

    // TODO

    public Owner() {
        id = null;
        firstName = "";
        propertyHistory = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, Property> getPropertyHistory() {
        return propertyHistory;
    }

    public void setPropertyHistory(HashMap<String, Property> propertyHistory) {
        this.propertyHistory = propertyHistory;
    }

    public Property getPropertyCurrent() {
        // TODO
        return new Property();
    }

    public void setPropertyCurrent(Property property) {
        // TODO
    }

    public Double getPropertyCurrentCount() {
        // TODO
        return 42d;
    }

    public Double getPropertyCurrentLandAreaSum() {
        // TODO
        return 42d;
    }
}
