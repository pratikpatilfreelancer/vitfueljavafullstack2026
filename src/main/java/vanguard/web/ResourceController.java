package vanguard.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vanguard.model.Resource;
import vanguard.util.DispatchCenter;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final DispatchCenter dc = DispatchCenter.getInstance();

    @GetMapping
    public List<Resource> listAll() {
        return dc.allResources();
    }
}
