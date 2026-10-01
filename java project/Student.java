package studentperformance;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    private double math;

    private double science;

    private double english;

    private double total;

    private double average;

    private String grade;


    // Default constructor

    public Student() {

    }


    // Get ID

    public Long getId() {

        return id;

    }


    // Get name

    public String getName() {

        return name;

    }


    // Set name

    public void setName(String name) {

        this.name = name;

    }


    // Get Math

    public double getMath() {

        return math;

    }


    // Set Math

    public void setMath(double math) {

        this.math = math;

    }


    // Get Science

    public double getScience() {

        return science;

    }


    // Set Science

    public void setScience(double science) {

        this.science = science;

    }


    // Get English

    public double getEnglish() {

        return english;

    }


    // Set English

    public void setEnglish(double english) {

        this.english = english;

    }


    // Get Total

    public double getTotal() {

        return total;

    }


    // Set Total

    public void setTotal(double total) {

        this.total = total;

    }


    // Get Average

    public double getAverage() {

        return average;

    }


    // Set Average

    public void setAverage(double average) {

        this.average = average;

    }


    // Get Grade

    public String getGrade() {

        return grade;

    }


    // Set Grade

    public void setGrade(String grade) {

        this.grade = grade;

    }

}