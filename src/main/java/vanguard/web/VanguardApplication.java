package vanguard.web;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import vanguard.model.IncidentType;
import vanguard.util.DispatchCenter;
import vanguard.util.ResourceFactory;


@SpringBootApplication
public class VanguardApplication {
    public static void main(String[] args) {
        SpringApplication.run(VanguardApplication.class, args);
    }

    /**
     * A CommandLineRunner bean runs automatically once, right after
     * the app starts — this is where the resource fleet gets seeded,
     * the same way Main.seedResources() and MainFrame.seedResources()
     * do for the other two front ends.
     */
    @Bean
    public CommandLineRunner seedResources() {
        return args -> {
            DispatchCenter dc = DispatchCenter.getInstance();
            dc.registerResource(ResourceFactory.create(IncidentType.FIRE, "r1", "Engine 12", 60, 60));
            dc.registerResource(ResourceFactory.create(IncidentType.MEDICAL, "r2", "Medic 3", 120, 90));
            dc.registerResource(ResourceFactory.create(IncidentType.STRUCTURAL, "r3", "Rescue Alpha", 200, 130));
            dc.registerResource(ResourceFactory.create(IncidentType.FLOOD, "r4", "Flood Boat 2", 340, 40));
            dc.registerResource(ResourceFactory.create(IncidentType.OTHER, "r5", "Volunteer Unit A", 180, 220));
            System.out.println("VANGUARD REST API: seeded 5 resources.");
        };
    }
}
