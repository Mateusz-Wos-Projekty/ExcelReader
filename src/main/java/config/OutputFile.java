package config;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class OutputFile {

    @NotBlank
    private String newFileName;
    @NotEmpty
    private String[] columns;

    //SETTERS
    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }

    //GETTERS
    public String getNewFileName() {
        return newFileName;
    }

    public String[] getColumns() {
        return columns;
    }
}
