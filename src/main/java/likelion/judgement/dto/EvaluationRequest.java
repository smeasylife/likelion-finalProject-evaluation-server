package likelion.judgement.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EvaluationRequest {
    private String teamName;
    private String evaluatorRole;
    private String evaluatorName;
    private Integer designTotalScore;
    private Integer developmentTotalScore;
    private Integer commonTotalScore;
}