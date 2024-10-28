package config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class Filtering {
    private static final String COMA_SEPARATOR = ",";
    private static final String COMA_WITH_DOUBLE_SPACES = " , ";
    private static final String COMA_WITH_LEADING_SPACE = " ,";
    private static final String COMA_WITH_TRAILING_SPACE = ", ";
    private static final String BLANK_VALUE = "";

    @NotNull
    private String columnName;
    @NotNull
    private String filterValue;
    @NotNull(message = "You must provide the filter option")
    private FilterOption filterOption;

    //SETTERS
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setFilterOption(FilterOption filterOption) {
        this.filterOption = filterOption;
    }

    @JsonProperty("filterValue")
    public void setFilterValue(String filterValue) {
        this.filterValue = getFilterValueWithSpacesRemoved(filterValue);
    }

    //GETTERS
    public String getColumnName() {
        return columnName;
    }

    public FilterOption getFilterOption() {
        return filterOption;
    }

    public String getFilterValue() {
        return filterValue;
    }

    private String getFilterValueWithSpacesRemoved(String filterValue) {
        if (filterValue == null) {
            return BLANK_VALUE;
        }
        return filterValue.trim()
                .replaceAll(COMA_WITH_DOUBLE_SPACES, COMA_SEPARATOR)
                .replaceAll(COMA_WITH_TRAILING_SPACE, COMA_SEPARATOR)
                .replaceAll(COMA_WITH_LEADING_SPACE, COMA_SEPARATOR);
    }
}
