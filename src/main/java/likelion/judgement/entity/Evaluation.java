package likelion.judgement.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "evaluations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"evaluator_role", "evaluator_name", "team_id"}))
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "evaluator_role", nullable = false, length = 50)
    private String evaluatorRole;

    @Column(name = "evaluator_name", nullable = false, length = 100)
    private String evaluatorName;

    @Column(name = "design_total_score", nullable = false)
    private Integer designTotalScore;

    @Column(name = "development_total_score", nullable = false)
    private Integer developmentTotalScore;

    @Column(name = "common_total_score", nullable = false)
    private Integer commonTotalScore;

    @Builder
    public Evaluation(Team team, String evaluatorRole, String evaluatorName,
                     Integer designTotalScore, Integer developmentTotalScore, Integer commonTotalScore) {
        this.team = team;
        this.evaluatorRole = evaluatorRole;
        this.evaluatorName = evaluatorName;
        this.designTotalScore = designTotalScore;
        this.developmentTotalScore = developmentTotalScore;
        this.commonTotalScore = commonTotalScore;
    }
}