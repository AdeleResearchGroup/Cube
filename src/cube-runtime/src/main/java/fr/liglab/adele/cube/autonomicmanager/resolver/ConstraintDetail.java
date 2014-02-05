package fr.liglab.adele.cube.autonomicmanager.resolver;

/**
 * Created by debbabi on 04/02/14.
 */
public class ConstraintDetail {
    Constraint constraint;
    String subject;
    String value;

    public ConstraintDetail(Constraint constraint, String subject, String value) {
        this.constraint = constraint;
        this.subject = subject;
        this.value = value;
    }

    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
