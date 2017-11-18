/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core.model;

public class PersonDuration {

    protected Person person;

    protected Integer duration;

    protected  Integer propertyCount;

    public PersonDuration(Person person, Integer duration, Integer propertyCount){
        this.person = person;
        this.duration=duration;
        this.propertyCount=propertyCount;
    }
    public PersonDuration(){
        this.person = new Person();
        this.duration=0;
        this.propertyCount=0;
    }


    public Integer getPropertyCount() {
        return propertyCount;
    }

    public void setPropertyCount(Integer propertyCount) {
        this.propertyCount = propertyCount;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
