package project.person;

public class PersonEventListener {
  Person person;
  public PersonEventListener(Person person){
    this.person = person;
  }

  public Person getPerson() {
    return person;
  }
}
