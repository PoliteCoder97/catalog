package project.management_panel.person;

import project.person.Person;

public class PanelPersonListEventListener {
    private Person person;

    public PanelPersonListEventListener(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}
