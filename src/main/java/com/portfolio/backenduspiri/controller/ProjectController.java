package com.portfolio.backenduspiri.controller;

import com.portfolio.backenduspiri.model.Project;
import com.portfolio.backenduspiri.service_interface.IPersonService;
import com.portfolio.backenduspiri.service_interface.IProjectService;
import com.portfolio.backenduspiri.services.CloudinaryService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/project")
@CrossOrigin( origins = "https://uriel-spiridione.web.app/" )
public class ProjectController {
    
    @Autowired
    private IProjectService projectService;
    
    @Autowired
    private IPersonService personService;
    
    @Autowired
    CloudinaryService cloudinaryService;
    
    @GetMapping
    @ResponseBody
    public List<Project> getProject(){
        return projectService.getProjects();
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public Project getProject( @PathVariable Long id ){
        return projectService.getProject( id );
    }
    
    @GetMapping("person/{id}")
    public List<Project> getProjectByPersonId( @PathVariable Long id ){
        return projectService.findByPersonId(id);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public Project saveProject( @PathVariable Long id, @RequestBody Project project ){
        project.setPerson(personService.getPerson(id));
        projectService.createProject(project);
        return projectService.getProject(project.getId());
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Project updateProject( @PathVariable Long id, @RequestBody Project project ){
        Project projectToUpdate = projectService.getProject(id);
        
        projectToUpdate.setName(project.getName());
        projectToUpdate.setDescription(project.getDescription());
        projectToUpdate.setImg_url(project.getImg_url());
        projectToUpdate.setLink(project.getLink());
        projectToUpdate.setEnabled_link(project.getEnabled_link());
        
        return projectService.updateProject(projectToUpdate);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/image")
    public Project updateProjectImage( @PathVariable Long id, @RequestParam("project") MultipartFile edu ) throws IOException{
        Project projectToUpdate = projectService.getProject(id);
        
        cloudinaryService.delete(projectToUpdate.getImageId());
        
        Map result = cloudinaryService.upload(edu);
        projectToUpdate.setImg_url(result.get("secure_url").toString());
        projectToUpdate.setImageId(result.get("public_id").toString());
        
        return projectService.updateProject(projectToUpdate);
        
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteProject( @PathVariable Long id ) throws IOException{
        cloudinaryService.delete(projectService.getProject(id).getImageId());
        projectService.deleteProject(id);
    }
    
}
