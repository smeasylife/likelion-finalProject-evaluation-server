package likelion.judgement.service;

import likelion.judgement.dto.EvaluationRequest;
import likelion.judgement.dto.TeamResult;
import likelion.judgement.entity.Evaluation;
import likelion.judgement.entity.Team;
import likelion.judgement.repository.EvaluationRepository;
import likelion.judgement.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvaluationService {

    private final TeamRepository teamRepository;
    private final EvaluationRepository evaluationRepository;

    @Transactional
    public Evaluation submitEvaluation(EvaluationRequest request) {
        Team team = teamRepository.findByName(request.getTeamName())
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다: " + request.getTeamName()));

        if (evaluationRepository.existsByTeamIdAndEvaluatorRoleAndEvaluatorName(
                team.getId(), request.getEvaluatorRole(), request.getEvaluatorName())) {
            throw new IllegalArgumentException("이미 평가한 팀입니다.");
        }

        validateScores(request);

        Evaluation evaluation = Evaluation.builder()
                .team(team)
                .evaluatorRole(request.getEvaluatorRole())
                .evaluatorName(request.getEvaluatorName())
                .designTotalScore(request.getDesignTotalScore())
                .developmentTotalScore(request.getDevelopmentTotalScore())
                .commonTotalScore(request.getCommonTotalScore())
                .build();

        return evaluationRepository.save(evaluation);
    }

    public List<TeamResult> getAllTeamResults() {
        List<Team> teams = teamRepository.findAll();

        return teams.stream()
                .map(this::calculateTeamResult)
                .sorted((a, b) -> Double.compare(b.getAverageTotalScore(), a.getAverageTotalScore()))
                .collect(Collectors.toList());
    }

    public TeamResult getTeamResult(String teamName) {
        Team team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다: " + teamName));

        return calculateTeamResult(team);
    }

    private TeamResult calculateTeamResult(Team team) {
        List<Evaluation> evaluations = evaluationRepository.findByTeam(team);

        if (evaluations.isEmpty()) {
            return TeamResult.builder()
                    .teamId(team.getId())
                    .teamName(team.getName())
                    .designTotalScore(0)
                    .developmentTotalScore(0)
                    .commonTotalScore(0)
                    .totalScore(0)
                    .evaluationCount(0)
                    .averageDesignScore(0.0)
                    .averageDevelopmentScore(0.0)
                    .averageCommonScore(0.0)
                    .averageTotalScore(0.0)
                    .build();
        }

        int designSum = evaluations.stream().mapToInt(Evaluation::getDesignTotalScore).sum();
        int developmentSum = evaluations.stream().mapToInt(Evaluation::getDevelopmentTotalScore).sum();
        int commonSum = evaluations.stream().mapToInt(Evaluation::getCommonTotalScore).sum();
        int totalSum = designSum + developmentSum + commonSum;
        int count = evaluations.size();

        return TeamResult.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .designTotalScore(designSum)
                .developmentTotalScore(developmentSum)
                .commonTotalScore(commonSum)
                .totalScore(totalSum)
                .evaluationCount(count)
                .averageDesignScore((double) designSum / count)
                .averageDevelopmentScore((double) developmentSum / count)
                .averageCommonScore((double) commonSum / count)
                .averageTotalScore((double) totalSum / count)
                .build();
    }

    private void validateScores(EvaluationRequest request) {
        if (request.getDesignTotalScore() < 5 || request.getDesignTotalScore() > 25) {
            throw new IllegalArgumentException("디자인 점수는 5-25점 사이여야 합니다.");
        }
        if (request.getDevelopmentTotalScore() < 7 || request.getDevelopmentTotalScore() > 35) {
            throw new IllegalArgumentException("개발 점수는 7-35점 사이여야 합니다.");
        }
        if (request.getCommonTotalScore() < 3 || request.getCommonTotalScore() > 15) {
            throw new IllegalArgumentException("공통 점수는 3-15점 사이여야 합니다.");
        }
    }
}