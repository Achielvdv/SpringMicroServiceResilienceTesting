package domain;

public class Channel {
    private String qualifierName;
    private String nameInClass;

    public Channel(String qualifierName, String nameInClass) {
        this.qualifierName = qualifierName;
        this.nameInClass = nameInClass;
    }

    public Channel() {
    }

    public String getQualifierName() {
        return qualifierName;
    }

    public void setQualifierName(String qualifierName) {
        this.qualifierName = qualifierName;
    }

    public String getNameInClass() {
        return nameInClass;
    }

    public void setNameInClass(String nameInClass) {
        this.nameInClass = nameInClass;
    }


}
