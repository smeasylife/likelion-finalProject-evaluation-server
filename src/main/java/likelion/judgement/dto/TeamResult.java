package likelion.judgement.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamResult {
    private Long teamId;
    private String teamName;
    private Integer designTotalScore;
    private Integer developmentTotalScore;
    private Integer commonTotalScore;
    private Integer totalScore;
    private Integer evaluationCount;
    private Double averageDesignScore;
    private Double averageDevelopmentScore;
    private Double averageCommonScore;
    private Double averageTotalScore;
}