/*
 * Copyright (C) 2017 VUT FIT PDB project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.vutbr.fit.pdb.core.model;

import java.util.Date;

/**
 * Model of database table Owner.
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */
public class Owner {

    protected Person person;

    protected Property property;

    protected Date validFrom;

    protected Date validTo;

    /**
     * Constructor of @see Owner.
     */
    public Owner() {
        person = new Person();
        property = new Property();
    }

    /**
     * Constructor of @see Owner.
     *
     * @param person    @see Person, who owns property in time interval
     * @param property  @see Property, which is owned by person in time interval
     * @param validFrom @see Date, from which the property is owned by person
     * @param validTo   @see Date, into which the property is owned by person
     */
    public Owner(Person person, Property property, Date validFrom, Date validTo) {
        this.person = person;
        this.property = property;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    /**
     * Method returns person.
     *
     * @return @see Person, who owns property in time interval
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Method sets person.
     *
     * @param person @see Person, who owns property in time interval
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Method returns property
     *
     * @return @see Property, which is owned by person in time interval
     */
    public Property getProperty() {
        return property;
    }

    /**
     * Method sets property.
     *
     * @param property @see Property, which is owned by person in time interval
     */
    public void setProperty(Property property) {
        this.property = property;
    }

    /**
     * Method returns date to.
     *
     * @return @see Date, from which the property is owned by person
     */
    public Date getValidTo() {
        return validTo;
    }

    /**
     * Method sets date to.
     *
     * @param validTo @see Date, into which the property is owned by person
     */
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    /**
     * Method returns date from.
     *
     * @return @see Date, from which the property is owned by person
     */
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * Method sets date from.
     *
     * @param validFrom @see Date, from which the property is owned by person
     */
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * Convert owner to string
     *
     * @return owner string representation
     */
    public String toString() {
        return getPerson().toString() + ": " + getProperty().toString();
    }
}
