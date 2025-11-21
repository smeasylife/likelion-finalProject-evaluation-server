package likelion.judgement.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EvaluationResponse {
    private Long id;
    private String teamName;
    private String evaluatorRole;
    private String evaluatorName;
    private Integer designTotalScore;
    private Integer developmentTotalScore;
    private Integer commonTotalScore;
}