package util.recordkeeper;

public class RecordInformation {
    String originalValue;
    String updatedValue;
    String additionalInfo;

    public RecordInformation(String originalValue){
        this.originalValue = originalValue;
    }
    public RecordInformation(String originalValue, String updatedValue){
        this.originalValue = originalValue;
        this.updatedValue = updatedValue;
    }
}