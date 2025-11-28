package likelion.judgement.service;

import likelion.judgement.dto.EvaluationRequest;
import likelion.judgement.dto.EvaluationResponse;
import likelion.judgement.dto.TeamResult;
import likelion.judgement.entity.Evaluation;
import likelion.judgement.entity.Team;
import likelion.judgement.exception.DuplicateEvaluationException;
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
    public EvaluationResponse submitEvaluation(EvaluationRequest request) {
        Team team = teamRepository.findByName(request.getTeamName())
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다: " + request.getTeamName()));

        if (evaluationRepository.existsByTeamIdAndEvaluatorRoleAndEvaluatorName(
                team.getId(), request.getEvaluatorRole(), request.getEvaluatorName())) {
            throw new DuplicateEvaluationException(request.getEvaluatorName(), request.getEvaluatorRole(), request.getTeamName());
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

        Evaluation savedEvaluation = evaluationRepository.save(evaluation);

        return EvaluationResponse.builder()
                .id(savedEvaluation.getId())
                .teamName(savedEvaluation.getTeam().getName())
                .evaluatorRole(savedEvaluation.getEvaluatorRole())
                .evaluatorName(savedEvaluation.getEvaluatorName())
                .designTotalScore(savedEvaluation.getDesignTotalScore())
                .developmentTotalScore(savedEvaluation.getDevelopmentTotalScore())
                .commonTotalScore(savedEvaluation.getCommonTotalScore())
                .build();
    }

    public List<TeamResult> getAllTeamResults() {
        List<Team> teams = teamRepository.findAll();

        return teams.stream()
                .map(this::calculateTeamResult)
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
                    .judgeTotalScore(0)
                    .judgeEvaluationCount(0)
                    .menteeTotalScore(0)
                    .menteeEvaluationCount(0)
                    .build();
        }

        // 심사위원 평가 필터링
        List<Evaluation> judgeEvaluations = evaluations.stream()
                .filter(e -> "심사위원".equals(e.getEvaluatorRole()))
                .toList();

        // 아기사자 평가 필터링
        List<Evaluation> menteeEvaluations = evaluations.stream()
                .filter(e -> "아기사자".equals(e.getEvaluatorRole()))
                .toList();

        // 심사위원 점수 계산
        int judgeTotalScore = judgeEvaluations.stream()
                .mapToInt(e -> e.getDesignTotalScore() + e.getDevelopmentTotalScore() + e.getCommonTotalScore())
                .sum();
        int judgeCount = judgeEvaluations.size();

        // 아기사자 점수 계산
        int menteeTotalScore = menteeEvaluations.stream()
                .mapToInt(e -> e.getDesignTotalScore() + e.getDevelopmentTotalScore() + e.getCommonTotalScore())
                .sum();
        int menteeCount = menteeEvaluations.size();

        return TeamResult.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .judgeTotalScore(judgeTotalScore)
                .judgeEvaluationCount(judgeCount)
                .menteeTotalScore(menteeTotalScore)
                .menteeEvaluationCount(menteeCount)
                .build();
    }

    private void validateScores(EvaluationRequest request) {
        if (request.getDesignTotalScore() < 5 || request.getDesignTotalScore() > 25) {
            throw new IllegalArgumentException("디자인 점수는 5-25점 사이여야 합니다.");
        }
        if (request.getDevelopmentTotalScore() < 7 || request.getDevelopmentTotalScore() > 35) {
            throw new IllegalArgumentException("개발 점수는 7-35점 사이여야 합니다.");
        }
        if (request.getCommonTotalScore() < 3 || request.getCommonTotalScore() > 20) {
            throw new IllegalArgumentException("공통 점수는 4-20점 사이여야 합니다.");
        }
    }

    @Transactional
    public void deleteAllEvaluations() {
        evaluationRepository.deleteAll();
    }
}