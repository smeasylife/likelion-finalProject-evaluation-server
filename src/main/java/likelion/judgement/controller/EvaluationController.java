package likelion.judgement.controller;

import likelion.judgement.dto.EvaluationRequest;
import likelion.judgement.dto.TeamResult;
import likelion.judgement.entity.Evaluation;
import likelion.judgement.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/evaluations")
    public ResponseEntity<?> submitEvaluation(@RequestBody EvaluationRequest request) {
        try {
            Evaluation evaluation = evaluationService.submitEvaluation(request);
            return ResponseEntity.ok(evaluation);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("평가 제출 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/results")
    public ResponseEntity<List<TeamResult>> getAllResults() {
        try {
            List<TeamResult> results = evaluationService.getAllTeamResults();
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/results/{teamName}")
    public ResponseEntity<?> getTeamResult(@PathVariable String teamName) {
        try {
            TeamResult result = evaluationService.getTeamResult(teamName);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}