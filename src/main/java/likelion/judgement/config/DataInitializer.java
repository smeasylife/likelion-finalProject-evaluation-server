package likelion.judgement.config;

import likelion.judgement.entity.Team;
import likelion.judgement.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TeamRepository teamRepository;

    @Override
    public void run(String... args) throws Exception {
        if (teamRepository.count() == 0) {
            initializeTeams();
        }
    }

    private void initializeTeams() {
        Team team1 = Team.builder()
                .name("Team A")
                .description("첫 번째 팀")
                .build();

        Team team2 = Team.builder()
                .name("Team B")
                .description("두 번째 팀")
                .build();

        Team team3 = Team.builder()
                .name("Team C")
                .description("세 번째 팀")
                .build();

        Team team4 = Team.builder()
                .name("Team D")
                .description("네 번째 팀")
                .build();

        teamRepository.save(team1);
        teamRepository.save(team2);
        teamRepository.save(team3);
        teamRepository.save(team4);

        System.out.println("초기 팀 데이터가 생성되었습니다.");
    }
}