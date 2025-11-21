package likelion.judgement.exception;

public class DuplicateEvaluationException extends RuntimeException {

    public DuplicateEvaluationException(String message) {
        super(message);
    }

    public DuplicateEvaluationException(String evaluatorName, String evaluatorRole, String teamName) {
        super(String.format("%s(%s)님은 이미 %s 팀을 평가했습니다.", evaluatorName, evaluatorRole, teamName));
    }
}