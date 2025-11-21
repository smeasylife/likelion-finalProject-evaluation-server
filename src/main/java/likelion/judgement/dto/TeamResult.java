package likelion.judgement.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamResult {
    private Long teamId;
    private String teamName;

    // 심사위원 총 점수
    private Integer judgeTotalScore;
    private Integer judgeEvaluationCount;

    // 아기사자 총 점수
    private Integer menteeTotalScore;
    private Integer menteeEvaluationCount;

}