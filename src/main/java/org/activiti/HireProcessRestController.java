package org.activiti;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class HireProcessRestController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/start-hire-process", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void startHireProcess(@RequestBody Map<String, String> data) {

        Applicant applicant = new Applicant(data.get("name"), data.get("email"), data.get("phoneNumber"));
        applicantRepository.save(applicant);

        Map<String, Object> vars = Collections.<String, Object>singletonMap("applicant", applicant);
        runtimeService.startProcessInstanceByKey("hireProcessWithJpa", vars);
    }


    @ResponseStatus(value = HttpStatus.OK)
    //reject email
    @RequestMapping(value = "/reject-email", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public void rejectEmail(@RequestBody Map<String, String> data) {
        runtimeService.createMessageCorrelation("RejectEmail")
                .processInstanceId(data.get("processInstanceId"))
                .setVariable("email", data.get("email"))
                .correlate();
    }



    public List<HistoricActivityInstance> getAllProcessSteps(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();
    }

    public List<Task> getActiveTasks(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
    }

//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = "/start-hire-process-with-jpa", method = RequestMethod.POST,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public void startHireProcessWithJpa(@RequestBody Map<String, String> data) {
//
//            Applicant applicant = new Applicant(data.get("name"), data.get("email"), data.get("phoneNumber"));
//            applicantRepository.save(applicant);
//
//            Map<String, Object> vars = Collections.<String, Object>singletonMap("applicant", applicant);
//            runtimeService.startProcessInstanceByKey("hireProcessWithJpa", vars);
//    }
//
//    //get all tasks of a process instance
//    @ResponseStatus(value = HttpStatus.OK)
//    @RequestMapping(value = "/get-tasks", method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<Execution> getTasks() {
//        return runtimeService.createExecutionQuery().processInstanceId("hireProcess").list();
//
//    }


}