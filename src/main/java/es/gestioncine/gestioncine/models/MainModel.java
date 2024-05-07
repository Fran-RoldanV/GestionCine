package es.gestioncine.gestioncine.models;

public class MainModel {
    private String name;
    private int age;

    // Constructor
    public MainModel(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
