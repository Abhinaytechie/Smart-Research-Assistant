package com.ai.research_assisant.controller;

import com.ai.research_assisant.entity.ResearchReq;
import com.ai.research_assisant.service.ResearchService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ResearchController {

    private final ResearchService researchService;
    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody ResearchReq req){
        String response=researchService.processContent(req);
        return ResponseEntity.ok(response);
    }
}
