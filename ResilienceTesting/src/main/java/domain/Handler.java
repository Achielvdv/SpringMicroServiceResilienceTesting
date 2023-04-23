package domain;

public class Handler {
    private String spyBeanName;
    private String nameInClass;

    public Handler(String spyBeanName, String nameInClass) {
        this.spyBeanName = spyBeanName;
        this.nameInClass = nameInClass;
    }

    public String getQualifierName() {
        return spyBeanName;
    }

    public void setQualifierName(String qualifierName) {
        this.spyBeanName = qualifierName;
    }

    public String getNameInClass() {
        return nameInClass;
    }

    public void setNameInClass(String nameInClass) {
        this.nameInClass = nameInClass;
    }
}
