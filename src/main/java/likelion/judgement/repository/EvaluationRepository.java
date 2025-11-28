package likelion.judgement.repository;

import likelion.judgement.entity.Evaluation;
import likelion.judgement.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    Optional<Evaluation> findByTeamIdAndEvaluatorRoleAndEvaluatorName(
            Long teamId, String evaluatorRole, String evaluatorName);

    boolean existsByTeamIdAndEvaluatorRoleAndEvaluatorName(
            Long teamId, String evaluatorRole, String evaluatorName);

    List<Evaluation> findByTeamId(Long teamId);

    List<Evaluation> findByTeam(Team team);

    @Query("SELECT e FROM Evaluation e WHERE e.team.id = :teamId")
    List<Evaluation> findAllByTeamIdWithTeam(@Param("teamId") Long teamId);

    void deleteAll();
}