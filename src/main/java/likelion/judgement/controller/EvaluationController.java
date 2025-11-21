package likelion.judgement.controller;

import likelion.judgement.dto.EvaluationRequest;
import likelion.judgement.dto.EvaluationResponse;
import likelion.judgement.dto.TeamResult;
import likelion.judgement.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/evaluations")
    public ResponseEntity<EvaluationResponse> submitEvaluation(@RequestBody EvaluationRequest request) {
        EvaluationResponse response = evaluationService.submitEvaluation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/results")
    public ResponseEntity<List<TeamResult>> getAllResults() {
        List<TeamResult> results = evaluationService.getAllTeamResults();
        return ResponseEntity.ok(results);
    }

  }