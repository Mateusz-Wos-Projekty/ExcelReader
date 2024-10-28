package config;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class ConfigFile {

    @NotBlank(message = "You must provide the file name ")
    private String inputFile;
    @Valid
    private List<Filtering> filteringList;

    @Valid
    private List<Replacement> replacements;

    @Valid
    private OutputFile outputFile;

    //SETTERS
    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public void setFilteringList(List<Filtering> filteringList) {
        this.filteringList = filteringList;
    }

    public void setOutputFile(OutputFile outputFile) {
        this.outputFile = outputFile;
    }

    public void setReplacements(List<Replacement> replacements) {
        this.replacements = replacements;
    }

    //GETTERS
    public String getInputFile() {
        return inputFile;
    }

    public List<Filtering> getFilteringList() {
        return filteringList;
    }

    public OutputFile getOutputFile() {
        return outputFile;
    }

    public List<Replacement> getReplacements() {
        return replacements;
    }

}
