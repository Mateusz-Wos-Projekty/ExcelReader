package config;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
public class Replacement {
    @NotBlank
    private String columnName;
    @NotBlank
    private String before;
    @NotBlank
    private String after;

    //SETTERS
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    //GETTERS
    public String getColumnName() {
        return columnName;
    }

    public String getBefore() {
        return before;
    }

    public String getAfter() {
        return after;
    }

}
