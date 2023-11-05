package cluz.com.agenda.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Error implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("message")
    @Valid
    private String message;

    @JsonProperty("source")
    @Valid
    private String source;

    @JsonProperty("reason")
    @Valid
    private String reason;

    @JsonProperty("status")
    @Valid
    private Integer status;

    public Error status(Integer status){
        this.status = status;
        return this;
    }

    @NotNull
    @Schema(name = "status", example = "404", description = "Http response status codes", required = true)
    public Integer getStatus(){
        return status;
    }

    public void setStatus(Integer status){
        this.status = status;
    }

    public Error message(String message){
        this.message = message;
        return this;
    }

    @NotNull
    @Schema(name = "message", example = "cpf validation message", description = "Error message details", required = true)
    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public Error source(String source){
        this.source = source;
        return this;
    }

    @NotNull
    @Schema(name = "source", example = "cpf", description = "Error source", required = true)
    public String getSource(){
        return source;
    }

    public void setSource(String source){
        this.source = source;
    }

    public Error reason(String reason){
        this.reason = reason;
        return this;
    }

    @NotNull
    @Schema(name = "reason", example = "The request constains an empty value for CPF", description = "Explanation of the reason for the error", required = true)
    public String getReason(){
        return reason;
    }

    public void setSReason(String reason){
        this.reason = reason;
    }

}
